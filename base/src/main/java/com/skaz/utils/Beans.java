package com.skaz.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author jungle
 */
public abstract class Beans extends BeanUtils {

    public static <T> T newBean(Object obj, Class<T> clazz) {
        return newBean(obj, clazz, null);
    }

    public static <T> T newBean(Object obj, Class<T> clazz, ObjectMapper mapper) {
        return ObjectMappers.convert(obj, clazz, mapper);
    }

    public static <T> T newBean(Map<String, Object> map, Class<T> clazz) {
        return newBean(map, clazz, null);
    }

    public static <T> T newBean(Map<String, Object> map, Class<T> clazz, ObjectMapper mapper) {
        return ObjectMappers.convert(map, clazz, mapper);
    }

    public static <T> List<T> newList(Object objs, Class<T> clazz) {
        return Casts.toList(objs, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> newList_each_object(Object objs, Class<T> clazz) {
        List<T> datas = Lists.newArrayList();
        for (Object obj : (List<Object>) objs) {
            datas.add(Beans.newBean(obj, clazz));
        }
        return datas;
    }

    public static void copyProperty(Object bean, String name, Object value) {
        try {
            BeanUtils.copyProperty(bean, name, value);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    public static void copyProperties(Object target, Object source) {
        try {
            BeanUtils.copyProperties(target, source);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 复制source中不能为空的对象
     */
    public static void copyPropertiesNotNull(Object target, Object source) {
        try {
            NullAwareBeanUtilsBean.getInstance().copyProperties(target, source);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    private static class NullAwareBeanUtilsBean extends BeanUtilsBean {

        private static NullAwareBeanUtilsBean nullAwareBeanUtilsBean;

        NullAwareBeanUtilsBean() {
            super(new ConvertUtilsBean(), new PropertyUtilsBean() {
            });
        }

        public static NullAwareBeanUtilsBean getInstance() {
            if (nullAwareBeanUtilsBean == null) {
                nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();
            }
            return nullAwareBeanUtilsBean;
        }

        @Override
        public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
            if (value == null){
                return;
            }
            super.copyProperty(bean, name, value);
        }
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

    public static Map<String, Object> toMap(Object bean) {
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

    public static TreeMap<String, Object> toTreeMap(Object bean) {
        Asserts.isNotNull(bean, "bean can not be EMPTY");
        TreeMap<String, Object> map = Maps.newTreeMap();
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

}
