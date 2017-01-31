package com.lostjs.wx4j.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostjs.wx4j.data.SyncKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public abstract class AbstractPersistentWxContext implements WxContext {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected WxContext internalContext;

    private boolean loaded = false;

    public AbstractPersistentWxContext() {
        this.internalContext = new BasicWxContext();
    }

    @Override
    public String getSkey() {
        lazyLoad();
        return internalContext.getSkey();
    }

    @Override
    public void setSkey(String skey) {
        lazyLoad();
        internalContext.setSkey(skey);
        write();
    }

    @Override
    public String getSid() {
        lazyLoad();
        return internalContext.getSid();
    }

    @Override
    public void setSid(String sid) {
        lazyLoad();
        internalContext.setSid(sid);
        write();
    }

    @Override
    public String getUin() {
        lazyLoad();
        return internalContext.getUin();
    }

    @Override
    public void setUin(String uin) {
        lazyLoad();
        internalContext.setUin(uin);
        write();
    }

    @Override
    public String getPassTicket() {
        lazyLoad();
        return internalContext.getPassTicket();
    }

    @Override
    public void setPassTicket(String passTicket) {
        lazyLoad();
        internalContext.setPassTicket(passTicket);
        write();
    }

    @Override
    public String getBaseUrl() {
        lazyLoad();
        return internalContext.getBaseUrl();
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        lazyLoad();
        internalContext.setBaseUrl(baseUrl);
        write();
    }

    @Override
    public List<SyncKey> getSyncKeys() {
        lazyLoad();
        return internalContext.getSyncKeys();
    }

    @Override
    public void setSyncKeys(List<SyncKey> syncKeys) {
        lazyLoad();
        internalContext.setSyncKeys(syncKeys);
        write();
    }

    @Override
    public String getUserName() {
        lazyLoad();
        return internalContext.getUserName();
    }

    @Override
    public void setUserName(String userName) {
        lazyLoad();
        internalContext.setUserName(userName);
        write();
    }

    @Override
    public List<Cookie> getCookies() {
        lazyLoad();
        return internalContext.getCookies();
    }

    @Override
    public void setCookies(List<Cookie> cookies) {
        lazyLoad();
        internalContext.setCookies(cookies);
        write();
    }

    @Override
    public void addCookie(Cookie cookie) {
        lazyLoad();
        internalContext.addCookie(cookie);
        write();
    }

    @Override
    public boolean clearExpired(Date date) {
        lazyLoad();
        boolean success = internalContext.clearExpired(date);
        if (success) {
            write();
        }

        return success;
    }

    @Override
    public void clear() {
        internalContext.clear();
        write();
        internalClear();
    }

    protected abstract void write();

    protected abstract void read();

    protected void loadFromString(String string) {
        if (StringUtils.isBlank(string)) {
            LOG.warn("skip load empty context");
            return;
        }

        WxContext webContext;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            webContext = objectMapper.readValue(string, BasicWxContext.class);
        } catch (IOException e) {
            LOG.error("can't load context, use empty context instead");
            LOG.debug("", e);
            return;
        }

        internalContext.setPassTicket(webContext.getPassTicket());
        internalContext.setBaseUrl(webContext.getBaseUrl());
        internalContext.setSid(webContext.getSid());
        internalContext.setSkey(webContext.getSkey());
        internalContext.setSyncKeys(webContext.getSyncKeys());
        internalContext.setUin(webContext.getUin());
        internalContext.setUserName(webContext.getUserName());
        internalContext.setCookies(webContext.getCookies());
    }

    protected String dumpToString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(internalContext);
    }

    protected abstract void internalClear();

    private void lazyLoad() {
        if (!loaded) {
            loaded = true;
            try {
                read();
            } catch (RuntimeException e) {
                clear();
                throw e;
            }
        }
    }
}
