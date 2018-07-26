package com.skaz.utils;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * @author jungle
 */
public class Cryptos {
    public static final String MD5 = "MD5";


    public static String encryptMd5(String input) {
        byte[] encrypted = digest(input.getBytes(), MD5, null, 1);
        return Encodes.encodeHex(encrypted);
    }

    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                digest.update(salt);
            }
            byte[] result = digest.digest(input);
            for (int i = 1; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        } catch (GeneralSecurityException e) {
            throw Exceptions.unchecked(e);
        }
    }
}
