package com.lostjs.wxbot4j.client;

import com.lostjs.wxbot4j.context.WxContext;
import com.lostjs.wxbot4j.test.TestHelper;
import com.lostjs.wxbot4j.transporter.WxTransporter;
import com.lostjs.wxbot4j.transporter.BasicWxTransporter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pw on 02/10/2016.
 */
public class BasicWxTransporterTest {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private WxTransporter wxTransporter;

    @Before
    public void setUp() throws Exception {
        WxContext context = TestHelper.loadContextFromTmpFile();
        wxTransporter = new BasicWxTransporter(context);
    }

    @Test
    @Ignore
    public void post() throws Exception {
        String response = wxTransporter.execute("/webwxinit");
        System.out.println(response);
    }

    @Test
    @Ignore
    public void post_params() throws Exception {
        String response = wxTransporter.execute("/webwxgetcontact");
        LOG.info("contact list: {}", response);
    }

    @Test
    @Ignore
    public void post_dataMap() throws Exception {

    }

}