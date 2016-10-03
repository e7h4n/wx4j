package com.lostjs.wx4j.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by pw on 02/10/2016.
 */
public class SyncKey {

    private int key;

    private int value;

    @JsonGetter("Key")
    public int getKey() {
        return key;
    }

    @JsonSetter("Key")
    public void setKey(int key) {
        this.key = key;
    }

    @JsonGetter("Val")
    public int getValue() {
        return value;
    }

    @JsonSetter("Val")
    public void setValue(int value) {
        this.value = value;
    }
}
