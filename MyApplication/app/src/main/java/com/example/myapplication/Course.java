package com.example.myapplication;

import java.util.List;

public class Course {
    private String code;
    private String name;
    private String faculty;
    private List<String> students;

    public Course() {}

    public Course(String code, String name, String faculty, List<String> students) {
        this.code = code;
        this.name = name;
        this.faculty = faculty;
        this.students = students;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }
}
