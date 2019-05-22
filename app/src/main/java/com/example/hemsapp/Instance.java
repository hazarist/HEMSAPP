package com.example.hemsapp;


public class Instance {
    private String type;
    private String subtype;
    private String priority;
    private String evaluation;
    private String byWho;

    public Instance(String type,String subtype,  String evaluation,String priority, String byWho) {
        this.type = type;
        this.subtype = subtype;
        this.priority = priority;
        this.evaluation = evaluation;
        this.byWho = byWho;
    }

    public Instance(){}

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }
    public String getPriority() {
        return priority;
    }
    public String getEvaluation() {
        return evaluation;
    }
    public String getByWho() {
        return byWho;
    }

}

