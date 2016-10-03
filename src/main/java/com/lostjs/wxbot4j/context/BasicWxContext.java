package com.lostjs.wxbot4j.context;

import com.lostjs.wxbot4j.data.SyncKey;

import java.util.Collections;
import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public class BasicWxContext implements WxContext {

    private String skey;

    private String sid;

    private String uin;

    private String passTicket;

    private String baseUrl;

    private String deviceId;

    private List<SyncKey> syncKeys;

    private String userName;

    @Override
    public String getSkey() {
        return skey;
    }

    @Override
    public void setSkey(String skey) {
        this.skey = skey;
    }

    @Override
    public String getSid() {
        return sid;
    }

    @Override
    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String getUin() {
        return uin;
    }

    @Override
    public void setUin(String uin) {
        this.uin = uin;
    }

    @Override
    public String getPassTicket() {
        return passTicket;
    }

    @Override
    public void setPassTicket(String passTicket) {
        this.passTicket = passTicket;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "BasicWxContext{" +
                "skey='" + skey + '\'' +
                ", sid='" + sid + '\'' +
                ", uin='" + uin + '\'' +
                ", passTicket='" + passTicket + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }

    @Override
    public List<SyncKey> getSyncKeys() {
        return syncKeys == null ? Collections.emptyList() : syncKeys;
    }

    @Override
    public void setSyncKeys(List<SyncKey> syncKeys) {
        this.syncKeys = syncKeys;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
