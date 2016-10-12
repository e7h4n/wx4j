package com.lostjs.wx4j.data.response;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by pw on 03/10/2016.
 */
public enum SyncCheckSelector {

    NORMAL(0),

    NEW_MESSAGE(2),

    CONTACT(4);

    private final int code;

    SyncCheckSelector(int code) {
        this.code = code;
    }

    public static Optional<SyncCheckSelector> findByInt(int selector) {
        return Arrays.stream(SyncCheckSelector.values()).filter(s -> s.getCode() == selector).findFirst();
    }

    public int getCode() {
        return code;
    }
}
