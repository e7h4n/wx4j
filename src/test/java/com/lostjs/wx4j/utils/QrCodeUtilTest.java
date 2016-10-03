package com.lostjs.wx4j.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pw on 01/10/2016.
 */
public class QrCodeUtilTest {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Test
    public void genTerminalQrCode() throws Exception {
        String code = QrCodeUtil.genTerminalQrCode("fjldksajlfkdjsaklfjdslkajflkdsajfk");

        LOG.info("code: \n{}", code);
    }

}