package com.richard.library.context.util.media.player.common;

/**
 * @author: Richard
 * @createDate: 2025/10/31 16:38
 * @version: 1.0
 * @description: 音频资源类型
 */
public interface AudioSourceType {

    /**
     * 音频资源id
     */
    int RAW_ID = 0;

    /**
     * 音频存储路径或者网络音频URL地址
     */
    int PATH_URL = 1;

    /**
     * assets来源音频文件
     */
    int ASSETS_FD = 2;

}
