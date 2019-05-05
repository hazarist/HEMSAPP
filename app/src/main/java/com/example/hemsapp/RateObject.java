package com.example.hemsapp;


public class RateObject {

    String current;
    String target;
    double rate;

    public RateObject(String current, String target, double rate) {
        this.current = current;
        this.target = target;
        this.rate = rate;
    }

    public RateObject(String current, double rate) {
        this.current = current;
        this.target = "total";
        this.rate = rate;
    }

    public String getCurrent() {
        return current;
    }
    public void setCurrent(String current) {
        this.current = current;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public double getRate() {
        return rate;
    }
    public void setRate(double probability) {
        this.rate = probability;
    }

}

