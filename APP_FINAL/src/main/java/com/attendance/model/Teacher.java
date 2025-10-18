package com.attendance.model;

public class Teacher {
    private int id;
    private String name;
    private String username;
    private String password;
    private double latitude;
    private double longitude;

    public Teacher() {}

    public Teacher(int id, String name, String username, String password, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}


