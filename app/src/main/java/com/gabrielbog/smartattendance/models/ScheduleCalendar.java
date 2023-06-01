package com.gabrielbog.smartattendance.models;

import java.sql.Date;
import java.sql.Time;

public class ScheduleCalendar {

    private int id; //schedule id
    private Date date;
    private Time timeStart;
    private Time timeStop;
    private int grup;

    public ScheduleCalendar(int id, Date date, Time timeStart, Time timeStop, int grup) {
        this.id = id;
        this.date = date;
        this.timeStart = timeStart;
        this.timeStop = timeStop;
        this.grup = grup;
    }

    public int getId() {
        return id;
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

    public int getGrup() {
        return grup;
    }

    @Override
    public String toString() {
        if (id != -1) {
            return date.toString() + " " + timeStart.toString() + "-" + timeStop.toString() + " Group " + grup;
        }
        else {
            return "Select a schedule date";
        }
    }
}
