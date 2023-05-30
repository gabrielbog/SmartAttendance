package com.gabrielbog.smartattendance.models.responses;

import com.gabrielbog.smartattendance.models.StudentAttendance;

import java.io.Serializable;
import java.util.List;

public class StudentAttendanceResponse implements Serializable {
    private int code;
    private List<StudentAttendance> studentAttendanceList;

    public StudentAttendanceResponse(int code, List<StudentAttendance> studentAttendanceList) {
        this.code = code;
        this.studentAttendanceList = studentAttendanceList;
    }

    public int getCode() {
        return code;
    }

    public List<StudentAttendance> getStudentAttendanceList() {
        return studentAttendanceList;
    }
}
