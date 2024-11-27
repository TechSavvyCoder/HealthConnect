package com.example.healthconnect;

public class Appointment {
    private int id;
    private String date;

    public Appointment(int id, String date) {
        this.id = id;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }
}
