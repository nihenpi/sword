package com.skaz.utils;

import com.skaz.Constants;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * @author jungle
 */
public class Envs {
    public static Environment ENV;

    public static String getString(String key, String defaultValue) {
        if (ENV != null) {
            String value = ENV.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        if (ENV != null) {
            Integer value = ENV.getProperty(key, Integer.class);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    public static boolean isDebug() {
        return isDev() || isTest();
    }

    private static boolean isTest() {
        if (ENV != null) {
            return Arrays.stream(ENV.getActiveProfiles()).anyMatch(ENV -> (ENV.equalsIgnoreCase(Constants.PROFILE_TEST)));
        }
        return true;    }

    private static boolean isDev() {
        if (ENV != null) {
            return Arrays.stream(ENV.getActiveProfiles()).anyMatch(ENV -> (ENV.equalsIgnoreCase(Constants.PROFILE_DEV)));
        }
        return true;    }
}
