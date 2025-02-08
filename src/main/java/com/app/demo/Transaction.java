
package com.app.demo;

public class Transaction {
    private String source;
    private String date;
    private String description;
    private double amount;
    private Category category;
    private String incomeExpense;
    private String payer;

    public Transaction(String source, String date, String description, double amount, Category category, String incomeExpense, String payer)
    {
        this.source = source;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.incomeExpense = incomeExpense;
        this.payer = payer;
    }
    public String getSource() {
        return source;
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

    public String getPayer() {
        return payer;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setIncomeExpense(String incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String toString(){
        return "Account: " + source + ", Date: " + date + ", Description: " + description + ", Amount: " + amount + ", Category: " + category.getName() + ", Income/Expense: " + incomeExpense + ", Payer: " + payer;
    }
}