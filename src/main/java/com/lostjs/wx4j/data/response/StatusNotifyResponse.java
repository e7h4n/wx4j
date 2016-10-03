package com.lostjs.wx4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by pw on 03/10/2016.
 */
public class StatusNotifyResponse implements WxResponse {

    private BaseResponse baseResponse;

    private String msgId;

    @Override
    public BaseResponse getBaseResponse() {
        return baseResponse;
    }

    @JsonSetter("BaseResponse")
    public void setBaseResponse(BaseResponse baseResponse) {
        this.baseResponse = baseResponse;
    }

    public String getMsgId() {
        return msgId;
    }

    @JsonSetter("MsgID")
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
