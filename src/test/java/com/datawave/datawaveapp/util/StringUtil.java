package com.datawave.datawaveapp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtil {


    @Test
    void isAlphanumeric_ReturnsTrue_WhenStringIsAlphanumeric() {
        StringUtils stringUtils = new StringUtils();
        assertTrue(StringUtils.isAlphanumeric("abc123"));
    }

    @Test
    void isAlphanumeric_ReturnsFalse_WhenStringIsNotAlphanumeric() {
        StringUtils stringUtils = new StringUtils();
        assertTrue(!StringUtils.isAlphanumeric("abc123!"));
    }

}
