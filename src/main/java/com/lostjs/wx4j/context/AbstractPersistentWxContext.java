package com.lostjs.wx4j.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostjs.wx4j.data.SyncKey;

import java.io.IOException;
import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public abstract class AbstractPersistentWxContext implements WxContext {

    protected WxContext internalContext;

    private boolean loaded = false;

    public AbstractPersistentWxContext() {
        this.internalContext = new BasicWxContext();
    }

    @Override
    public String getUserName() {
        lazyLoad();
        return internalContext.getUserName();
    }

    @Override
    public void setUserName(String userName) {
        internalContext.setUserName(userName);
        write();
    }

    @Override
    public String getSkey() {
        lazyLoad();
        return internalContext.getSkey();
    }

    @Override
    public void setSkey(String skey) {
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
        internalContext.setBaseUrl(baseUrl);
        write();
    }

    @Override
    public String getDeviceId() {
        lazyLoad();
        return internalContext.getDeviceId();
    }

    @Override
    public void setDeviceId(String deviceId) {
        internalContext.setDeviceId(deviceId);
        write();
    }

    @Override
    public List<SyncKey> getSyncKeys() {
        lazyLoad();
        return internalContext.getSyncKeys();
    }

    @Override
    public void setSyncKeys(List<SyncKey> syncKeys) {
        internalContext.setSyncKeys(syncKeys);
        write();
    }

    protected abstract void write();

    protected abstract void read();

    protected void loadFromString(String string) {
        WxContext webContext1;
        try {
            webContext1 = new ObjectMapper().readValue(string, BasicWxContext.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WxContext webContext = webContext1;
        internalContext.setPassTicket(webContext.getPassTicket());
        internalContext.setBaseUrl(webContext.getBaseUrl());
        internalContext.setDeviceId(webContext.getDeviceId());
        internalContext.setSid(webContext.getSid());
        internalContext.setSkey(webContext.getSkey());
        internalContext.setSyncKeys(webContext.getSyncKeys());
        internalContext.setUin(webContext.getUin());
        internalContext.setUserName(webContext.getUserName());
    }

    protected String dumpToString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(internalContext);
    }

    private void lazyLoad() {
        if (!loaded) {
            try {
                read();
            } catch (RuntimeException e) {
                clear();
                throw e;
            }
        }
    }

    protected abstract void clear();
}
