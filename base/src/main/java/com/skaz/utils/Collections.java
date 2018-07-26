package com.skaz.utils;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.*;

public abstract class Collections {

    public static final <T> List<T> emptyList() {
        return java.util.Collections.emptyList();
    }

    public static final <T> Set<T> emptySet() {
        return java.util.Collections.emptySet();
    }

    public static <T> Iterator<T> emptyIterator() {
        return java.util.Collections.emptyIterator();
    }

    public static <T> List<T> unmodifiableList(List<? extends T> list) {
        return java.util.Collections.unmodifiableList(list);
    }

    public static byte[][] convertByteListToByteArray(List<byte[]> args) {
        return args.toArray(new byte[args.size()][0]);
    }

    /**
     * 提取集合中的对象的两个属性(通过Getter函数), 组合成Map.
     *
     * @param collection        来源集合.
     * @param keyPropertyName   要提取为Map中的Key值的属性名.
     * @param valuePropertyName 要提取为Map中的Value值的属性名.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map extractToMap(final Collection collection, final String keyPropertyName, final String valuePropertyName) {
        Map map = new HashMap(collection.size());

        try {
            for (Object obj : collection) {
                map.put(PropertyUtils.getProperty(obj, keyPropertyName), PropertyUtils.getProperty(obj, valuePropertyName));
            }
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }

        return map;
    }

    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成List.
     *
     * @param collection   来源集合.
     * @param propertyName 要提取的属性名.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List extractToList(final Collection collection, final String propertyName) {
        List list = new ArrayList(collection.size());

        try {
            for (Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }

        return list;
    }

    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成由分割符分隔的字符串.
     *
     * @param collection   来源集合.
     * @param propertyName 要提取的属性名.
     * @param separator    分隔符.
     */
    @SuppressWarnings({"rawtypes"})
    public static String extractToString(final Collection collection, final String propertyName, final String separator) {
        List list = extractToList(collection, propertyName);
        return Strings.join(list, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    @SuppressWarnings({"rawtypes"})
    public static String convertToString(final Collection collection, final String separator) {
        return Strings.join(collection, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
     */
    @SuppressWarnings({"rawtypes"})
    public static String convertToString(final Collection collection, final String prefix, final String postfix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(postfix);
        }
        return builder.toString();
    }

    /**
     * 判断是否为空.
     */
    @SuppressWarnings({"rawtypes"})
    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断是否不为空.
     */
    @SuppressWarnings({"rawtypes"})
    public static boolean isNotEmpty(Collection collection) {
        return (collection != null && !(collection.isEmpty()));
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or EMPTY.
     * Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is EMPTY
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or EMPTY.
     * Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is EMPTY
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return (map != null && !(map.isEmpty()));
    }

    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        // 当类型为List时，直接取得最后一个元素 。
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }

        // 其他类型通过iterator滚动到最后一个元素.
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }

    /**
     * 返回a+b的新List.
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        List<T> result = new ArrayList<T>(a);
        result.addAll(b);
        return result;
    }

    /**
     * 返回a-b的新List.
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        List<T> list = new ArrayList<T>(a);
        for (T element : b) {
            list.remove(element);
        }

        return list;
    }

    /**
     * 返回a与b的交集的新List.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<T>();

        for (T element : a) {
            if (b.contains(element)) {
                list.add(element);
            }
        }
        return list;
    }

    public static void shuffle(List<?> list) {
        java.util.Collections.shuffle(list);
    }

    public static <K, V> Map<K, V> synchronizedMap(Map<K, V> m) {
        return java.util.Collections.synchronizedMap(m);
    }

    public static <T> List<T> synchronizedList(List<T> list) {
        return java.util.Collections.synchronizedList(list);
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        java.util.Collections.sort(list);
    }

}
