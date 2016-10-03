package com.lostjs.wxbot4j.transporter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lostjs.wxbot4j.context.WxContext;
import com.lostjs.wxbot4j.data.response.WxResponse;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;

/**
 * Created by pw on 02/10/2016.
 */
public interface WxTransporter {

    WxContext getWxContext();

    String execute(String api);

    String execute(String api, List<NameValuePair> params);

    String execute(String api, Map<String, Object> dataMap);

    <T extends WxResponse> T execute(String api, TypeReference<T> responseType);

    <T extends WxResponse> T execute(String api, Map<String, Object> dataMap, TypeReference<T> responseType);

    <T extends WxResponse> T execute(String api, List<NameValuePair> params, TypeReference<T> responseType);

    <T extends WxResponse> T execute(String api, List<NameValuePair> params, Map<String, Object> dataMap,
                                     TypeReference<T> responseType);

    String execute(String api, List<NameValuePair> params, Map<String, Object> dataMap);
}
