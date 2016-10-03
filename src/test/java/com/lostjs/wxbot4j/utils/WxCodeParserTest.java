package com.lostjs.wxbot4j.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Created by pw on 02/10/2016.
 */
public class WxCodeParserTest {

    @Test
    public void parse() throws Exception {
        Optional<Integer> integerOptional = WxCodeParser.parse("window.code=201;");

        Assert.assertTrue(integerOptional.isPresent());
        Assert.assertEquals(201, integerOptional.get().intValue());

        integerOptional = WxCodeParser.parse("fdas fdsa fds afd sa");

        Assert.assertFalse(integerOptional.isPresent());
    }
}