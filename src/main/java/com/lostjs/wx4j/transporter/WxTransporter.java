package com.lostjs.wx4j.transporter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lostjs.wx4j.context.WxContext;
import com.lostjs.wx4j.data.response.WxResponse;
import org.apache.http.NameValuePair;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by pw on 02/10/2016.
 */
public interface WxTransporter {

    WxContext getContext();

    InputStream getBinary(String api);

    InputStream getBinary(String api, List<NameValuePair> params);

    String get(String api);

    String get(String api, List<NameValuePair> params);

    <T extends WxResponse> T get(String api, List<NameValuePair> params, TypeReference<T> responseType);

    <T extends WxResponse> T get(String api, TypeReference<T> responseType);

    String post(String api);

    String post(String api, List<NameValuePair> params);

    String post(String api, Map<String, Object> dataMap);

    <T extends WxResponse> T post(String api, TypeReference<T> responseType);

    <T extends WxResponse> T post(String api, Map<String, Object> dataMap, TypeReference<T> responseType);

    <T extends WxResponse> T post(String api, List<NameValuePair> params, TypeReference<T> responseType);

    <T extends WxResponse> T post(String api, List<NameValuePair> params, Map<String, Object> dataMap,
                                  TypeReference<T> responseType);

    String post(String api, List<NameValuePair> params, Map<String, Object> dataMap);
}
