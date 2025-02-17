// Java
package com.app.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:app.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "source TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "description TEXT," +
                "amount REAL NOT NULL," +
                "category TEXT NOT NULL," + // This column will be used to store the category name
                "incomeExpense TEXT NOT NULL," + // Add incomeExpense column
                "payer TEXT" +
                ");";
        String createAssetLiabilityTypesTable = "CREATE TABLE IF NOT EXISTS asset_liability_types (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "subType TEXT NOT NULL," +
                "accBalance REAL NOT NULL" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTransactionsTable);
            stmt.execute(createAssetLiabilityTypesTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}