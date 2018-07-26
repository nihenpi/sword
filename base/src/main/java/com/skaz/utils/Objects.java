package com.skaz.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author jungle
 */
public abstract class Objects {

    private static final Class<?>[] BASIC_NUMBER_CLASSES = new Class[]{short.class, int.class, long.class, float.class, double.class};

    private Objects() {
    }

    public static <T> T newObject(Object obj, Class<T> clazz) {
        return newObject(obj, clazz, null);
    }

    public static <T> T newObject(Object obj, Class<T> clazz, ObjectMapper mapper) {
        return ObjectMappers.convert(obj, clazz, mapper);
    }

    public static <T> T newObject(Map<String, Object> map, Class<T> clazz) {
        return newObject(map, clazz, null);
    }

    public static <T> T newObject(Map<String, Object> map, Class<T> clazz, ObjectMapper mapper) {
        return ObjectMappers.convert(map, clazz, mapper);
    }

    public static <T> List<T> newListObject(Object objs, Class<T> clazz) {
        return Casts.toList(objs, clazz);
    }


    /**
     * 是否为基础数据类型
     *
     * @param obj Object
     * @return boolean true or false
     */
    public static boolean isPrimitive(Object obj) {
        boolean rv = obj.getClass().isPrimitive() || obj instanceof String || obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Character || obj instanceof Boolean || obj instanceof Short || obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal || obj instanceof BigInteger;
        return rv;
    }


    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).length() == 0;
        }
        Class cl = obj.getClass();
        if (cl.isArray()) {
            int len = Array.getLength(obj);
            return len == 0;
        }
        if (obj instanceof Collection) {
            Collection tempCol = (Collection) obj;
            return tempCol.isEmpty();
        }
        if (obj instanceof Map) {
            Map tempMap = (Map) obj;
            return tempMap.isEmpty() || tempMap.size() == 0;
        }
        return false;
    }

    public static boolean isArray(Object obj) {
        return (obj != null && obj.getClass().isArray());
    }

    public static boolean isEqual(Object a, Object b) {
        return com.google.common.base.Objects.equal(a, b);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> newListEachObject(Object objs, Class<T> clazz) {
        List<T> datas = Lists.newArrayList();
        for (Object obj : (List<Object>) objs) {
            datas.add(Objects.newObject(obj, clazz));
        }
        return datas;
    }


    public static final boolean isZero(Object obj) {
        if (!isNumberType(obj)) {
            return false;
        }
        final String foo = String.valueOf(obj);
        return StringUtils.equals(foo, "0") || StringUtils.equals(foo, "0.0");
    }

    public static final boolean isNumberType(Object obj) {
        if (obj == null) {
            throw new RuntimeException("object is null.");
        }
        if (obj instanceof Number) {
            return true;
        } else {
            for (Class<?> clazz : BASIC_NUMBER_CLASSES) {
                if (obj.getClass().equals(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void copy(Object target, Object source) {
        try {
            BeanUtils.copyProperties(target, source);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    public static void copy(Object bean, String name, Object value) {
        try {
            BeanUtils.copyProperty(bean, name, value);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }


    @SuppressWarnings("unchecked")
    public static final <T> T clone(T t) {
        if (t == null) {
            return null;
        }
        if (t instanceof Serializable) {
            return (T) SerializationUtils.clone((Serializable) t);
        }
        T result = null;
        if (t instanceof Cloneable) {
            try {
                result = (T) ObjectUtils.clone(t);
            } catch (Throwable e) {
            }
        }
        if (result == null) {
            String json = JSON.toJSONString(t);
            result = (T) JSON.parseObject(json, t.getClass());
        }
        return result;
    }

    /**
     * 复制source中不能为空的对象
     */
    public static void copyNotNullProperty(Object target, Object source) {
        Beans.copyPropertiesNotNull(target, source);
    }

    /**
     * Map中的属性复制到Bean
     */
    public static void populate(Object bean, Map<String, ? extends Object> properties) {
        try {
            BeanUtils.populate(bean, properties);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * Properties中的属性复制到Bean
     */
    public static void populate(Object bean, Properties properties) {
        populate(bean, Props.toMap(properties));
    }

    public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        BeanUtils.setProperty(bean, name, value);
    }

    public static Map<String, Object> toMap_ObjectMappers(Object bean) {
        return ObjectMappers.toMap(bean);
    }

    public static Map<String, String> toMap_BeanUtils(Object bean) {
        Map<String, String> map = Maps.newHashMap();
        try {
            map = BeanUtils.describe(bean);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException("转换错误", e);
        }
        return map;
    }


    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

    public static Map<String, Object> toMap(Object bean) {
        return JSON.parseObject(JSON.toJSONString(bean));
    }

    public static Map<String, Object> toMapByReflection(Object bean) {
        Asserts.isNotNull(bean, "bean can not be EMPTY");
        Map<String, Object> map = Maps.newHashMap();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(bean);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return map;
    }


    public static Map<String, String> toMapString(Object bean) {
        Asserts.isNotNull(bean, "bean can not be EMPTY");
        Map<String, String> map = Maps.newHashMap();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(bean);
                    if (value instanceof Date) {
                        value = Dates.toString((Date) value);
                    }
                    map.put(key, value != null ? String.valueOf(value) : Strings.EMPTY);
                }
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return map;
    }

    public static void println(Object... objs) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            sb.append(toString(obj) + "\n");
        }
        System.out.println(sb);
    }

    public static String toString(Object obj) {
        return toString(obj, false);
    }

    public static String toString(Object obj, boolean prettyFormat) {
        StringBuffer s = new StringBuffer();
        if (null == obj) {
            return "null";
        }
        if (isPrimitive(obj)) {
            s.append(String.valueOf(obj));
        }
        //
        else if (obj instanceof Enum) {
            s.append(((Enum<?>) obj).name());
        }
        //
        else if (obj instanceof Date) {
            // return String.valueOf(((Date) obj).getTime());
            s.append(FastJsons.toJSONString(obj, prettyFormat));
        }
        //
        //
        else if (obj instanceof Calendar) {
            // return String.valueOf(((Calendar) obj).getTime().getTime());
            s.append(FastJsons.toJSONString(obj, prettyFormat));
        }
        //
        else if (obj instanceof LocalDate) {
            s.append(toString(((LocalDate) obj).toDate()));
        } else if (obj instanceof LocalTime) {
            s.append(toString(((LocalTime) obj).toString()));
        } else if (obj instanceof DateTime) {
            s.append(toString(((DateTime) obj).toDate()));
        } else if (obj instanceof LocalDateTime) {
            s.append(toString(((LocalDateTime) obj).toDate()));
        } else if (obj instanceof Collections) {
            List<?> obj_list = (List<?>) obj;
            s.append('[');
            for (int i = 0; i < obj_list.size(); i++) {
                s.append(toString(obj_list.get(i), prettyFormat));
                if (i < obj_list.size() - 1) {
                    s.append(',');
                }
            }
            s.append(']');
        } else if (obj instanceof Map) {
            Map<?, ?> obj_map = (Map<?, ?>) obj;
            List<?> obj_map_keys = Lists.newArrayList(obj_map.keySet());
            s.append('{');
            for (int i = 0; i < obj_map_keys.size(); i++) {
                s.append("'" + obj_map_keys.get(i) + "'" + ":" + toString(obj_map.get(obj_map_keys.get(i)), prettyFormat));
                if (i < obj_map_keys.size() - 1) {
                    s.append(',');
                }
            }
            s.append('}');
        } else if (obj instanceof Object[]) {
            s.append(toString(Lists.newArrayList((Object[]) obj), prettyFormat));
        } else if (obj instanceof Class) {
            Class<?> tmpCls = (Class<?>) obj;
            s.append(tmpCls.getName());
        } else {
            s.append(FastJsons.toJSONString(obj, prettyFormat));
        }
        return s.toString();
    }

    public static String toStringWithToStringBuilder(Object obj) {
        return toStringWithToStringBuilder(obj, ToStringStyle.JSON_STYLE);
    }

    public static String toStringWithToStringBuilder(Object obj, ToStringStyle style) {
        return ToStringBuilder.reflectionToString(obj, style);
    }

}
