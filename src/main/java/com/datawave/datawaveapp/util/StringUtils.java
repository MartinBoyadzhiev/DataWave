package com.datawave.datawaveapp.util;

public class StringUtils {
    public static boolean isAlphanumeric(String s) {
        return s != null && s.matches("^[a-z0-9]*$");
    }
}
