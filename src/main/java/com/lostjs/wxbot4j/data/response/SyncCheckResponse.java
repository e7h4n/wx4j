package com.lostjs.wxbot4j.data.response;

/**
 * Created by pw on 03/10/2016.
 */
public class SyncCheckResponse {

    private int retcode;

    private SyncCheckSelector selector;

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public SyncCheckSelector getSelector() {
        return selector;
    }

    public void setSelector(SyncCheckSelector selector) {
        this.selector = selector;
    }
}
