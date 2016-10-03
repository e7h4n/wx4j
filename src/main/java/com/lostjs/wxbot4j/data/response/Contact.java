package com.lostjs.wxbot4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.lostjs.wxbot4j.utils.WxNickNameConverter;

import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public class Contact {

    private int uin;

    private String userName;

    private String nickName;

    private String headImgUrl;

    private int contactFlag;

    private String remarkName;

    private int hideInputBarFlag;

    private int Sex;

    private String signature;

    private int verifyFlag;

    private int ownerUin;

    private String pyInitial;

    private String pyQuanPin;

    private String remarkPyInitial;

    private String remarkPyQuanPin;

    private int starFriend;

    private int appAccountFlag;

    private int statues;

    private int attrStatus;

    private String Province;

    private String City;

    private String alias;

    private int snsFlag;

    private int uniFriend;

    private String displayName;

    private int chatRoomId;

    private String keyword;

    private String encryChatRoomId;

    private int memberCount;

    private List<GroupMember> memberList;

    public int getUin() {
        return uin;
    }

    @JsonSetter("Uin")
    public void setUin(int uin) {
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

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    @JsonSetter("HeadImgUrl")
    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getContactFlag() {
        return contactFlag;
    }

    @JsonSetter("ContactFlag")
    public void setContactFlag(int contactFlag) {
        this.contactFlag = contactFlag;
    }

    public String getRemarkName() {
        return remarkName;
    }

    @JsonSetter("RemarkName")
    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public int getHideInputBarFlag() {
        return hideInputBarFlag;
    }

    @JsonSetter("HideInputBarFlag")
    public void setHideInputBarFlag(int hideInputBarFlag) {
        this.hideInputBarFlag = hideInputBarFlag;
    }

    public int getSex() {
        return Sex;
    }

    @JsonSetter("Sex")
    public void setSex(int sex) {
        Sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    @JsonSetter("Signature")
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getVerifyFlag() {
        return verifyFlag;
    }

    @JsonSetter("VerifyFlag")
    public void setVerifyFlag(int verifyFlag) {
        this.verifyFlag = verifyFlag;
    }

    public int getOwnerUin() {
        return ownerUin;
    }

    @JsonSetter("OwnerUin")
    public void setOwnerUin(int ownerUin) {
        this.ownerUin = ownerUin;
    }

    public int getStarFriend() {
        return starFriend;
    }

    @JsonSetter("StarFriend")
    public void setStarFriend(int starFriend) {
        this.starFriend = starFriend;
    }

    public int getAppAccountFlag() {
        return appAccountFlag;
    }

    @JsonSetter("AppAccountFlag")
    public void setAppAccountFlag(int appAccountFlag) {
        this.appAccountFlag = appAccountFlag;
    }

    public int getStatues() {
        return statues;
    }

    @JsonSetter("Statues")
    public void setStatues(int statues) {
        this.statues = statues;
    }

    public int getAttrStatus() {
        return attrStatus;
    }

    @JsonSetter("AttrStatus")
    public void setAttrStatus(int attrStatus) {
        this.attrStatus = attrStatus;
    }

    public String getProvince() {
        return Province;
    }

    @JsonSetter("Province")
    public void setProvince(String province) {
        Province = province;
    }

    public String getCity() {
        return City;
    }

    @JsonSetter("City")
    public void setCity(String city) {
        City = city;
    }

    public String getAlias() {
        return alias;
    }

    @JsonSetter("Alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getSnsFlag() {
        return snsFlag;
    }

    @JsonSetter("SnsFlag")
    public void setSnsFlag(int snsFlag) {
        this.snsFlag = snsFlag;
    }

    public int getUniFriend() {
        return uniFriend;
    }

    @JsonSetter("UniFriend")
    public void setUniFriend(int uniFriend) {
        this.uniFriend = uniFriend;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonSetter("DisplayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    @JsonSetter("ChatRoomId")
    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getKeyword() {
        return keyword;
    }

    @JsonSetter("KeyWord")
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getEncryChatRoomId() {
        return encryChatRoomId;
    }

    @JsonSetter("EncryChatRoomId")
    public void setEncryChatRoomId(String encryChatRoomId) {
        this.encryChatRoomId = encryChatRoomId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    @JsonSetter("MemberCount")
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public List<GroupMember> getMemberList() {
        return memberList;
    }

    @JsonSetter("MemberList")
    public void setMemberList(List<GroupMember> memberList) {
        this.memberList = memberList;
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

    public String getRemarkPyInitial() {
        return remarkPyInitial;
    }

    @JsonSetter("RemarkPYInitial")
    public void setRemarkPyInitial(String remarkPyInitial) {
        this.remarkPyInitial = remarkPyInitial;
    }

    public String getRemarkPyQuanPin() {
        return remarkPyQuanPin;
    }

    @JsonSetter("RemarkPYQuanPin")
    public void setRemarkPyQuanPin(String remarkPyQuanPin) {
        this.remarkPyQuanPin = remarkPyQuanPin;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "uin=" + uin +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", contactFlag=" + contactFlag +
                ", remarkName='" + remarkName + '\'' +
                ", hideInputBarFlag=" + hideInputBarFlag +
                ", Sex=" + Sex +
                ", signature='" + signature + '\'' +
                ", verifyFlag=" + verifyFlag +
                ", ownerUin=" + ownerUin +
                ", pyInitial='" + pyInitial + '\'' +
                ", pyQuanPin='" + pyQuanPin + '\'' +
                ", remarkPyInitial='" + remarkPyInitial + '\'' +
                ", remarkPyQuanPin='" + remarkPyQuanPin + '\'' +
                ", starFriend=" + starFriend +
                ", appAccountFlag=" + appAccountFlag +
                ", statues=" + statues +
                ", attrStatus=" + attrStatus +
                ", Province='" + Province + '\'' +
                ", City='" + City + '\'' +
                ", alias='" + alias + '\'' +
                ", snsFlag=" + snsFlag +
                ", uniFriend=" + uniFriend +
                ", displayName='" + displayName + '\'' +
                ", chatRoomId=" + chatRoomId +
                ", keyword='" + keyword + '\'' +
                ", encryChatRoomId='" + encryChatRoomId + '\'' +
                ", memberCount=" + memberCount +
                ", memberList=" + memberList +
                '}';
    }
}
