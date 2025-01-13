// Java
package com.app.demo;

import java.sql.*;
import java.util.*;

public class TransactionDAO {
    public static void insertTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions(date, description, amount, category, incomeExpense, payer) VALUES(?,?,?,?,?,?)";

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

    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category(rs.getString("category"));
                Transaction transaction = new Transaction(
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
}