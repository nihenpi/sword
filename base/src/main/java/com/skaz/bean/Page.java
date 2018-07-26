package com.skaz.bean;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author jungle
 */
public class Page<E> implements Serializable {

    @Getter
    private int no;
    @Getter
    private int size;
    @Getter
    private long total;
    @Getter
    private List<E> data = new ArrayList<>();
    @Getter
    private boolean isEmpty;

    public Page(List<E> data, int no, int size, long total) {
        if (no < 1) {
            throw new IllegalArgumentException("参数错误:no < 1");
        }
        if (size == 0) {
            throw new IllegalArgumentException("参数错误:size = 0");
        }
        this.isEmpty = size == 0;
        this.no = no;
        this.size = Math.abs(size);
        this.total = total;
        this.data.addAll(data);
    }

    public boolean hasPrevious() {
        return this.no > 1;
    }

    public boolean isFirst() {
        return !hasPrevious();
    }

    public boolean isLast() {
        return !hasNext();
    }

    public boolean hasNext() {
        return this.no < getTotalPages();
    }

    public int getTotalPages() {
        return getSize() == 0 ? 1 : ((int) Math.ceil((double) total / (double) getSize()));
    }
}
