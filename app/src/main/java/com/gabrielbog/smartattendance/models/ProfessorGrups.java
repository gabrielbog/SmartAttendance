package com.gabrielbog.smartattendance.models;

public class ProfessorGrups {
    private int grup; //this class exists for list clarity when using Postman

    public ProfessorGrups(int grup) {
        this.grup = grup;
    }

    public int getGrup() {
        return grup;
    }

    @Override
    public String toString() {
        return "Group " + grup;
    }
}
