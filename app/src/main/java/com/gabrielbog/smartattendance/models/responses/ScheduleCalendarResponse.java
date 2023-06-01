package com.gabrielbog.smartattendance.models.responses;

import com.gabrielbog.smartattendance.models.ScheduleCalendar;

import java.io.Serializable;
import java.util.List;

public class ScheduleCalendarResponse implements Serializable {

    private int code;
    private List<ScheduleCalendar> scheduleCalendarList;

    public ScheduleCalendarResponse(int code, List<ScheduleCalendar> scheduleCalendarList) {
        this.code = code;
        this.scheduleCalendarList = scheduleCalendarList;
    }

    public int getCode() {
        return code;
    }

    public List<ScheduleCalendar> getScheduleCalendarList() {
        return scheduleCalendarList;
    }
}
