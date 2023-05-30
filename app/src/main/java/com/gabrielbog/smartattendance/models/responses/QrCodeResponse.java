package com.gabrielbog.smartattendance.models.responses;

import java.io.Serializable;

public class QrCodeResponse implements Serializable {
    private int code;
    private String qrString;
    private String additionalString;

    public QrCodeResponse(int code, String qrString, String additionalString) {
        this.code = code;
        this.qrString = qrString;
        this.additionalString = additionalString;
    }

    public int getCode() {
        return code;
    }

    public String getQrString() {
        return qrString;
    }

    public String getAdditionalString() {
        return additionalString;
    }
}
