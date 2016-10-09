package com.lostjs.wx4j.data.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.lostjs.wx4j.context.WxContext;
import org.apache.commons.lang3.RandomUtils;

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
        deviceID = randomDeviceId();
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

    private String randomDeviceId() {
        StringBuilder sb = new StringBuilder(16);
        sb.append("e");
        for (int i = 0; i < 15; i++) {
            sb.append(String.valueOf(RandomUtils.nextInt(0, 10)));
        }
        return sb.toString();
    }
}
