package com.gabrielbog.smartattendance.models.responses;

import java.io.Serializable;

public class LogInResponse implements Serializable {
    private int code;
    private int id;
    private int isAdmin;
    private String firstName;
    private String lastName;

    public LogInResponse(int code, int id, int isAdmin, String firstName, String lastName) {
        this.code = code;
        this.id = id;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
