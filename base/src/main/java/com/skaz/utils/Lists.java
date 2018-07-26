package com.skaz.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jungle
 */
public abstract class Lists {

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    @SuppressWarnings("unchecked")
    public static <E> ArrayList<E> newArrayList(E... elements) {
        return com.google.common.collect.Lists.newArrayList(elements);
    }

    public static <E> List<E> newSynchronizedList(List<E> list) {
        return Collections.synchronizedList(list);
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return com.google.common.collect.Lists.newCopyOnWriteArrayList();
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> elements) {
        return com.google.common.collect.Lists.newCopyOnWriteArrayList(elements);
    }

    /**
     * 是否为null
     */
    public static boolean isNull(List<?> list) {
        return list == null;
    }

    /**
     * 是否不为null
     */
    public static boolean isNotNull(List<?> list) {
        return !isNull(list);
    }

    /**
     * 是否为空
     */
    public static boolean isEmpty(List<?> list) {
        if (isNull(list) || list.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否不为空
     * size>0
     */
    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

    /**
     * 删除元素
     */
    public static <E> void remove(List<E> list, List<E> removes) {
        Iterator<E> iter = list.iterator();
        while (iter.hasNext()) {
            E e = iter.next();
            if (removes.contains(e)) {
                iter.remove();
            }
        }
    }

    /**
     * 删除元素
     */
    @SuppressWarnings("unchecked")
    public static <E> void remove(List<E> list, E... removes) {
        remove(list, newArrayList(removes));
    }

    /**
     * 获取并删除原list中的i元素
     */
    public static <E> E getAndRemove(List<E> list, int i) {
        E e = list.get(i);
        list.remove(i);
        return e;
    }

    /**
     * 反转
     */
    public static void reverse(List<?> list) {
        Collections.reverse(list);
    }

    /***
     * 集合变数组
     */
    public static String[] toStringArray(List<?> objs) {
        return objs.toArray(new String[objs.size()]);
    }

    /***
     * 数组变集合
     */
    public static <E> List<E> fromArray(E[] arr) {
        return com.google.common.collect.Lists.newArrayList(arr);
    }

    public static <T> List<List<T>> split(List<T> list, int count) {
        if (list == null || count < 1) {
            return null;
        }
        List<List<T>> lists = new ArrayList<List<T>>();
        int size = list.size();
        if (size <= count) {
            lists.add(list);
        } else {
            int pre = size / count;
            int last = size % count;
            // 前面pre个集合，每个大小都是count个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<T>();
                for (int j = 0; j < count; j++) {
                    itemList.add(list.get(i * count + j));
                }
                lists.add(itemList);
            }
            // last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<T>();
                for (int i = 0; i < last; i++) {
                    itemList.add(list.get(pre * count + i));
                }
                lists.add(itemList);
            }
        }
        return lists;

    }

}
