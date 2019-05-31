package com.task.rvt.mt.util;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomStringGenerator {
    public static String generateString(int lalelLength) {
        return RandomStringUtils.randomAlphanumeric(lalelLength);
    }

    private RandomStringGenerator() {
    }
}
