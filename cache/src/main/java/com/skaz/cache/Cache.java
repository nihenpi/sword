package com.skaz.cache;


import com.skaz.Constants;

/**
 * @author jungle
 */
public abstract class Cache {


    public static final Object SPLITER = ".";
    public static final String CACHE_STORE = Constants.CACHE_STORE;

    public static final int NOT_EXIST = -2;
    public static final int NO_EXPIRE = -1;

    public static final String CACHE_STORE_SYNC = "CACHE.SYNC.CHANNEL";

    private Cache() {
    }

    public enum Level {
        Local, Remote
    }

    public enum Operator {
        SET, GET, DEL, REM, CLS
    }

}
