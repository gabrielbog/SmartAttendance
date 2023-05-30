package com.gabrielbog.smartattendance.models.responses;

import com.gabrielbog.smartattendance.models.Subject;

import java.io.Serializable;
import java.util.List;

public class SubjectListResponse implements Serializable {
    private int code;
    private List<Subject> subjectList;

    public SubjectListResponse(int code, List<Subject> subjectList) {
        this.code = code;
        this.subjectList = subjectList;
    }

    public int getCode() {
        return code;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }
}
