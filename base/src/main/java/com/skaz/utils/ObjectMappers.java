package com.skaz.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;
import java.util.Map;

public abstract class ObjectMappers {

    private static final String DateFormat = Dates.FORMAT_DATETIME;
    private static final boolean PrettyFormat = false;

    /********************************************************************************************************************************************************
     * newMapper
     *********************************************************************************************************************************************************/
    /**
     * 创建Mapper转换器
     */
    private static ObjectMapper newMapper(String dateFormat, ObjectMapper mapper) {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); // 单引号
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);// 未引号字段
            if (dateFormat == null) {
                dateFormat = DateFormat;
            }
            mapper.setDateFormat(Dates.newDateFormat(dateFormat));
        }
        return mapper;
    }

    /**
     * 创建Mapper转换器，
     */
    public static ObjectMapper newMapper() {
        return newMapper(null, null);
    }

    public static ObjectMapper newMapper(String dateFormat) {
        return newMapper(dateFormat, null);
    }

    public static ObjectMapper newMapper(ObjectMapper mapper) {
        return newMapper(null, mapper);
    }

    /********************************************************************************************************************************************************
     * toJSONString
     *********************************************************************************************************************************************************/
    public static String toJSONString(Object o) {
        return toJSONString(o, null, null, PrettyFormat);
    }

    public static String toJSONString(Object o, boolean prettyFormat) {
        return toJSONString(o, null, null, prettyFormat);
    }

    public static String toJSONString(Object o, String dateFormat) {
        return toJSONString(o, dateFormat, null, PrettyFormat);
    }

    public static String toJSONString(Object o, String dateFormat, boolean prettyFormat) {
        return toJSONString(o, dateFormat, null, prettyFormat);
    }

    public static String toJSONString(Object o, ObjectMapper mapper) {
        return toJSONString(o, null, mapper, PrettyFormat);
    }

    public static String toJSONString(Object o, ObjectMapper mapper, boolean prettyFormat) {
        return toJSONString(o, null, mapper, prettyFormat);
    }

    private static String toJSONString(Object o, String dateFormat, ObjectMapper mapper, boolean prettyFormat) {
        String jsonString = null;
        try {
            mapper = newMapper(mapper);
            if (dateFormat != null) {
                mapper.setDateFormat(Dates.newDateFormat(dateFormat));
            }
            if (prettyFormat) {
                jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            } else {
                jsonString = mapper.writeValueAsString(o);
            }
        } catch (JsonProcessingException e) {
            throw Exceptions.unchecked(e);
        }
        return jsonString;
    }

    /********************************************************************************************************************************************************
     * toBean
     *********************************************************************************************************************************************************/
    public static <T> T toBean(String jsonString, Class<T> clazz) {
        return toBean(jsonString, clazz, null, null);
    }

    public static <T> T toBean(String jsonString, Class<T> clazz, ObjectMapper mapper) {
        return toBean(jsonString, clazz, null, mapper);
    }

    public static <T> T toBean(String jsonString, TypeReference<?> typeReference) {
        return toBean(jsonString, null, typeReference, null);
    }

    public static <T> T toBean(String jsonString, TypeReference<?> typeReference, ObjectMapper mapper) {
        return toBean(jsonString, null, typeReference, mapper);
    }

    private static <T> T toBean(String jsonString, Class<T> clazz, TypeReference<?> type, ObjectMapper mapper) {
        try {
            mapper = newMapper(mapper);
            if (type != null) {
                return mapper.readValue(jsonString, type);
            } else {
                return mapper.readValue(jsonString, clazz);
            }
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /********************************************************************************************************************************************************
     * toList
     *********************************************************************************************************************************************************/
    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        return toList(jsonString, clazz, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(String jsonString, Class<T> clazz, ObjectMapper mapper) {
        List<T> objs = Lists.newArrayList();
        List<Map<String, Object>> maps = toBean(jsonString, List.class, mapper);
        if (maps != null) {
            for (Map<String, Object> map : maps) {
                objs.add(convert(map, clazz, mapper));
            }
        }
        return objs;
    }

    /********************************************************************************************************************************************************
     * toMap
     *********************************************************************************************************************************************************/
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        return newMapper().convertValue(obj, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String jsonString) {
        return toBean(jsonString, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String jsonString, ObjectMapper mapper) {
        return toBean(jsonString, Map.class, mapper);
    }

    /********************************************************************************************************************************************************
     * convert
     *********************************************************************************************************************************************************/
    public static <T> T convert(Object fromValue, Class<T> toValueType) {
        return convert(fromValue, toValueType, null);
    }

    public static <T> T convert(Object fromValue, Class<T> toValueType, ObjectMapper mapper) {
        return newMapper(mapper).convertValue(fromValue, toValueType);
    }

}
