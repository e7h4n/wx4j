package com.lostjs.wx4j.context;

import com.lostjs.wx4j.test.TestHelper;
import com.lostjs.wx4j.transporter.WxTransporter;
import com.lostjs.wx4j.transporter.BasicWxTransporter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pw on 01/10/2016.
 */
public class QRCodeWxContextSourceTest {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Test
    @Ignore
    public void testInit() throws Exception {
        WxContextSource contextSource = new QRCodeWxContextSource();
        WxContext context = new FileWxContext(TestHelper.getTmpFilePath());
        boolean success = contextSource.initWxWebContext(context);
        Assert.assertTrue(success);
        Assert.assertTrue(StringUtils.isNotBlank(context.getSid()));
        Assert.assertTrue(StringUtils.isNotBlank(context.getBaseUrl()));
        Assert.assertTrue(StringUtils.isNotBlank(context.getDeviceId()));
        Assert.assertTrue(StringUtils.isNotBlank(context.getPassTicket()));
        Assert.assertTrue(StringUtils.isNotBlank(context.getSkey()));
        Assert.assertTrue(StringUtils.isNotBlank(context.getUin()));

        WxTransporter wxTransporter = new BasicWxTransporter(context);
        LOG.info("init: {}", wxTransporter.execute("/webwxinit"));
        String contactResponse = wxTransporter.execute("/webwxgetcontact");

        LOG.info("concactResponse: {}", contactResponse);
    }
}