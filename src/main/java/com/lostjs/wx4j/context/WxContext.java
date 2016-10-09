package com.lostjs.wx4j.context;

import com.lostjs.wx4j.data.SyncKey;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public interface WxContext extends CookieStore {

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

    List<SyncKey> getSyncKeys();

    void setSyncKeys(List<SyncKey> syncKeys);

    String getUserName();

    void setUserName(String userName);

    @Override
    List<Cookie> getCookies();

    void setCookies(List<Cookie> cookies);
}
