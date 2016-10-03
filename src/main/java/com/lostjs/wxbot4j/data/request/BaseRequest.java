package com.lostjs.wxbot4j.data.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.lostjs.wxbot4j.context.WxContext;

/**
 * Created by pw on 02/10/2016.
 */
public class BaseRequest {

    private final String uin;

    private final String sid;

    private final String skey;

    private final String deviceID;

    public BaseRequest(WxContext context) {
        uin = context.getUin();
        sid = context.getSid();
        skey = context.getSkey();
        deviceID = context.getDeviceId();
    }

    @JsonGetter("Uin")
    public String getUin() {
        return uin;
    }

    @JsonGetter("Sid")
    public String getSid() {
        return sid;
    }

    @JsonGetter("Skey")
    public String getSkey() {
        return skey;
    }

    @JsonGetter("DeviceId")
    public String getDeviceID() {
        return deviceID;
    }
}
