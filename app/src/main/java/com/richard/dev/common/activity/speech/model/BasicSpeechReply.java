package com.richard.dev.common.activity.speech.model;

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

    /// 向前兼容（sessionId替代了）待废弃
    @Deprecated
    private String contextId;

    /// uuid，服务端通过相同的sessionId关联多轮请求的上下文；首轮对话请求不需要携带；非首轮对话请求取值是上一轮服务端返回结果中的sessionId
    private String sessionId;

    /// 对话结果topic (v3 接口新增)
    private String topic;

    /// 发送错误时的错误信息
    private Error error;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
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
