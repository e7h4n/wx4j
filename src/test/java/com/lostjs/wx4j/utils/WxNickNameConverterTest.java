package com.lostjs.wx4j.utils;

import org.junit.Test;

/**
 * Created by pw on 02/10/2016.
 */
public class WxNickNameConverterTest {

    @Test
    public void convertFromHtml() throws Exception {
        String emoji = WxNickNameConverter.convertFromHtml("<span class=\"emoji emoji2728\"></span>");
        System.out.println(emoji);

        emoji = WxNickNameConverter.convertFromHtml("<span class=\"emoji emoji1f353\"></span>");
        System.out.println(emoji);
    }
}