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
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by pw on 02/10/2016.
 */
public class BasicWxTransporter implements WxTransporter {

    protected final WxContext wxContext;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

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
            String responseBody = HttpUtil.execute(new URI(url), httpPost, getHttpClient());
            return responseBody;
        } catch (URISyntaxException e) {
            throw new RuntimeException("e");
        }
    }

    private HttpClient getHttpClient() {
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Content-Type", "charset=UTF-8"));
        return HttpClientBuilder.create()
                .setDefaultCookieStore(wxContext)
                .setDefaultHeaders(headers)
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                    @Override
                    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                        int statusCode = response.getStatusLine().getStatusCode();
                        boolean retry = statusCode != 200 && executionCount < 10;
                        if (retry) {
                            LOG.info("retry, statusCode={}, executionCount = {}", statusCode, executionCount);
                        }
                        return retry;
                    }

                    @Override
                    public long getRetryInterval() {
                        return TimeUnit.SECONDS.toMillis(10);
                    }
                })
                .build();
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
