package com.example.myapplication;

public class Faculty {
    String Name;
    String Email;

    public Faculty(String name, String email, String[] courses) {
        Name = name;
        Email = email;
        Courses = courses;
    }

    String Courses[] =  new String[5];

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String[] getCourses() {
        return Courses;
    }

    public void setCourses(String[] courses) {
        Courses = courses;
    }

}
