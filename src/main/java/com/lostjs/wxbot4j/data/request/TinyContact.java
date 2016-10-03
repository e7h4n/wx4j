package com.lostjs.wxbot4j.data.request;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * Created by pw on 02/10/2016.
 */
public class TinyContact {

    private String userName;

    private String encryChatRoomId;

    @JsonGetter("UserName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonGetter("EncryChatRoomId")
    public String getEncryChatRoomId() {
        return encryChatRoomId;
    }

    public void setEncryChatRoomId(String encryChatRoomId) {
        this.encryChatRoomId = encryChatRoomId;
    }
}
