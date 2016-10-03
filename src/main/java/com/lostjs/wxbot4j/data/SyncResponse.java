package com.lostjs.wxbot4j.data;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.lostjs.wxbot4j.data.response.BaseResponse;
import com.lostjs.wxbot4j.data.response.SyncKeyContainer;
import com.lostjs.wxbot4j.data.response.WxResponse;

/**
 * Created by pw on 03/10/2016.
 */
public class SyncResponse implements WxResponse {

    private BaseResponse baseResponse;

    private SyncKeyContainer syncKeyContainer;

    @Override
    public BaseResponse getBaseResponse() {
        return baseResponse;
    }

    @JsonSetter("BaseResponse")
    public void setBaseResponse(BaseResponse baseResponse) {
        this.baseResponse = baseResponse;
    }

    public SyncKeyContainer getSyncKeyContainer() {
        return syncKeyContainer;
    }

    @JsonSetter("SyncKey")
    public void setSyncKeyContainer(SyncKeyContainer syncKeyContainer) {
        this.syncKeyContainer = syncKeyContainer;
    }
}
