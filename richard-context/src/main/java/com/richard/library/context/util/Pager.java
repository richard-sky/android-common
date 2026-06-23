package com.richard.library.context.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Richard
 * @createDate: 2024/8/12 10:59
 * @version: 1.0
 * @description: 分页
 */
public class Pager<T> {

    private final List<T> data;
    private final int pageSize;
    private int pageNo = 1;


    public Pager(List<T> data, int pageSize) {
        this.data = data == null ? new ArrayList<>() : data;
        this.pageSize = pageSize;
    }

    /**
     * 上一页
     */
    public List<T> lastPage() {
        return this.page(pageNo - 1);
    }

    /**
     * 下一页
     */
    public List<T> nextPage() {
        return this.page(pageNo + 1);
    }

    /**
     * 获取某页数据，从第1页开始
     *
     * @param pageNo 页码 从1开始
     * @return 分页数据
     */
    public List<T> page(int pageNo) {
        if (pageNo < 1) {
            pageNo = 1;
        }

        if(pageNo > this.getPageSize()){
            return new ArrayList<>();
        }

        this.pageNo = pageNo;

        int from = (pageNo - 1) * pageSize;
        int to = Math.min(pageNo * pageSize, data.size());
        if (from > to) {
            from = to;
        }

        return data.subList(from, to);
    }

    /**
     * 获取数据
     */
    public List<T> getData() {
        return data;
    }

    /**
     * 获取当前页码
     */
    public int getPageNo(){
        return this.pageNo;
    }

    /**
     * 获取总页数
     */
    public int getPageSize() {
        if (pageSize == 0) {
            return 0;
        }
        return data.size() % pageSize == 0 ? (data.size() / pageSize) : (data.size() / pageSize + 1);
    }
}
