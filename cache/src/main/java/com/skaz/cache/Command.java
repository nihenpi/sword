package com.skaz.cache;

import java.io.Serializable;

/**
 * @author jungle
 */
public class Command implements Serializable {

    public final static byte OPT_SET = 0x01;


    private byte oprator;
    private String name;
    private String key;


    public Command(byte operator, String name, String key) {
        this.oprator = operator;
        this.name = name;
        this.key = key;
    }

    public static Command set(String name, String key) {
        return new Command(OPT_SET, name, key);
    }
}
