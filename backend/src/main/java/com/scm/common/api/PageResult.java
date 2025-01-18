package com.scm.common.api;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;

    public PageResult() {
    }

    public PageResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }
} 