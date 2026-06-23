package com.richard.library.context.simple;

import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * <pre>
 * Description : 自定义简单异常构造，减少异常抛出的性能消耗
 * Author : admin-richard
 * Date : 2019-09-20 09:04
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-09-20 09:04     admin-richard         new file.
 * </pre>
 */
public class SimpleException extends RuntimeException {

    private static final long serialVersionUID = -6276372702307734770L;
    /**
     * 异常抛出业务类型
     */
    private int type;

    /**
     * 异常抛出携带数据对象
     */
    private Object data;

    /**
     * 原始异常信息对象
     */
    private Throwable originException;

    public SimpleException(String message) {
        super(message);
    }

    public SimpleException(String message, Throwable originException) {
        super(message);
        this.originException = originException;
    }

    /**
     * @param type    异常抛出业务类型
     * @param data    携带数据对象
     * @param message 异常的描述信息
     */
    public SimpleException(int type, Object data, String message) {
        super(message);
        this.type = type;
        this.data = data;
    }

    public SimpleException(int type, Object data, String message, Throwable originException) {
        super(message);
        this.type = type;
        this.data = data;
        this.originException = originException;
    }

    /**
     * @param message 异常的描述信息
     * @param clazz   调用者的class
     */
    public SimpleException(String message, Class clazz) {
        this(TextUtils.isEmpty(message)
                ? String.format("[%s]", clazz.toString())
                : String.format(message.concat("-[%s]"), clazz.toString())
        );
    }

    /**
     * @param message 异常的描述信息
     * @param clazz   调用者的class
     */
    public SimpleException(String message, Class clazz, Throwable originException) {
        this(TextUtils.isEmpty(message)
                ? String.format("[%s]", clazz.toString())
                : String.format(message.concat("-[%s]"), clazz.toString())
        );
        this.originException = originException;
    }

    /**
     * 验证异常是否属于指定类型
     *
     * @param e    异常信息
     * @param type 异常类型标识
     */
    public static boolean isType(Throwable e, int type) {
        if (!(e instanceof SimpleException)) {
            return false;
        }

        return ((SimpleException) e).getType() == type;
    }

    /**
     * 获取异常信息的data数据
     *
     * @param e 异常信息
     */
    public static Object getData(Throwable e) {
        if (!(e instanceof SimpleException)) {
            return null;
        }

        return ((SimpleException) e).getData();
    }

    /**
     * 获取异常抛出业务类型
     */
    public int getType() {
        return type;
    }

    /**
     * 获取异常抛出携带数据对象
     */
    public Object getData() {
        return data;
    }

    @NonNull
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public void setOriginException(Throwable originException) {
        this.originException = originException;
    }

    public Throwable getOriginException() {
        return originException;
    }
}
