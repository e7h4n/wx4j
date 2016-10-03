package com.lostjs.wx4j.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostjs.wx4j.context.BasicWxContext;
import com.lostjs.wx4j.context.WxContext;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by pw on 02/10/2016.
 */
public class TestHelper {

    public static void writeContextToTmpFile(WxContext WxContext) throws IOException {
        String tmpFile = getTmpFilePath();
        ObjectMapper objectMapper = new ObjectMapper();
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(tmpFile), "utf-8"));
        writer.write(objectMapper.writeValueAsString(WxContext));
        writer.flush();
        writer.close();
    }

    public static WxContext loadContextFromTmpFile() throws IOException {
        String tmpFile = getTmpFilePath();
        FileInputStream fileInputStream = new FileInputStream(tmpFile);
        String string = StreamUtils.copyToString(fileInputStream, Charset.forName("UTF-8"));
        return new ObjectMapper().readValue(string, BasicWxContext.class);
    }

    public static String getTmpFilePath() {
        return System.getProperty("java.io.tmpdir") + "/ctxSource";
    }
}
