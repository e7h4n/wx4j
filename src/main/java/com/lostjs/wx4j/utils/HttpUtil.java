package com.lostjs.wx4j.utils;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by pw on 02/10/2016.
 */
@Service
public class HttpUtil {

    public static final long RETRY_INTERVAL = TimeUnit.SECONDS.toMillis(10);

    public static final int MAX_RETRY = 10;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CookieStore cookieStore;

    public HttpClient getHttpClient() {
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Content-Type", "charset=UTF-8"));

        return HttpClientBuilder.create()
                .setDefaultHeaders(headers)
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .setDefaultCookieStore(cookieStore)
                .setServiceUnavailableRetryStrategy(
                        new ServiceUnavailableRetryStrategy() {
                            @Override
                            public boolean retryRequest(HttpResponse response, int executionCount,
                                                        HttpContext context) {
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
                        })
                .build();
    }

    public String execute(URI uri, HttpRequest request) {
        return execute(uri, request, getHttpClient());
    }

    private String execute(URI uri, HttpRequest request, HttpClient client) {
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
        String responseBody;
        try {
            responseBody = StreamUtils.copyToString(content, Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseBody;
    }
}
