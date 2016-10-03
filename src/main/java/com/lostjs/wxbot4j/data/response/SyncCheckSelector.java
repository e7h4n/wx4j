package com.lostjs.wxbot4j.data.response;

/**
 * Created by pw on 03/10/2016.
 */
public enum SyncCheckSelector {

    NORMAL(0),

    NEW_MESSAGE(1),

    CHATROOM_CHANGE(7);

    private final int code;

    SyncCheckSelector(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SyncCheckSelector findByInt(int selector) {
        return null;
    }
}
