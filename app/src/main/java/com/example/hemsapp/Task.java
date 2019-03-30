package com.example.hemsapp;

public class Task {

    private String name;
    private String description;
    private String state;
    private String byWho; //UID
    private String type; // enum id si olabilir
    private String location; // room object olursa onun id si gelir. oda kolidor vs vs
    private String priority;
    private long taskCreateTime;
    private long taskAssignTime;
    private long taskDoneTime;
    private String isDeleted;

    public Task() { } // for firebase

    public Task(String name, String description, String type, String location, String priority,String isDeleted) {
        this.name = name;
        this.description = description;
        this.state = "waiting";
        this.byWho = "It is in queue.";
        this.type = type;
        this.location = location;
        this.priority = priority;
        this.isDeleted = isDeleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getByWho() {
        return byWho;
    }

    public void setByWho(String byWho) {
        this.byWho = byWho;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public long getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(long taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public long getTaskAssignTime() {
        return taskAssignTime;
    }

    public void setTaskAssignTime(long taskAssignTime) {
        this.taskAssignTime = taskAssignTime;
    }

    public long getTaskDoneTime() {
        return taskDoneTime;
    }

    public void setTaskDoneTime(long taskDoneTime) {
        this.taskDoneTime = taskDoneTime;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
}
