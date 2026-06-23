package com.richard.library.net.ftp.common;

/**
 * @author: Richard
 * @createDate: 2026/6/8 10:43
 * @version: 1.0
 * @description: 服务器特性
 */
public class ServerFeatures {

    private String systemType;
    private boolean supportsMLSD;
    private boolean supportsUTF8;
    private boolean supportsSize;
    private boolean supportsMDTM;

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public boolean isSupportsMLSD() {
        return supportsMLSD;
    }

    public void setSupportsMLSD(boolean supportsMLSD) {
        this.supportsMLSD = supportsMLSD;
    }

    public boolean isSupportsUTF8() {
        return supportsUTF8;
    }

    public void setSupportsUTF8(boolean supportsUTF8) {
        this.supportsUTF8 = supportsUTF8;
    }

    public boolean isSupportsSize() {
        return supportsSize;
    }

    public void setSupportsSize(boolean supportsSize) {
        this.supportsSize = supportsSize;
    }

    public boolean isSupportsMDTM() {
        return supportsMDTM;
    }

    public void setSupportsMDTM(boolean supportsMDTM) {
        this.supportsMDTM = supportsMDTM;
    }

}
