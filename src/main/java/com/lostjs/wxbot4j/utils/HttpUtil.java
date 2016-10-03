package com.lostjs.wxbot4j.utils;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Created by pw on 02/10/2016.
 */
public class HttpUtil {

    public static String execute(URI uri, HttpRequest request, HttpClient client) {
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
