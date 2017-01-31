package com.lostjs.wx4j.client;

import com.lostjs.wx4j.context.FileWxContext;
import com.lostjs.wx4j.context.QRCodeWxContextSource;
import com.lostjs.wx4j.context.WxContext;
import com.lostjs.wx4j.context.WxContextSource;
import com.lostjs.wx4j.data.response.Contact;
import com.lostjs.wx4j.data.response.GroupMember;
import com.lostjs.wx4j.test.TestHelper;
import com.lostjs.wx4j.transporter.BasicWxTransporter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Created by pw on 02/10/2016.
 */
public class BasicWxClientTest {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private WxClient client;

    private BasicWxTransporter transporter;
    private WxContext wxContext;

    /**
     * use QRCodeWxContextSourceTest to init context
     */
    @Before
    public void setUp() throws Exception {
        System.setProperty("jsse.enableSNIExtension", "false");

        LOG.info("context file: {}", TestHelper.getTmpFilePath());
        wxContext = new FileWxContext(TestHelper.getTmpFilePath());
        transporter = new BasicWxTransporter(wxContext);
        WxContextSource wxContextSource = new QRCodeWxContextSource(transporter);

        client = new BasicWxClient();
        client.setTransporter(transporter);
        client.setContextSource(wxContextSource);
    }

    @Test
    @Ignore
    public void statusNotify() throws Exception {
        String msgId = client.statusNotify();

        LOG.info("msgId: {}", msgId);

        Assert.assertTrue(StringUtils.isNotBlank(msgId));
    }

    @Test
    @Ignore
    public void startEventLoop() throws Exception {
        client.syncCheckLoop();
    }

    @Test
    @Ignore
    public void getGroupContacts() throws Exception {
        List<Contact> contactList = client.getContacts();
        Optional<Contact> targetContact = contactList.stream().filter(
                c -> c.getNickName().equals("翡丽铂庭 业主之家")).findAny();
        Assert.assertTrue(targetContact.isPresent());

        List<GroupMember> groupMembers = client.getGroupMembers(targetContact.get().getUserName());
        groupMembers.forEach(g -> System.out.println(g.getNickName()));
    }

    @Test
    @Ignore
    public void getContacts() throws Exception {
        List<Contact> contactList = client.getContacts();
        Optional<Contact> targetContact = contactList.stream().filter(
                c -> c.getNickName().equals("蓝小盒")).findAny();
        Assert.assertTrue(targetContact.isPresent());

        String url = String.format("%s&type=big", targetContact.get().getHeadImgUrl().replace("/cgi-bin/mmwebwx-bin", ""));

        InputStream is = transporter.getBinary(url);

        String md5Hex = DigestUtils.md5Hex(is);
        LOG.info("md5hex: {}", md5Hex);
    }
}