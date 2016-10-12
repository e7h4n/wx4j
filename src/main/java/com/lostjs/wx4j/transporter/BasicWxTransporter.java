package com.lostjs.wx4j.transporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostjs.wx4j.context.WxContext;
import com.lostjs.wx4j.data.request.BaseRequest;
import com.lostjs.wx4j.data.response.WxResponse;
import com.lostjs.wx4j.utils.HttpUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by pw on 02/10/2016.
 */
public class BasicWxTransporter implements WxTransporter {

    protected final WxContext wxContext;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpUtil httpUtil;

    public BasicWxTransporter(WxContext WxContext) {
        this.wxContext = WxContext;
    }

    @Override
    public WxContext getWxContext() {
        return wxContext;
    }

    @Override
    public String execute(String api) {
        return execute(api, Collections.emptyList(), Collections.emptyMap());
    }

    @Override
    public String execute(String api, List<NameValuePair> params) {
        return execute(api, params, Collections.emptyMap());
    }

    @Override
    public String execute(String api, Map<String, Object> dataMap) {
        return execute(api, new ArrayList<>(), dataMap);
    }

    @Override
    public <T extends WxResponse> T execute(String api, TypeReference<T> responseType) {
        return execute(api, Collections.emptyList(), Collections.emptyMap(), responseType);
    }

    @Override
    public <T extends WxResponse> T execute(String api, Map<String, Object> dataMap, TypeReference<T> responseType) {
        return execute(api, Collections.emptyList(), dataMap, responseType);
    }

    @Override
    public <T extends WxResponse> T execute(String api, List<NameValuePair> params, TypeReference<T> responseType) {
        return execute(api, params, Collections.emptyMap(), responseType);
    }

    @Override
    public <T extends WxResponse> T execute(String api, List<NameValuePair> params, Map<String, Object> dataMap,
                                            TypeReference<T> responseType) {
        String responseBody = execute(api, params, dataMap);

        LOG.debug("response: \n{}", responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T responseObject;
        try {
            responseObject = objectMapper.readValue(responseBody, responseType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        checkResponse(responseObject);
        return responseObject;
    }

    @Override
    public String execute(String api, List<NameValuePair> params, Map<String, Object> dataMap) {
        String url = wxContext.getBaseUrl() + api;
        List<NameValuePair> targetParams = new ArrayList<>();
        targetParams.addAll(params);
        targetParams.add(new BasicNameValuePair("r", String.valueOf(RandomUtils.nextInt(0, Integer.MAX_VALUE))));
        targetParams.add(new BasicNameValuePair("lang", "zh_CN"));
        targetParams.add(new BasicNameValuePair("pass_ticket", wxContext.getPassTicket()));
        targetParams.add(new BasicNameValuePair("skey", wxContext.getSkey()));

        Map<String, Object> targetMap = new HashMap<>();
        targetMap.putAll(dataMap);
        targetMap.put("BaseRequest", new BaseRequest(wxContext));

        URI uri;
        try {
            uri = new URIBuilder(url).addParameters(targetParams).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpPost httpPost = new HttpPost(uri);

        ObjectMapper objectMapper = new ObjectMapper();
        String body;
        try {
            body = objectMapper.writeValueAsString(targetMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        httpPost.setEntity(new StringEntity(body, Charset.forName("UTF-8")));

        try {
            String responseBody = httpUtil.execute(new URI(url), httpPost);
            return responseBody;
        } catch (URISyntaxException e) {
            throw new RuntimeException("e");
        }
    }

    private void checkResponse(WxResponse response) {
        if (response.getBaseResponse() == null) {
            throw new RuntimeException("no BaseResponse");
        }

        if (response.getBaseResponse().getRet() > 0) {
            throw new RuntimeException(
                    "invalid response, ret=" + response.getBaseResponse().getRet() + ", errMsg=" + response.getBaseResponse().getErrMsg());
        }
    }
}
