package com.lostjs.wxbot4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by pw on 02/10/2016.
 */
public class User {

    private String userName;

    public String getUserName() {
        return userName;
    }

    @JsonSetter("UserName")
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
