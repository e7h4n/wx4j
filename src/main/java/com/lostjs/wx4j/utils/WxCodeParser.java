package com.lostjs.wx4j.utils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pw on 02/10/2016.
 */
public class WxCodeParser {

    public static Optional<Integer> parse(CharSequence content) {
        Pattern pattern = Pattern.compile("window\\.code=(\\d+);");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(Integer.valueOf(matcher.group(1)));
    }
}
