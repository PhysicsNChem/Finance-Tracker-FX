package com.app.demo;

public class Account {
    private String name;
    private String type;
    private String subType;
    private double accBalance;

    public Account(String name, String type, String subType, double accBalance) {
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.accBalance = accBalance;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getSubType() {
        return subType;
    }
    public double getAccBalance() {
        return accBalance;
    }
    public void setAccBalance(double accBalance) {
        this.accBalance = accBalance;
    }
}
