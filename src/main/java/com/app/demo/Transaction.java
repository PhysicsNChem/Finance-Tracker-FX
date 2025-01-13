// Java
package com.app.demo;

public class Transaction {
    private String date;
    private String description;
    private double amount;
    private Category category;
    private String incomeExpense;
    private String payer;

    public Transaction(String date, String description, double amount, Category category, String incomeExpense, String payer) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.incomeExpense = incomeExpense;
        this.payer = payer;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public String getIncomeExpense() {
        return incomeExpense;
    }

    public void setIncomeExpense(String incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public String getPayer() {
        return payer;
    }
}