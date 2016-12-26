package com.lostjs.wx4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.lostjs.wx4j.utils.WxNickNameConverter;

/**
 * Created by pw on 02/10/2016.
 */
public class GroupMember {

    private long uin;

    private String userName;

    private String nickName;

    private long attrStatus;

    private String pyInitial;

    private String pyQuanPin;

    private String remarkPYInitial;

    private String remarkPYQuanPin;

    private int memberStatus;

    private String displayName;

    private String keyword;

    public long getUin() {
        return uin;
    }

    @JsonSetter("Uin")
    public void setUin(long uin) {
        this.uin = uin;
    }

    public String getUserName() {
        return userName;
    }

    @JsonSetter("UserName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    @JsonSetter("NickName")
    public void setNickName(String nickName) {
        this.nickName = WxNickNameConverter.convertFromHtml(nickName);
    }

    public long getAttrStatus() {
        return attrStatus;
    }

    @JsonSetter("AttrStatus")
    public void setAttrStatus(long attrStatus) {
        this.attrStatus = attrStatus;
    }

    public String getPyInitial() {
        return pyInitial;
    }

    @JsonSetter("PYInitial")
    public void setPyInitial(String pyInitial) {
        this.pyInitial = pyInitial;
    }

    public String getPyQuanPin() {
        return pyQuanPin;
    }

    @JsonSetter("PYQuanPin")
    public void setPyQuanPin(String pyQuanPin) {
        this.pyQuanPin = pyQuanPin;
    }

    public String getRemarkPYInitial() {
        return remarkPYInitial;
    }

    @JsonSetter("RemarkPYInitial")
    public void setRemarkPYInitial(String remarkPYInitial) {
        this.remarkPYInitial = remarkPYInitial;
    }

    public String getRemarkPYQuanPin() {
        return remarkPYQuanPin;
    }

    @JsonSetter("RemarkPYQuanPin")
    public void setRemarkPYQuanPin(String remarkPYQuanPin) {
        this.remarkPYQuanPin = remarkPYQuanPin;
    }

    public int getMemberStatus() {
        return memberStatus;
    }

    @JsonSetter("MemberStatus")
    public void setMemberStatus(int memberStatus) {
        this.memberStatus = memberStatus;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonSetter("DisplayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getKeyword() {
        return keyword;
    }

    @JsonSetter("KeyWord")
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
