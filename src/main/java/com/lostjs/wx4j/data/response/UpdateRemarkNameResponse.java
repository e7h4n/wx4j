package com.lostjs.wx4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by pw on 05/10/2016.
 */
public class UpdateRemarkNameResponse implements WxResponse {

    private BaseResponse baseResponse;

    @Override
    public BaseResponse getBaseResponse() {
        return baseResponse;
    }

    @JsonSetter("BaseResponse")
    public void setBaseResponse(BaseResponse baseResponse) {
        this.baseResponse = baseResponse;
    }
}
