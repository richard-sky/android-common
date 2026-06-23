package com.richard.dev.common.mvvm.common;

/**
 * @author: Richard
 * @createDate: 2022/6/29 15:43
 * @version: 1.0
 * @description: 指令定义
 */
public enum Command {
    SHOW_MSG,//提示消息
    ON_START,//当某业务开始时
    ON_COMPLETE,//当某业务处理完成时
    ON_ERROR;//当某业务处理发生错误时
}
