package com.acc.gluon.utilities;

public class StringUtils {
    public static String lpad(String str, int len) {
        int l = str.length();
        if (l == len) return str;

        return "0".repeat(Math.max(0, len - l)) + str;
    }
}
