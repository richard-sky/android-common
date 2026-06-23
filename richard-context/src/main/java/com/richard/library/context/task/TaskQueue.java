package com.richard.library.context.task;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: Richard
 * @createDate: 2024/2/18 17:56
 * @version: 1.0
 * @description: 任务队列
 */
public class TaskQueue<T> extends LinkedBlockingQueue<T> {

    public TaskQueue() {
    }

    public TaskQueue(int maxSize) {
        super(maxSize);
    }

    @Override
    public boolean add(T t) {
        return this.offer(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean offer(T t) {
        if (super.offer(t)) {
            return true;
        }
        super.poll();
        return super.offer(t);
    }
}
