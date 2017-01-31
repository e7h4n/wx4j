package com.lostjs.wx4j.transporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostjs.wx4j.context.WxContext;
import com.lostjs.wx4j.data.request.BaseRequest;
import com.lostjs.wx4j.data.response.BaseResponse;
import com.lostjs.wx4j.data.response.WxResponse;
import com.lostjs.wx4j.exception.InvalidResponseException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pw on 02/10/2016.
 */
public class BasicWxTransporter implements WxTransporter {

    public static final long RETRY_INTERVAL = TimeUnit.SECONDS.toMillis(10);

    public static final int MAX_RETRY = 10;

    public static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Safari/602.1.50";

    public static final int REQUEST_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(60);

    protected final WxContext context;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public BasicWxTransporter(WxContext context) {
        this.context = context;
    }

    @Override
    public WxContext getContext() {
        return context;
    }

    @Override
    public InputStream getBinary(String api) {
        return getBinary(api, Collections.emptyList());
    }

    @Override
    public InputStream getBinary(String api, List<NameValuePair> params) {
        URI uri = getUri(api, params);

        return executeBinary(uri, new HttpGet(uri));
    }

    private URI getUri(String api, List<NameValuePair> params) {
        String url = getUrl(api);
        List<NameValuePair> targetParams = getRequestParams(params);

        URI uri;
        try {
            uri = new URIBuilder(url).addParameters(targetParams).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    @Override
    public String get(String api) {
        return get(api, Collections.emptyList());
    }

    @Override
    public String get(String api, List<NameValuePair> params) {
        URI uri = getUri(api, params);

        return execute(uri, new HttpGet(uri));
    }

    @Override
    public <T extends WxResponse> T get(String api, List<NameValuePair> params, TypeReference<T> responseType) {
        return internalGet(api, params, responseType);
    }

    @Override
    public <T extends WxResponse> T get(String api, TypeReference<T> responseType) {
        return get(api, Collections.emptyList(), responseType);
    }

    @Override
    public String post(String api) {
        return post(api, Collections.emptyList(), Collections.emptyMap());
    }

    @Override
    public String post(String api, List<NameValuePair> params) {
        return post(api, params, Collections.emptyMap());
    }

    @Override
    public String post(String api, Map<String, Object> dataMap) {
        return post(api, new ArrayList<>(), dataMap);
    }

    @Override
    public <T extends WxResponse> T post(String api, TypeReference<T> responseType) {
        return post(api, Collections.emptyList(), Collections.emptyMap(), responseType);
    }

    @Override
    public <T extends WxResponse> T post(String api, Map<String, Object> dataMap, TypeReference<T> responseType) {
        return post(api, Collections.emptyList(), dataMap, responseType);
    }

    @Override
    public <T extends WxResponse> T post(String api, List<NameValuePair> params, TypeReference<T> responseType) {
        return post(api, params, Collections.emptyMap(), responseType);
    }

    @Override
    public <T extends WxResponse> T post(String api, List<NameValuePair> params, Map<String, Object> dataMap,
                                         TypeReference<T> responseType) {
        return internalPost(api, params, dataMap, responseType);
    }

    @Override
    public String post(String api, List<NameValuePair> params, Map<String, Object> dataMap) {
        String url = getUrl(api);
        List<NameValuePair> targetParams = getRequestParams(params);

        Map<String, Object> targetMap = new HashMap<>();
        targetMap.putAll(dataMap);
        targetMap.put("BaseRequest", new BaseRequest(context));

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
            String responseBody = execute(new URI(url), httpPost);
            return responseBody;
        } catch (URISyntaxException e) {
            throw new RuntimeException("e");
        }
    }

    private <T extends WxResponse> T internalGet(String api, List<NameValuePair> params,
                                                 TypeReference<T> responseType) {
        while (true) {
            try {
                String responseBody = get(api, params);
                return convertResponse(responseType, responseBody);
            } catch (InvalidResponseException e) {
                processInvalidResponseException(e);
            }
        }
    }

    private void processInvalidResponseException(InvalidResponseException e) {
        if (e.getRet() == 1101) {
            try {
                LOG.warn("encounter 1101, sleep 3 seconds to retry");

                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
            } catch (InterruptedException e1) {
                throw new RuntimeException(e1);
            }
            return;
        }

        throw e;
    }

    private <T extends WxResponse> T internalPost(String api, List<NameValuePair> params, Map<String, Object> dataMap,
                                                  TypeReference<T> responseType) {
        while (true) {
            try {
                String responseBody = post(api, params, dataMap);
                return convertResponse(responseType, responseBody);
            } catch (InvalidResponseException e) {
                processInvalidResponseException(e);
            }
        }
    }

    private HttpClient getHttpClient() {
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "charset=UTF-8"));
        headers.add(new BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT));

        RequestConfig defaultRequestConfig =
                RequestConfig.custom().setSocketTimeout(REQUEST_TIMEOUT).setConnectionRequestTimeout(REQUEST_TIMEOUT)
                        .setConnectTimeout(REQUEST_TIMEOUT).build();

        return HttpClientBuilder.create().setDefaultCookieStore(context).setDefaultHeaders(headers)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setRetryHandler((exception, executionCount, httpContext) -> executionCount < 10)
                .setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {

                    @Override
                    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                        int statusCode = response.getStatusLine().getStatusCode();
                        boolean success = statusCode == 200;
                        boolean retry = !success && executionCount < MAX_RETRY;
                        if (retry) {
                            LOG.info("retry, statusCode={}, executionCount = {}", statusCode, executionCount);
                        }
                        return retry;
                    }

                    @Override
                    public long getRetryInterval() {
                        return RETRY_INTERVAL;
                    }
                }).build();
    }

    private <T extends WxResponse> T convertResponse(TypeReference<T> responseType, String responseBody) {
        LOG.debug("response: \n{}", responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T responseObject;
        try {
            responseObject = objectMapper.readValue(responseBody, responseType);
        } catch (IOException e) {
            throw new RuntimeException("failed to convert response: \n" + responseBody, e);
        }

        checkResponse(responseObject);
        return responseObject;
    }

    private String getUrl(String api) {
        Pattern pattern = Pattern.compile("^https?://");
        Matcher matcher = pattern.matcher(api);
        if (matcher.find()) {
            return api;
        }

        while (StringUtils.isEmpty(context.getBaseUrl())) {
            LOG.warn("context is uninitialized, sleep 3 seconds to retry");
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return matcher.find() ? api : context.getBaseUrl() + api;
    }

    private List<NameValuePair> getRequestParams(List<NameValuePair> params) {
        List<NameValuePair> targetParams = new ArrayList<>();
        targetParams.addAll(params);
        targetParams.add(new BasicNameValuePair("r", String.valueOf(RandomUtils.nextInt(0, Integer.MAX_VALUE))));
        targetParams.add(new BasicNameValuePair("lang", "zh_CN"));
        targetParams.add(new BasicNameValuePair("pass_ticket", context.getPassTicket()));
        targetParams.add(new BasicNameValuePair("skey", context.getSkey()));
        return targetParams;
    }

    private void checkResponse(WxResponse response) {
        BaseResponse baseResponse = response.getBaseResponse();
        if (baseResponse == null) {
            throw new RuntimeException("no BaseResponse");
        }

        if (baseResponse.getRet() > 0) {
            throw new InvalidResponseException(baseResponse.getRet(), baseResponse.getErrMsg());
        }
    }

    private String execute(URI uri, HttpRequest request) {
        InputStream content = executeBinary(uri, request);

        String responseBody;
        try {
            responseBody = StreamUtils.copyToString(content, Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseBody;
    }

    private InputStream executeBinary(URI uri, HttpRequest request) {
        HttpClient client = getHttpClient();

        HttpResponse httpResponse;
        try {
            httpResponse = client.execute(new HttpHost(uri.getHost()), request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new RuntimeException("invalid http status code, status=" + String.valueOf(statusCode));
        }

        HttpEntity entity = httpResponse.getEntity();
        InputStream content;
        try {
            content = entity.getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }
}
