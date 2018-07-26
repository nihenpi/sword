package com.skaz.utils;

import java.util.List;
import java.util.Map;

public abstract class Jsons {

    public static final String toJSONString(Object o) {
        return ObjectMappers.toJSONString(o);
    }

    public static final String toJSONString(Object o, boolean prettyFormat) {
        return ObjectMappers.toJSONString(o, prettyFormat);
    }

    public static final String toJSONString(Object o, String dateFormat) {
        return ObjectMappers.toJSONString(o, dateFormat);
    }

    public static final String toJSONString(Object o, String dateFormat, boolean prettyFormat) {
        return ObjectMappers.toJSONString(o, dateFormat, prettyFormat);
    }

    public static <T> T toBean(String jsonString, Class<T> clazz) {
        return ObjectMappers.toBean(jsonString, clazz);
    }

    public static <T> T toBean(String jsonString, Class<T> clazz, String dateFormat) {
        return ObjectMappers.toBean(jsonString, clazz, ObjectMappers.newMapper(dateFormat));
    }

    public static List<Object> toList(String jsonString) {
        return ObjectMappers.toList(jsonString, Object.class);
    }

    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        return ObjectMappers.toList(jsonString, clazz);
    }

    public static <T> List<T> toList(String jsonString, Class<T> clazz, String dateFormat) {
        return ObjectMappers.toList(jsonString, clazz, ObjectMappers.newMapper(dateFormat));
    }

    public static Map<String, Object> toMap(String jsonString) {
        return ObjectMappers.toMap(jsonString);
    }

}
