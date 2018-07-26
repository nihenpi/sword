package com.skaz.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.skaz.bean.Result;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author jungle
 */
public abstract class FastJsons {

    public static final SerializerFeature PrettyFormat = SerializerFeature.PrettyFormat;
    public static final SerializerFeature WriteMapNullValue = SerializerFeature.WriteMapNullValue;
    public static final SerializerFeature DisableCircularReferenceDetect = SerializerFeature.DisableCircularReferenceDetect;
    public static final SerializerFeature WriteClassName = SerializerFeature.WriteClassName;
    public static final SerializerFeature WriteDateUseDateFormat = SerializerFeature.WriteDateUseDateFormat;
    public static final SerializerFeature UseISO8601DateFormat = SerializerFeature.UseISO8601DateFormat;

    public static final SerializerFeature[] ToJSONStringFeatures = {WriteMapNullValue, DisableCircularReferenceDetect};

    // 使用默认日期格式:yyyy-MM-dd HH:mm:ss
    public static final SerializerFeature[] ToJSONStringFeaturesWithDateFormat = Arrays.add(ToJSONStringFeatures, WriteDateUseDateFormat);

    // 使用ISO日期格式
    public static final SerializerFeature[] ToJSONStringFeaturesWithISO8601DateFormat = Arrays.add(ToJSONStringFeatures, UseISO8601DateFormat);

    public static final String toJSONString(Object o) {
        return toJSONString(o, false, new SerializerFeature[]{});
    }

    public static final String toJSONString(Object o, SerializerFeature... features) {
        return toJSONString(o, false, features);
    }

    public static final String toJSONString(Object o, boolean prettyFormat) {
        return toJSONString(o, prettyFormat, new SerializerFeature[]{});
    }

    public static final String toJSONString(Object o, boolean prettyFormat, SerializerFeature... features) {
        if (null == o) {
            return null;
        }
        if (prettyFormat) {
            features = Arrays.add(features, PrettyFormat);
        }
        // 添加默认配置
        features = Arrays.add(features, ToJSONStringFeatures);
        return JSON.toJSONString(o, features);
    }

    public static final String toJSONStringWithDateFormat(Object o, String dateFormat, SerializerFeature... features) {
        if (null == o) {
            return null;
        }
        features = Arrays.add(features, ToJSONStringFeaturesWithDateFormat);
        return JSON.toJSONStringWithDateFormat(o, dateFormat, features);
    }

    public static final String toJSONStringWithDateFormatDate(Object o, SerializerFeature... features) {
        return toJSONStringWithDateFormat(o, Dates.FORMAT_DATE, features);
    }

    public static final String toJSONStringWithDateFormatDateTime(Object o, SerializerFeature... features) {
        return toJSONStringWithDateFormat(o, Dates.FORMAT_DATETIME, features);
    }

    public static final String toJSONStringWithDateFormatISO8601(Object o, SerializerFeature... features) {
        features = Arrays.add(features, ToJSONStringFeaturesWithISO8601DateFormat);
        return toJSONString(o, features);
    }

    @SuppressWarnings("unused")
    private static final String _toJSONString(Object o, String dateFormat, boolean prettyFormat, SerializerFeature... features) {
        String jsonString = null;
        // 合并默认SerializerFeatures
        features = Arrays.add(ToJSONStringFeaturesWithDateFormat, features);
        if (prettyFormat) {// 设置PrettyFormat
            features = Arrays.add(features, PrettyFormat);
        }
        if (dateFormat != null) {
            jsonString = JSON.toJSONStringWithDateFormat(o, dateFormat, features);
        } else {
            jsonString = JSON.toJSONString(o, features);
        }
        return jsonString;
    }

    //
    // parse
    // ---------------------------------------------------------------------------------------------------
    public static <T> T parse(String jsonString, Class<T> clazz) {
        if (null == jsonString || null == clazz) {
            return null;
        } else {
            return JSON.parseObject(jsonString, clazz);
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    private static <T> T ___parse(String jsonString, Class<T> clazz) {
        if (null == jsonString) {
            return null;
        }
        if (null == clazz) {
            T obj = (T) JSON.parse(jsonString);
            if (obj instanceof JSONArray) {
                List<T> objs = Lists.newArrayList();
                JSONArray array = JSON.parseArray(jsonString);
                for (int i = 0; i < array.size(); i++) {
                    objs.add((T) array.get(i));
                }
                return (T) objs;
            }
            return obj;
        } else {
            return JSON.parseObject(jsonString, clazz);
        }
    }

    public static <T> T parseToObject(String jsonString, Class<T> clazz) {
        if (null == jsonString || null == clazz) {
            return null;
        }
        return JSON.parseObject(jsonString, clazz);
    }

    public static <T> T parseToObject(String jsonString, Type returnType) {
        if (null == jsonString || jsonString.length() == 0) {
            return null;
        }
        return JSON.parseObject(jsonString, returnType);
    }

    public static <T> T parseToObject(String jsonString, TypeReference<T> type) {
        if (null == jsonString || null == type) {
            return null;
        }
        return JSON.parseObject(jsonString, type);
    }

    public static <T> List<T> parseToList(String jsonString, Class<T> clazz) {
        if (null == jsonString || null == clazz) {
            return null;
        } else {
            return JSON.parseArray(jsonString, clazz);
        }
    }

    public static <K, V> Map<K, V> parseToMap(String jsonString, Class<K> keyType, Class<V> valueType) {
        if (null == jsonString || null == keyType || null == valueType) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<Map<K, V>>(keyType, valueType) {
        });
    }

    public static <T> T parseToMap(String jsonString, TypeReference<T> type) {
        if (null == jsonString) {
            return null;
        }
        return parseToObject(jsonString, type);
    }

    public static <T> Result<T> parseToResultWithClassIn(String jsonString, Class<T> clazz) {
        if (null == jsonString || null == clazz) {
            return null;
        }
        return JSON.parseObject(jsonString, ParameterizedTypeImpl.make(Result.class, clazz));
    }

    public static <T> Result<List<T>> parseToResultWithListIn(String jsonString, Class<T> clazz) {
        if (null == jsonString || null == clazz) {
            return null;
        }
        return JSON.parseObject(jsonString, ParameterizedTypeImpl.makeInList(Result.class, clazz));
    }

    public static <K, V> Result<Map<K, V>> parseToResultWithMapIn(String jsonString, Class<K> keyType, Class<V> valueType) {
        if (null == jsonString) {
            return null;
        }
        return JSON.parseObject(jsonString, ParameterizedTypeImpl.makeInMap(Result.class, keyType, valueType));
    }

}
