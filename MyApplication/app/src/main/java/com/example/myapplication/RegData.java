package com.example.myapplication;


public class RegData {
    private String Course;
    private String RegNo;
    private String Name;
    private String Mobile;

    public RegData(String course, String regNo, String name, String mobile) {
        Course = course;
        RegNo = regNo;
        Name = name;
        Mobile = mobile;
    }
    public String getCourse() {
        return Course;
    }

    public String getRegNo() {
        return RegNo;
    }

    public String getName() {
        return Name;
    }

    public String getMobile() {
        return Mobile;
    }
}