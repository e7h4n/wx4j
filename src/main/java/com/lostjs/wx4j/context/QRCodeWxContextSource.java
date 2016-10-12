package com.lostjs.wx4j.context;

import com.lostjs.wx4j.transporter.WxTransporter;
import com.lostjs.wx4j.utils.QrCodeUtil;
import com.lostjs.wx4j.utils.WxCodeParser;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pw on 01/10/2016.
 */
public class QRCodeWxContextSource implements WxContextSource {

    public static final int RETRY_LIMIT = 10;

    private static final int TIP_WAIT_SCAN = 1;

    private static final int TIP_WAIT_CONFIRM = 0;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private String uuid;

    private WxTransporter transporter;

    public QRCodeWxContextSource(WxTransporter transporter) {
        this.transporter = transporter;
    }

    public String prepareLogin() {
        this.uuid = getUUID();
        return this.getLoginUrl();
    }

    public Optional<String> waitLogin() {
        /*
        http comet:
        tip=1, 等待用户扫描二维码,
               201: scaned
               408: timeout
        tip=0, 等待用户确认登录,
               200: confirmed
         */

        String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
        int tip = TIP_WAIT_SCAN;

        int retryLimit = RETRY_LIMIT;
        while (retryLimit-- > 0) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("tip", String.valueOf(tip)));
            params.add(new BasicNameValuePair("uuid", this.uuid));
            String response = transporter.get(url, params);

            Optional<Integer> codeOpt = WxCodeParser.parse(response);
            if (!codeOpt.isPresent()) {
                continue;
            }

            int code = codeOpt.get();

            if (code == 201) {
                retryLimit += 1;
                tip = TIP_WAIT_CONFIRM;
                LOG.debug("qrcode scanned");
                continue;
            }

            if (code == 408) {
                LOG.warn("timeout");
                continue;
            }

            if (code == 200) {
                Pattern pattern = Pattern.compile("window.redirect_uri=\"(\\S+?)\";");
                Matcher matcher = pattern.matcher(response);
                if (!matcher.find()) {
                    throw new RuntimeException("Invalid response, can not parse redirect_uri, response=" + response);
                }

                String redirectUrl = matcher.group(1) + "&fun=new";
                LOG.debug("redirectUrl: {}", redirectUrl);
                return Optional.of(redirectUrl);
            }
        }

        return Optional.empty();
    }

    public boolean login(String loginUrl) {
        if (StringUtils.isBlank(loginUrl)) {
            throw new RuntimeException("do waitForLogin first");
        }

        String response = transporter.get(loginUrl);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document document;
        try {
            document = builder.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        String skey = getSingleTagContent(document, "skey");
        String sid = getSingleTagContent(document, "wxsid");
        String uin = getSingleTagContent(document, "wxuin");
        String passTicket = getSingleTagContent(document, "pass_ticket");

        WxContext context = transporter.getContext();
        context.setBaseUrl(getBaseUrl(loginUrl));
        context.setSkey(skey);
        context.setSid(sid);
        context.setUin(uin);
        context.setPassTicket(passTicket);

        LOG.debug("wechat auth context: {}", context.toString());
        return true;
    }

    @Override
    public boolean initWxWebContext() {
        String qrcodeLink = prepareLogin();
        String qrcode = QrCodeUtil.genTerminalQrCode(qrcodeLink);
        LOG.info("Scan qrcode to login: \n{}", qrcode);
        Optional<String> loginUrl = waitLogin();
        if (!loginUrl.isPresent()) {
            throw new RuntimeException("can't get login url");
        }

        return login(loginUrl.get());
    }

    private String getBaseUrl(String loginUrl) {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(loginUrl).setCustomQuery(null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String path = uriBuilder.getPath();
        int lastIndex = path.lastIndexOf("/");
        path = path.substring(0, lastIndex);
        uriBuilder.setPath(path);

        URI uri;
        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return uri.toString();
    }

    private String getSingleTagContent(Document document, String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            throw new RuntimeException(tagName + " not found");
        }

        return nodes.item(0).getTextContent();
    }

    private String getUUID() {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", "wx782c26e4c19acffb"));
        params.add(new BasicNameValuePair("fun", "new"));
        params.add(new BasicNameValuePair("lang", "zh_CN"));
        params.add(new BasicNameValuePair("_", String.valueOf(RandomUtils.nextInt(0, Integer.MAX_VALUE))));

        String responseBody = transporter.get("https://login.weixin.qq.com/jslogin", params);
        Pattern pattern = Pattern.compile("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\"");
        Matcher matcher = pattern.matcher(responseBody);
        int groupCount = matcher.groupCount();
        if (groupCount != 2) {
            throw new RuntimeException("invalid get uuid response, responseBody=" + responseBody);
        }

        boolean found = matcher.find();
        if (!found) {
            throw new RuntimeException("invalid get uuid response, responseBody=" + responseBody);
        }

        String code = matcher.group(1);

        LOG.debug("code: {}", code);
        if (!"200".equals(code)) {
            throw new RuntimeException("invalid get uuid response, responseBody=" + responseBody);
        }

        String uuid = matcher.group(2);
        LOG.debug("uuid: {}", uuid);
        return uuid;
    }

    private String getLoginUrl() {
        if (StringUtils.isBlank(this.uuid)) {
            throw new RuntimeException("prepareLogin first");
        }

        return String.format("https://login.weixin.qq.com/l/%s", this.uuid);
    }
}