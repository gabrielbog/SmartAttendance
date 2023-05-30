package com.gabrielbog.smartattendance.models;

public class Subject {

    private int id;
    private String name;
    private int spec; //specialization
    private int grade; //year
    private String type; //course, laboratory, seminary, project
    private int semester;
    private int attendanceTotal;
    private int absencesAllowed;

    public Subject(int id, String name, int spec, int grade, String type, int semester, int attendanceTotal, int absencesAllowed) {
        this.id = id;
        this.name = name;
        this.spec = spec;
        this.grade = grade;
        this.type = type;
        this.semester = semester;
        this.attendanceTotal = attendanceTotal;
        this.absencesAllowed = absencesAllowed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSpec() {
        return spec;
    }

    public int getGrade() {
        return grade;
    }

    public String getType() {
        return type;
    }

    public int getSemester() {
        return semester;
    }

    public int getAttendanceTotal() {
        return attendanceTotal;
    }

    public int getAbsencesAllowed() {
        return absencesAllowed;
    }

    @Override
    public String toString() {
        return name;
    }
}
