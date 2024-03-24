package org.slogga.habboscanner.utils;

import java.nio.charset.StandardCharsets;

public class UTF8Utils {
    public static String convertToUTF8(String originalString) {
        byte[] bytes = originalString.getBytes(StandardCharsets.ISO_8859_1);

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
