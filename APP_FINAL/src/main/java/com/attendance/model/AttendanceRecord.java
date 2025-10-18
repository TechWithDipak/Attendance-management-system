package com.attendance.model;

import java.time.LocalDate;

public class AttendanceRecord {
    private int id;
    private int studentId;
    private int teacherId;
    private String course;
    private LocalDate date;
    private String status;
    private String location;

    public AttendanceRecord() {}

    public AttendanceRecord(int id, int studentId, int teacherId, String course, LocalDate date, String status, String location) {
        this.id = id;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.course = course;
        this.date = date;
        this.status = status;
        this.location = location;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}


