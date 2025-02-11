package com.app.demo;

public class Account {
    private String name;
    private String type;
    private String subType;

    public Account(String name, String type, String subType) {
        this.name = name;
        this.type = type;
        this.subType = subType;
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
}
