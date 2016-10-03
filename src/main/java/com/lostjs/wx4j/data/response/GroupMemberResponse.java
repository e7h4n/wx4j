package com.lostjs.wx4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public class GroupMemberResponse implements WxResponse {

    private BaseResponse baseResponse;

    private int count;

    private List<Contact> contacts;

    public BaseResponse getBaseResponse() {
        return baseResponse;
    }

    @JsonSetter("BaseResponse")
    public void setBaseResponse(BaseResponse baseResponse) {
        this.baseResponse = baseResponse;
    }

    public int getCount() {
        return count;
    }

    @JsonSetter("Count")
    public void setCount(int count) {
        this.count = count;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    @JsonSetter("ContactList")
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
