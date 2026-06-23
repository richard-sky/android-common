package com.richard.library.context.util.media;

import java.io.Serializable;

/**
 * @author: Richard
 * @createDate: 2025/10/31 16:36
 * @version: 1.0
 * @description: 音频项目
 */
public class AudioItem implements Serializable {

    /// 音频资源
    private String source;

    /// 音频资源类型
    ///
    /// @see AudioSourceType
    private int mediaSourceType;

    public AudioItem() {
    }

    public AudioItem(String source, int mediaSourceType) {
        this.source = source;
        this.mediaSourceType = mediaSourceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getMediaSourceType() {
        return mediaSourceType;
    }

    public void setMediaSourceType(int mediaSourceType) {
        this.mediaSourceType = mediaSourceType;
    }

}
