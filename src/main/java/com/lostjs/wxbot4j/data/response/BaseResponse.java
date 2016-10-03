package com.lostjs.wxbot4j.data.response;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by pw on 02/10/2016.
 */
public class BaseResponse {

    private int ret;

    private String errMsg;

    public int getRet() {
        return ret;
    }

    @JsonSetter("Ret")
    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getErrMsg() {
        return errMsg;
    }

    @JsonSetter("ErrMsg")
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
