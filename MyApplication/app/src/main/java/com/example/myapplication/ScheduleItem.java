package com.example.myapplication;

import java.util.Date;

public class ScheduleItem {
    private String code;
    private String faculty;
    private String location;
    private String roomNo;
    private Date start;
    private Date end;

    public ScheduleItem() {}

    public ScheduleItem(String code, String faculty, String location, String roomNo, Date start, Date end) {
        this.code = code;
        this.faculty = faculty;
        this.location = location;
        this.roomNo = roomNo;
        this.start = start;
        this.end = end;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
