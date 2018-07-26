package com.skaz.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Set;

/**
 * Arrays工具类
 */
public abstract class Arrays extends ArrayUtils {

    public static String[] newStringArray(Set<String> set) {
        return set.toArray(new String[set.size()]);
    }

    /**
     * 判断数组是否非空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !ArrayUtils.isEmpty(array);
    }

    /**
     * 判断对象是否在数组中
     */
    public static <T> boolean isContains(T[] array, T obj) {
        return ArrayUtils.contains(array, obj);
    }

    /**
     * 判断数组是否相等
     */
    public static boolean isEquals(Object[] array1, Object[] array2) {
        return java.util.Arrays.equals(array1, array2);
    }

    /**
     * 判断数组是否相等
     */
    public static boolean isEqualsIgnoreOrder(Object[] array1, Object[] array2) {
        java.util.Arrays.sort(array1);
        java.util.Arrays.sort(array2);
        return java.util.Arrays.equals(array1, array2);
    }

    /**
     * 添加
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] add(final T[] array, final T... elements) {
        return ArrayUtils.addAll(array, elements);
    }

    /**
     * 连接数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(final T[] array, final T... elements) {
        return ArrayUtils.addAll(array, elements);
    }

    /**
     * 删除
     */
    public static <T> T[] remove(final T[] array, final int index) {
        return ArrayUtils.remove(array, index);
    }

    /**
     * 删除
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] remove(final T[] array, final T... elements) {
        return ArrayUtils.removeElements(array, elements);
    }

    @SafeVarargs
    public static <T> List<T> asList(T... a) {
        return java.util.Arrays.asList(a);
    }

    public static String deepToString(Object[] a) {
        return java.util.Arrays.deepToString(a);
    }

}
