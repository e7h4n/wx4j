package com.lostjs.wxbot4j.data.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.lostjs.wxbot4j.data.SyncKey;

import java.util.List;

/**
 * Created by pw on 02/10/2016.
 */
public class SyncKeyContainer {

    private int count;

    private List<SyncKey> list;

    @JsonGetter("Count")
    public int getCount() {
        return count;
    }

    @JsonSetter("Count")
    public void setCount(int count) {
        this.count = count;
    }

    @JsonGetter("List")
    public List<SyncKey> getList() {
        return list;
    }

    @JsonSetter("List")
    public void setList(List<SyncKey> list) {
        this.list = list;
    }
}
