package com.lostjs.wxbot4j.client;

import com.lostjs.wxbot4j.data.response.Contact;
import com.lostjs.wxbot4j.data.response.GroupMember;
import com.lostjs.wxbot4j.transporter.BasicWxTransporter;

import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public interface WxClient {

    void setTransporter(BasicWxTransporter transporter);

    List<Contact> getContacts();

    List<GroupMember> getGroupMembers(String groupUserName);

    void startEventLoop();

    String statusNotify();
}
