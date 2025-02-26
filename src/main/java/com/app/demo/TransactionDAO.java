// Java
package com.app.demo;

import java.sql.*;
import java.util.*;

public class TransactionDAO {
    public static void insertTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions(source, date, description, amount, category, incomeExpense, payer) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getSource());
            pstmt.setString(2, transaction.getDate());
            pstmt.setString(3, transaction.getDescription());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getCategory().getName());
            pstmt.setString(6, transaction.getIncomeExpense());
            pstmt.setString(7, transaction.getPayer());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category(rs.getString("category"));
                Transaction transaction = new Transaction(
                        rs.getString("source"),
                        rs.getString("date"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        category,
                        rs.getString("incomeExpense"),
                        rs.getString("payer")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transactions;
    }
    public static void deleteTransaction(Transaction transaction){
        String sql = "DELETE FROM transactions WHERE date = ? AND description = ? AND amount = ? AND category = ? AND incomeExpense = ? AND payer = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getDate());
            pstmt.setString(2, transaction.getDescription());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getCategory().getName());
            pstmt.setString(5, transaction.getIncomeExpense());
            pstmt.setString(6, transaction.getPayer());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void updateTransaction(Transaction transaction){
        String sql = "UPDATE transactions SET date = ?, description = ?, amount = ?, category = ?, incomeExpense = ?, payer = ? WHERE date = ? AND description = ? AND amount = ? AND category = ? AND incomeExpense = ? AND payer = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getDate());
            pstmt.setString(2, transaction.getDescription());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getCategory().getName());
            pstmt.setString(5, transaction.getIncomeExpense());
            pstmt.setString(6, transaction.getPayer());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void insertAssetLiabilityType(Account account){
        String sql = "INSERT INTO asset_liability_types(name, type, subType, accBalance) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getName());
            pstmt.setString(2, account.getType());
            pstmt.setString(3, account.getSubType());
            pstmt.setDouble(4, account.getAccBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static List<Account> getAssetLiabilityTypes(){
        List<Account> assetLiabilityTypes = new ArrayList<>();
        String sql = "SELECT * FROM asset_liability_types";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = new Account(rs.getString("name"), rs.getString("type"), rs.getString("subType"), rs.getDouble("accBalance"));
                assetLiabilityTypes.add(account);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return assetLiabilityTypes;
    }
    public static void deleteAssetLiabilityType(Account account){
        String sql = "DELETE FROM asset_liability_types WHERE name = ? AND type = ? AND subType = ? AND accBalance = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getName());
            pstmt.setString(2, account.getType());
            pstmt.setString(3, account.getSubType());
            pstmt.setDouble(4, account.getAccBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void insertCategory(Category category){
        String sql = "INSERT INTO categories(name) VALUES(?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static List<Category> getCategories(){
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Category category = new Category(rs.getString("name"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return categories;
    }
    public static void updateAccBalance(Account account) {
        String sql = "UPDATE asset_liability_types SET accBalance = ? WHERE name = ? AND type = ? AND subType = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getAccBalance());
            pstmt.setString(2, account.getName());
            pstmt.setString(3, account.getType());
            pstmt.setString(4, account.getSubType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static double getTotalBalance(){
        String sql = "SELECT SUM(amount) AS total_balance FROM transactions";
        double totalBalance = 0;
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) {
                totalBalance = rs.getDouble("total_balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return totalBalance;
    }
}