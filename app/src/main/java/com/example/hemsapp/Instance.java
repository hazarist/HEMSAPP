package com.example.hemsapp;


public class Instance {
    String type;
    String priority;
    String evaluation;
    String byWho;

    public Instance(String type,  String evaluation,String priority, String byWho) {
        this.type = type;
        this.priority = priority;
        this.evaluation = evaluation;
        this.byWho = byWho;
    }

    public Instance(){}

    public String getType() {
        return type;
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

