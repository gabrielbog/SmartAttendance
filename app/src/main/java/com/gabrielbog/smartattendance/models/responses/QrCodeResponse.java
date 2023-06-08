package com.gabrielbog.smartattendance.models.responses;

import java.io.Serializable;

public class QrCodeResponse implements Serializable {
    private int code;
    private long duration; //in milliseconds
    private String qrString;
    private String subjectString;
    private int grup;

    public QrCodeResponse(int code, long duration, String qrString, String additionalString, int grup) {
        this.code = code;
        this.duration = duration;
        this.qrString = qrString;
        this.subjectString = additionalString;
        this.grup = grup;
    }

    public int getCode() {
        return code;
    }

    public long getDuration() {
        return duration;
    }

    public String getQrString() {
        return qrString;
    }

    public String getSubjectString() {
        return subjectString;
    }

    public int getGrup() {
        return grup;
    }
}
