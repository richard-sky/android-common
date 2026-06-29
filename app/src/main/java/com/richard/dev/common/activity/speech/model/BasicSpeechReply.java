package com.richard.dev.common.activity.speech.model;

import com.richard.library.context.util.ObjectUtilKt;
import com.richard.library.context.util.StringUtilKt;

import java.io.Serializable;

/**
 * @author: Richard
 * @createDate: 2026/6/25 18:23
 * @version: 1.0
 * @description: 思必驰对话回复基本信息
 */
public class BasicSpeechReply implements Serializable {

    /// uuid，标识一次请求，32字符
    private String recordId;

    /// 对话结果topic (v3 接口新增)
    private String topic;

    /// 发送错误时的错误信息
    private Error error;

    /// 错误信息
    private String errMsg;

    /// 错误id
    private String errId;

    /**
     * 验证当前服务器回复消息是否属于该指定请求
     *
     * @param recordId 请求时指定的id
     */
    public boolean isRecordId(Object recordId) {
        return this.recordId.startsWith(ObjectUtilKt.toString(recordId));
    }

    /**
     * 验证是否属于指定topic
     */
    public boolean isTopic(String topic) {
        return StringUtilKt.eqVal(this.topic, topic);
    }

    /**
     * 是否为错误消息
     */
    public boolean isError() {
        return error != null || errId != null;
    }

    /**
     * 获取错误信息
     */
    public String getErrMsg() {
        if (error != null) {
            return error.getErrMsg();
        }
        return errMsg;
    }

    /**
     * 获取错误id
     */
    public String getErrId() {
        if (error != null) {
            return error.getErrId();
        }
        return errId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setErrId(String errId) {
        this.errId = errId;
    }

    public static class Error implements Serializable {
        private String errMsg;
        private String errId;

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }

        public String getErrId() {
            return errId;
        }

        public void setErrId(String errId) {
            this.errId = errId;
        }
    }

}
