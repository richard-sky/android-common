package com.richard.library.net.http.model;

import java.io.Serializable;


/**
 * <pre>
 * Description : 上传文件的携带信息
 * Author : admin-richard
 * Date : 2019-05-27 08:28
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-27 08:28     admin-richard         new file.
 * </pre>
 */
public class UploadFile implements Serializable {

    private static final long serialVersionUID = -6228297591333508623L;

    /**
     * 上传文件接口对应的name
     */
    private String name;

    /**
     * 文件路径或者url
     */
    private String path;

    public UploadFile() {
    }

    public UploadFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
