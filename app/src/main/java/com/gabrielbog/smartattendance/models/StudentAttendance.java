package com.gabrielbog.smartattendance.models;

import java.sql.Date;

public class StudentAttendance {
    private int type; //0 - for professors, toString with all data; 1 - for students, ignores the name fields
    private Date date;
    private String firstName; //change name to firstString - to repurpose into timeStart for students
    private String lastName; //change name to lastString - to repurpose into timeStop for students
    private String state;

    public StudentAttendance(int type, Date date, String firstName, String lastName, String state) {
        this.type = type;
        this.date = date;
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
            return "StudentAttendance{" +
                    "date=" + date +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", state='" + state + '\'' +
                    '}';
        }
        else {
            return date + " " + state;
        }
    }
}
