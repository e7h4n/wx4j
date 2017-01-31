package com.lostjs.wx4j.context;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lostjs.wx4j.data.SyncKey;
import com.lostjs.wx4j.data.WxCookie;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.Collections;
import java.util.Date;
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

    private List<SyncKey> syncKeys;

    private String userName;

    @JsonIgnore
    private CookieStore cookieStore = new BasicCookieStore();

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

    @Override
    @JsonGetter("cookies")
    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

    @Override
    @JsonSetter("cookies")
    @JsonDeserialize(contentAs = WxCookie.class)
    public void setCookies(List<Cookie> cookies) {
        cookies.forEach(this::addCookie);
    }

    @Override
    public String toString() {
        return "BasicWxContext{" +
                "skey='" + skey + '\'' +
                ", sid='" + sid + '\'' +
                ", uin='" + uin + '\'' +
                ", passTicket='" + passTicket + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", syncKeys=" + syncKeys +
                ", userName='" + userName + '\'' +
                ", cookieStore=" + cookieStore +
                '}';
    }

    @Override
    @JsonIgnore
    public void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    @Override
    @JsonIgnore
    public boolean clearExpired(Date date) {
        return cookieStore.clearExpired(date);
    }

    @Override
    @JsonIgnore
    public void clear() {
        this.skey = null;
        this.sid = null;
        this.uin = null;
        this.passTicket = null;
        this.baseUrl = null;
        this.syncKeys = Collections.emptyList();
        this.userName = null;

        cookieStore.clear();
    }
}
