package com.lostjs.wx4j.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pw on 01/10/2016.
 */
public class QrCodeUtil {

    private static final String UNSET_DOT = Ansi.ansi().bg(Ansi.Color.WHITE).a("  ").reset().toString();

    private static final String SET_DOT = Ansi.ansi().a("  ").reset().toString();

    private static Logger LOG = LoggerFactory.getLogger(QrCodeUtil.class);

    public static String genTerminalQrCode(String content) {
        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, 16, 2);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        StringBuilder result = new StringBuilder();

        for (int y = 0; y < matrix.getHeight(); ++y) {
            for (int x = 0; x < matrix.getWidth(); ++x) {
                result.append(matrix.get(x, y) ? SET_DOT : UNSET_DOT);
            }
            result.append("\n");
        }

        return result.toString();
    }
}
