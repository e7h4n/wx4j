package com.lostjs.wxbot4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by pw on 02/10/2016.
 */
public class InitResponse implements WxResponse {

    private BaseResponse baseResponse;

    private SyncKeyContainer syncKeyContainer;

    private User user;

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

    public User getUser() {
        return user;
    }

    @JsonSetter("User")
    public void setUser(User user) {
        this.user = user;
    }
}
