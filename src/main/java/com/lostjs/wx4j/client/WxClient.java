package com.lostjs.wx4j.client;

import com.lostjs.wx4j.context.WxContextSource;
import com.lostjs.wx4j.data.response.Contact;
import com.lostjs.wx4j.data.response.GroupMember;
import com.lostjs.wx4j.transporter.WxTransporter;

import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public interface WxClient {

    void setContextSource(WxContextSource contextSource);

    void setTransporter(WxTransporter transporter);

    List<Contact> getContacts();

    List<GroupMember> getGroupMembers(String groupUserName);

    List<Contact> getContactsByUserNames(List<String> userNames);

    void syncCheckLoop(String validHost);

    String statusNotify();

    boolean addContact(String userName, String reason);

    boolean updateRemarkName(String userName, String remarkName);

    void syncCheckLoop();
}
