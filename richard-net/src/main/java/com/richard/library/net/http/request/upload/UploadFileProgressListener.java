package com.richard.library.net.http.request.upload;


/**
 * <pre>
 * Description : 文件上传进度监听
 * Author : admin-richard
 * Date : 2019-05-20 19:48
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-20 19:48     admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface UploadFileProgressListener {

    /**
     * 计算百分比
     *
     * @param total        总大小
     * @param uploadedSize 已上传文件大小
     * @return 百分率值
     */
    default int getProgress(long total, long uploadedSize) {
        return (int) (uploadedSize * 1.0 / total * 100.0);
    }

    /**
     * 上传文件进度回调
     * 取整（已上传文件大小/上传文件大小*100）（计算百分比）
     *
     * @param total        总大小
     * @param uploadedSize 已上传文件大小
     */
    void onUploadFileProgress(long total, long uploadedSize);
}
