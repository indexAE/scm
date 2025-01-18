package com.scm.utils;

import java.util.List;

public class PageData<T> {
    private List<T> list;  // 数据列表
    private int total;     // 总数

    public PageData(List<T> list, int total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
} 