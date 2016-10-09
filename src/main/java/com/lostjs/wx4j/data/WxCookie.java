package com.lostjs.wx4j.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.http.impl.cookie.BasicClientCookie2;

/**
 * Created by pw on 09/10/2016.
 */
public class WxCookie extends BasicClientCookie2 {

    /**
     * Default Constructor taking a name and a value. The value may be null.
     *
     * @param name  The name.
     * @param value The value.
     */
    public WxCookie(@JsonProperty("name") String name, @JsonProperty("value") String value) {
        super(name, value);
    }
}
