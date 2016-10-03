package com.lostjs.wx4j.context;

import com.lostjs.wx4j.data.SyncKey;

import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public interface WxContext {

    String getSkey();

    void setSkey(String skey);

    String getSid();

    void setSid(String sid);

    String getUin();

    void setUin(String uin);

    String getPassTicket();

    void setPassTicket(String passTicket);

    String getBaseUrl();

    void setBaseUrl(String baseUrl);

    String getDeviceId();

    void setDeviceId(String deviceId);

    List<SyncKey> getSyncKeys();

    void setSyncKeys(List<SyncKey> syncKeys);

    String getUserName();

    void setUserName(String userName);
}
