package com.lostjs.wx4j.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lostjs.wx4j.context.WxContext;
import com.lostjs.wx4j.data.SyncResponse;
import com.lostjs.wx4j.data.request.BaseRequest;
import com.lostjs.wx4j.data.request.TinyContact;
import com.lostjs.wx4j.data.response.*;
import com.lostjs.wx4j.transporter.WxTransporter;
import com.lostjs.wx4j.utils.HttpUtil;
import com.lostjs.wx4j.utils.WxSyncKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by pw on 02/10/2016.
 */
public class BasicWxClient implements WxClient {

    private static final int MYSTERIOUS_CODE = 3; // 我也不知道这玩意干嘛的，但是就是需要这么个东西

    private static final String API_GET_CONTACT = "/webwxgetcontact";

    private static final String API_BATCH_GET_CONTACT = "/webwxbatchgetcontact";

    private static final String API_STATUS_NOTIFY = "/webwxstatusnotify";

    private static final String API_SYNC = "/webwxsync";

    private static final String API_INIT = "/webwxinit";

    private static final String REQUEST_FIELD_CODE = "Code";

    private static final String REQUEST_FIELD_FROM_USER_NAME = "FromUserName";

    private static final String REQUEST_FIELD_TO_USER_NAME = "ToUserName";

    private static final String REQUEST_FIELD_CLIENT_MESSAGE_ID = "ClientMsgId";

    private static final String REQUEST_FIELD_SYNC_KEY = "SyncKey";

    private static final String REQUEST_FIELD_RANDOM_RANDOM = "rr";

    private static final String REQUEST_FIELD_RANDOM_FOR_PUSH = "r";

    private static final String REQUEST_FIELD_SID_FOR_PUSH = "sid";

    private static final String REQUEST_FIELD_UIN_FOR_PUSH = "uin";

    private static final String REQUEST_FIELD_SKEY_FOR_PUSH = "skey";

    private static final String REQUEST_FIELD_DEVICE_ID_FOR_PUSH = "deviceid";

    private static final String REQUEST_FIELD_SYNC_KEY_FOR_PUSH = "synckey";

    private static final String[] PUSH_HOST_LIST = new String[]{"webpush.weixin.qq.com", "webpush2.weixin.qq.com"};

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private WxTransporter transporter;

    @Override
    public void setTransporter(WxTransporter transporter) {
        this.transporter = transporter;
    }

    @Override
    public List<Contact> getContacts() {
        ContactResponse contactResponse = transporter.execute(API_GET_CONTACT, new TypeReference<ContactResponse>() {
        });

        return contactResponse.getContacts();
    }

    @Override
    public List<GroupMember> getGroupMembers(String groupUserName) {

        List<String> userNames = new ArrayList<>();
        userNames.add(groupUserName);

        List<Contact> contacts = getContactsByUserNames(userNames);

        return contacts.get(0).getMemberList();
    }

    @Override
    public List<Contact> getContactsByUserNames(List<String> userNames) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Count", userNames.size());
        List<TinyContact> tinyContacts = userNames.stream().map(n -> {
            TinyContact contact = new TinyContact();
            contact.setUserName(n);
            contact.setEncryChatRoomId(StringUtils.EMPTY);
            return contact;
        }).collect(Collectors.toList());
        dataMap.put("List", tinyContacts);
        ContactListResponse contactList = transporter.execute(API_BATCH_GET_CONTACT, dataMap,
                new TypeReference<ContactListResponse>() {
                });

        return contactList.getContacts();
    }

    @Override
    public void startEventLoop() {
        if (StringUtils.isEmpty(getContext().getUserName())) {
            init();
        }

        LOG.info("check valid comet host");
        String validHost = null;
        SyncCheckResponse checkResponse = null;
        for (String host : PUSH_HOST_LIST) {
            try {
                SyncCheckResponse syncCheckResponse = syncComet(host);
                if (syncCheckResponse.getRetcode() == 0) {
                    checkResponse = syncCheckResponse;
                    validHost = host;
                    continue;
                }
            } catch (RuntimeException e) {
                LOG.error("", e);
            }
        }

        if (checkResponse == null) {
            throw new RuntimeException("sync check error, all comet return error code 1100");
        }

        String finalValidHost = validHost;
        LOG.info("valid comet host: {}", validHost);
        new Thread(() -> {
            while (true) {
                SyncCheckResponse syncCheckResponse = null;
                try {
                    syncCheckResponse = syncComet(finalValidHost);
                } catch (RuntimeException e) {
                    LOG.error("exception when sync check", e);
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(3));
                    } catch (InterruptedException e1) {
                        throw new RuntimeException(e1);
                    }
                    continue;
                }

                if (syncCheckResponse.getRetcode() != 0) {
                    LOG.error("invalid synccheck retcode, retcode=" + syncCheckResponse.getRetcode());
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    LOG.info("synccheck retcode = {}", syncCheckResponse.getRetcode());
                }

                try {
                    sync();
                } catch (RuntimeException e) {
                    LOG.error("sync exception", e);
                }
            }
        }).start();
    }

    @Override
    public String statusNotify() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put(REQUEST_FIELD_CODE, MYSTERIOUS_CODE);
        dataMap.put(REQUEST_FIELD_FROM_USER_NAME, getContext().getUserName());
        dataMap.put(REQUEST_FIELD_TO_USER_NAME, getContext().getUserName());
        dataMap.put(REQUEST_FIELD_CLIENT_MESSAGE_ID, getClientMessageId());

        StatusNotifyResponse statusNotifyResponse = transporter.execute(API_STATUS_NOTIFY, dataMap,
                new TypeReference<StatusNotifyResponse>() {
                });

        return statusNotifyResponse.getMsgId();
    }

    @Override
    public boolean addContact(String userName, String reason) {
        int retryCount = 10;
        int[] retryDuration = {1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};

        AddContactResponse addContactResponse = null;
        for (int i = 0; i < retryCount; i++) {
            try {
                addContactResponse = internalAddContact(userName, reason);
            } catch (RuntimeException e) {
                LOG.warn("add contact got a invalid result", e);

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(retryDuration[i]));
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }

                continue;
            }

            return true;
        }

        assert addContactResponse != null;

        LOG.warn("can't add contact, Ret={}, ErrMsg={}", addContactResponse.getBaseResponse().getRet(),
                addContactResponse.getBaseResponse().getErrMsg());

        return false;
    }

    @Override
    public boolean updateRemarkName(String userName, String remarkName) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("CmdId", 2);
        dataMap.put("RemarkName", remarkName);
        dataMap.put("UserName", userName);

        transporter.execute("/webwxoplog", dataMap,
                new TypeReference<UpdateRemarkNameResponse>() {
                });

        return true;
    }

    private AddContactResponse internalAddContact(String userName, String reason) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Opcode", 2);
        dataMap.put("VerifyContent", reason);
        dataMap.put("skey", getContext().getSkey());
        dataMap.put("VerifyUserListSize", 1);
        ArrayList<Map<String, Object>> verifyUserList = new ArrayList<>();
        Map<String, Object> verifyUser = new HashMap<>();
        verifyUser.put("Value", userName);
        verifyUser.put("VerifyUserTicket", "");
        verifyUserList.add(verifyUser);
        dataMap.put("VerifyUserList", verifyUserList);
        dataMap.put("SceneListCount", 1);
        dataMap.put("SceneList", Collections.singletonList(33));
        return transporter.execute("/webwxverifyuser", dataMap,
                new TypeReference<AddContactResponse>() {
                });
    }

    private long getClientMessageId() {
        return System.currentTimeMillis() / 1000;
    }

    private void sync() {
        LOG.info("sync");
        if (StringUtils.isEmpty(getContext().getUserName())) {
            init();
        }

        Map<String, Object> dataMap = new HashMap<>();

        SyncKeyContainer container = new SyncKeyContainer();
        container.setCount(getContext().getSyncKeys().size());
        container.setList(getContext().getSyncKeys());
        dataMap.put(REQUEST_FIELD_SYNC_KEY, container);
        dataMap.put(REQUEST_FIELD_RANDOM_RANDOM, System.currentTimeMillis());

        SyncResponse syncResponse = transporter.execute(API_SYNC, dataMap, new TypeReference<SyncResponse>() {
        });

        getContext().setSyncKeys(syncResponse.getSyncKeyContainer().getList());
        LOG.debug(getContext().getSyncKeys().toString());
    }

    private SyncCheckResponse syncComet(String host) {
        LOG.info("sync comet to {}", host);
        WxContext context = getContext();
        BaseRequest baseRequest = new BaseRequest(context);
        URI uri;
        try {
            uri = new URIBuilder(getPushApi(host))
                    .addParameter(REQUEST_FIELD_RANDOM_FOR_PUSH, String.valueOf(System.currentTimeMillis()))
                    .addParameter(REQUEST_FIELD_SID_FOR_PUSH, baseRequest.getSid())
                    .addParameter(REQUEST_FIELD_UIN_FOR_PUSH, baseRequest.getUin())
                    .addParameter(REQUEST_FIELD_SKEY_FOR_PUSH, baseRequest.getSkey())
                    .addParameter(REQUEST_FIELD_DEVICE_ID_FOR_PUSH, baseRequest.getDeviceID())
                    .addParameter(REQUEST_FIELD_SYNC_KEY_FOR_PUSH, WxSyncKeyUtil.format(context.getSyncKeys())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String response = HttpUtil.execute(uri, new HttpGet(uri), HttpClientBuilder.create().setRetryHandler(
                new DefaultHttpRequestRetryHandler()).setServiceUnavailableRetryStrategy(
                new ServiceUnavailableRetryStrategy() {
                    @Override
                    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                        int statusCode = response.getStatusLine().getStatusCode();
                        return statusCode != 200 && executionCount < 5;
                    }

                    @Override
                    public long getRetryInterval() {
                        return 1000;
                    }
                }).build());

        Pattern pattern = Pattern.compile("window\\.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}");
        Matcher m = pattern.matcher(response);
        if (!m.find()) {
            throw new RuntimeException("unrecognized response, response=" + response);
        }

        int retcode = Integer.parseInt(m.group(1), 10);
        int selector = Integer.parseInt(m.group(2), 10);

        SyncCheckResponse checkResponse = new SyncCheckResponse();
        checkResponse.setRetcode(retcode);
        checkResponse.setSelector(SyncCheckSelector.findByInt(selector));

        return checkResponse;
    }

    private String getPushApi(String host) {
        return "https://" + host + "/cgi-bin/mmwebwx-bin/synccheck";
    }

    private void init() {
        InitResponse initResponse = transporter.execute(API_INIT, new TypeReference<InitResponse>() {
        });

        String userName = initResponse.getUser().getUserName();
        getContext().setUserName(userName);
        getContext().setSyncKeys(initResponse.getSyncKeyContainer().getList());
    }

    private WxContext getContext() {
        return transporter.getWxContext();
    }
}
