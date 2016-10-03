package com.lostjs.wxbot4j.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lostjs.wxbot4j.context.WxContext;
import com.lostjs.wxbot4j.data.SyncResponse;
import com.lostjs.wxbot4j.data.request.TinyContact;
import com.lostjs.wxbot4j.data.response.*;
import com.lostjs.wxbot4j.transporter.BasicWxTransporter;
import com.lostjs.wxbot4j.utils.HttpUtil;
import com.lostjs.wxbot4j.utils.WxSyncKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private BasicWxTransporter transporter;

    @Override
    public void setTransporter(BasicWxTransporter transporter) {
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
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Count", 1);
        List<TinyContact> contacts = new ArrayList<>();
        TinyContact group = new TinyContact();
        group.setUserName(groupUserName);
        group.setEncryChatRoomId(StringUtils.EMPTY);
        contacts.add(group);
        dataMap.put("List", contacts);
        GroupMemberResponse memberResponse = transporter.execute(API_BATCH_GET_CONTACT, dataMap,
                new TypeReference<GroupMemberResponse>() {
                });

        return memberResponse.getContacts().get(0).getMemberList();
    }

    @Override
    public void startEventLoop() {
        if (StringUtils.isEmpty(getContext().getUserName())) {
            init();
        }

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
        new Thread(() -> {
            while (true) {
                SyncCheckResponse syncCheckResponse = syncComet(finalValidHost);

                if (syncCheckResponse.getRetcode() != 0) {
                    throw new RuntimeException("invalid synccheck retcode, retcode=" + syncCheckResponse.getRetcode());
                }

                sync();
            }
        });
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

    private long getClientMessageId() {
        return System.currentTimeMillis() / 1000;
    }

    private void sync() {
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
        WxContext context = getContext();
        URI uri;
        try {
            uri = new URIBuilder(getPushApi(host))
                    .addParameter(REQUEST_FIELD_RANDOM_FOR_PUSH, String.valueOf(System.currentTimeMillis()))
                    .addParameter(REQUEST_FIELD_SID_FOR_PUSH, context.getSid())
                    .addParameter(REQUEST_FIELD_UIN_FOR_PUSH, context.getUin())
                    .addParameter(REQUEST_FIELD_SKEY_FOR_PUSH, context.getSkey())
                    .addParameter(REQUEST_FIELD_DEVICE_ID_FOR_PUSH, context.getDeviceId())
                    .addParameter(REQUEST_FIELD_SYNC_KEY_FOR_PUSH, WxSyncKeyUtil.format(context.getSyncKeys())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String response = HttpUtil.execute(uri, new HttpGet(uri), HttpClientBuilder.create().build());

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
