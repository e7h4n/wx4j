package com.lostjs.wxbot4j.utils;

import com.lostjs.wxbot4j.data.SyncKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pw on 02/10/2016.
 */
public class WxSyncKeyUtil {

    private static Logger LOG = LoggerFactory.getLogger(WxSyncKeyUtil.class);

    public static String format(List<SyncKey> syncKeys) {
        LOG.debug("sync key size: {}", syncKeys.size());

        return StringUtils.join(syncKeys.stream().map(k -> String.format("%d_%d", k.getKey(), k.getValue())).collect(
                Collectors.toList()), "|");
    }
}
