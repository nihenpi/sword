package com.skaz.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jungle
 */
public class Maps {

    public static <K, V> Map newHashMap(Object... objs) {
        if (objs.length > 2 && objs.length % 2 != 0) {
            throw new IllegalArgumentException("Odd number of arguments");
        }
        Map<K, V> map = Maps.newHashMap();
        Object key = null;
        Integer step = -1;
        for (Object obj : objs) {
            step++;
            switch (step % 2) {
                case 0:
                    if (obj == null) {
                        throw new IllegalArgumentException("Null key value");
                    }
                    key = obj;
                    continue;
                case 1:
                    map.put((K) key, (V) obj);
                    break;
            }
        }
        return map;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static final class MapBuilder<K, V> {

        private final HashMap<K, V> map;

        public MapBuilder(K k, V v) {
            map = new HashMap<K, V>();
            map.put(k, v);
        }

        public MapBuilder<K, V> put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }

    }

    public static <K, V> MapBuilder<K, V> put(K k, V v) {
        return new MapBuilder<K, V>(k, v);
    }

    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
    }

    public static <K, V> HashMap<K, V> newHashMap(Object bean) {
        return newHashMap(bean, null);
    }

    public static <K, V> HashMap<K, V> newHashMap(Object bean, ObjectMapper mapper) {
        return ObjectMappers.convert(bean, HashMap.class, mapper);
    }

    public static <K, V> HashMap<K, V> newHashMap(K key, V value) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }

    public static HashMap<String, String> toHashMap(Map<String, String[]> map) {
        HashMap<String, String> params = newHashMap();
        map.forEach((key, val) -> {
            params.put(key, Strings.join(val, ","));
        });
        return params;
    }

    public static Map<String, String> newHashMapFromQueryString(String query) {
        return Strings.toQueryMap(query);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return com.google.common.collect.Maps.newConcurrentMap();
    }

    /**
     * 创建同步Map
     */
    public static <K, V> Map<K, V> newSynchronizedMap(Map<K, V> m) {
        return Collections.synchronizedMap(m);
    }

    /**
     * 根据Key获取元素并删除该元素在原始Map中
     */
    public static <T> T getAndRemove(Map<String, T> map, String key) {
        T value = null;
        if (map.containsKey(key)) {
            value = map.get(key);
            map.remove(key);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> void remove(Map<String, T> map, T... keys) {
        for (T key : keys) {
            map.remove(key);
        }
    }

    public static String toQueryString(Map<String, ?> queryMap) {
        return toQueryString(null, queryMap);
    }

    public static String toQueryString(String url, Map<String, ?> queryMap) {
        StringBuffer query = new StringBuffer();
        boolean isAppendWithUrl = true;
        if (Strings.isNotNullOrEmpty(url)) {
            query = new StringBuffer(url);
            String lastSeperator = Strings.substringAfterLast(url, "/");
            if (Strings.isContains(lastSeperator, "?")) {
                isAppendWithUrl = false;
            }
        }
        Set<String> keys = queryMap.keySet();
        for (String key : keys) {
            String value = String.valueOf(queryMap.get(key));
            if (Strings.isNotNullOrEmpty(key)) {
                if (isAppendWithUrl) {
                    query.append("?");
                    isAppendWithUrl = false;
                } else {
                    query.append("&");
                }
                query.append(Encodes.encodeUrl(key)).append("=").append(Encodes.encodeUrl(value));
            }
        }
        return query.toString();
    }

    public static String toPostQueryString(Map<String, ?> queryMap) {
        StringBuffer query = new StringBuffer();
        boolean isAppendWithUrl = true;
        Set<String> keys = queryMap.keySet();
        for (String key : keys) {
            String value = String.valueOf(queryMap.get(key));
            if (Strings.isNotNullOrEmpty(key)) {
                if (isAppendWithUrl) {
                    isAppendWithUrl = false;
                } else {
                    query.append("&");
                }
                query.append(Encodes.encodeUrl(key)).append("=").append(Encodes.encodeUrl(value));
            }
        }
        return query.toString();
    }

    public static String toPostNotNullAndNotEncodeQueryString(Map<String, ?> queryMap) {
        StringBuffer query = new StringBuffer();
        boolean isAppendWithUrl = true;
        Set<String> keys = queryMap.keySet();
        for (String key : keys) {
            Object valObj = queryMap.get(key);
            if (Strings.isNotNullOrEmpty(key) && Strings.isNotNullOrEmpty(valObj)) {
                String value = String.valueOf(valObj);
                if (isAppendWithUrl) {
                    isAppendWithUrl = false;
                } else {
                    query.append("&");
                }
                query.append(key).append("=").append(value);
            }
        }
        return query.toString();
    }

}
