package com.example.hemsapp;

public class Task {
    private long ID;
    private String name;
    private String definition;
    private Boolean state;
    private String byWho; //UID
    private String type; // enum id si olabilir
    private String where; // room object olursa onun id si gelir. oda kolidor vs vs

    public Task() { } // for firebase

    public Task(String name, String definition, String type, String where) {
        this.name = name;
        this.definition = definition;
        this.state = false;
        this.byWho = "No Body";
        this.type = type;
        this.where = where;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
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

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
