package com.example.hemsapp;

public class TasksAverage {
    int counter;
    int hour;
    int minute;

    public TasksAverage(int counter, int hour, int minute) {
        this.counter = counter;
        this.hour = hour;
        this.minute = minute;
    }

    public TasksAverage() {}

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
