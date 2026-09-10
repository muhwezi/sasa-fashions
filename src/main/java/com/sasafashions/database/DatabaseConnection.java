package com.sasafashions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sasa_fashions";

    private static final String USERNAME = "root";

    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }

    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            System.out.println(
                    "Successfully connected to Sasa Fashions database."
            );

        } catch (SQLException error) {

            System.out.println(
                    "Connection failed: " + error.getMessage()
            );
        }
    }
}