package com.gabrielbog.smartattendance.models;

import java.sql.Date;
import java.sql.Time;

public class StudentAttendance {
    private int type; //0 - for professors, toString with all data; 1 - for students, ignores the name fields
    private Date date;
    private Time timeStart; //these are used when listing total attendance for all students
    private Time timeStop;
    private String firstName; //change name to firstString - to repurpose into timeStart for students
    private String lastName; //change name to lastString - to repurpose into timeStop for students
    private String state;

    public StudentAttendance(int type, Date date, Time timeStart, Time timeStop, String firstName, String lastName, String state) {
        this.type = type;
        this.date = date;
        this.timeStart = timeStart;
        this.timeStop = timeStop;
        this.firstName = firstName;
        this.lastName = lastName;
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public Time getTimeStop() {
        return timeStop;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        if(type == 0) {
            return lastName + " " + firstName + " - " + state;
        }
        else {
            return date.toString() + " " + timeStart.toString() + "-" + timeStop.toString() + " - " + state;
        }
    }
}
