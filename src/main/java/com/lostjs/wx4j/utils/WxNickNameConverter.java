package com.lostjs.wx4j.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pw on 02/10/2016.
 */
public class WxNickNameConverter {

    public static String convertFromHtml(String htmlNickName) {
        Pattern pattern = Pattern.compile("<span class=\"emoji emoji([0-9a-f]+)\"></span>");
        Matcher m = pattern.matcher(htmlNickName);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, new String(Character.toChars(Integer.parseInt(m.group(1), 16))));
        }
        m.appendTail(sb);

        return sb.toString();
    }
}
