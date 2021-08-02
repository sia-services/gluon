package com.acc.gluon.utilities;

public class StringUtils {
    public static String lpad(String str, int len) {
        int l = str.length();
        if (l == len) return str;

        return "0".repeat(Math.max(0, len - l)) + str;
    }

    public static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String bytesToHex(final byte[] bytes)
    {
        final int numBytes = bytes.length;
        final char[] container = new char[numBytes * 2];

        for (int i = 0; i < numBytes; i++)
        {
            final int b = bytes[i] & 0xFF;

            container[i * 2] = Character.forDigit(b >>> 4, 0x10);
            container[i * 2 + 1] = Character.forDigit(b & 0xF, 0x10);
        }

        return new String(container).toUpperCase();
    }
}
