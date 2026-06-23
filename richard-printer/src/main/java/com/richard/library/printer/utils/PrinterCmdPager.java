package com.richard.library.printer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Description : 打印机指令分页
 * Author : PHILIPS
 * Date : 2023/4/6 16:06
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2023/4/6 16:06      PHILIPS         new file.
 * </pre>
 */
public class PrinterCmdPager {
    private final List<Byte> data;
    private final int pageSize;


    public PrinterCmdPager(byte[] bytes, int pageSize) {
        this.data = new ArrayList<>();
        for (byte b : bytes) {
            data.add(b);
        }
        this.pageSize = pageSize;
    }

    /**
     * 获取某页数据，从第1页开始
     *
     * @param pageNum 第几页
     * @return 分页数据
     */
    public byte[] page(int pageNum) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(pageNum * pageSize, data.size());
        if (from > to) {
            from = to;
        }

        List<Byte> subList = data.subList(from, to);
        byte[] result = new byte[subList.size()];
        for (int i = 0; i < subList.size(); i++) {
            result[i] = subList.get(i);
        }
        return result;
    }

    /**
     * 获取总页数
     */
    public int getPageCount() {
        if (pageSize == 0) {
            return 0;
        }
        return data.size() % pageSize == 0 ? (data.size() / pageSize) : (data.size() / pageSize + 1);
    }
}


