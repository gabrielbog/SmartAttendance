package com.gabrielbog.smartattendance.models;

import java.io.Serializable;

public class QrCodeResponse implements Serializable {
    private int code;
    private String qrString;

    public QrCodeResponse(int code, String qrString) {
        this.code = code;
        this.qrString = qrString;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getQrString() {
        return qrString;
    }

    public void setQrString(String qrString) {
        this.qrString = qrString;
    }
}
