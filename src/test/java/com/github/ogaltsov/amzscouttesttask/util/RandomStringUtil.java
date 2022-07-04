package com.github.ogaltsov.amzscouttesttask.util;

import java.util.UUID;

public class RandomStringUtil {

    private RandomStringUtil() {
    }

    public static String getRandomString() {
        return UUID.randomUUID().toString().substring(0, 9);
    }

}
