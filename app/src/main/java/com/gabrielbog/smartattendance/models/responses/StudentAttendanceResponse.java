package com.gabrielbog.smartattendance.models.responses;

import com.gabrielbog.smartattendance.models.StudentAttendance;

import java.io.Serializable;
import java.util.List;

public class StudentAttendanceResponse implements Serializable {
    private int code;
    private int completeCalendarCount;
    private List<StudentAttendance> studentAttendanceList;

    public StudentAttendanceResponse(int code, int completeCalendarCount, List<StudentAttendance> studentAttendanceList) {
        this.code = code;
        this.completeCalendarCount = completeCalendarCount;
        this.studentAttendanceList = studentAttendanceList;
    }

    public int getCode() {
        return code;
    }

    public int getCompleteCalendarCount() {
        return completeCalendarCount;
    }

    public List<StudentAttendance> getStudentAttendanceList() {
        return studentAttendanceList;
    }
}
