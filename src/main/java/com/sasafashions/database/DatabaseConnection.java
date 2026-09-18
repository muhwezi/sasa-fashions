package com.sasafashions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides database connections for the Sasa Fashions Management System.
 *
 * <p>This class stores the connection information required to communicate
 * with the {@code sasa_fashions} MySQL database. DAO classes call the
 * {@link #getConnection()} method whenever they need to execute an SQL
 * operation.</p>
 *
 * <p>The class also contains a main method that can be used to test whether
 * the application can successfully connect to MySQL.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class DatabaseConnection {

    /**
     * JDBC address of the Sasa Fashions MySQL database.
     */
    private static final String URL =
            "jdbc:mysql://localhost:3306/sasa_fashions";

    /**
     * MySQL username used by the application.
     */
    private static final String USERNAME = "root";

    /**
     * MySQL password used by the application.
     *
     * <p>The current local development installation uses a blank password.
     * A secure password and configuration file should be used when the
     * application is deployed.</p>
     */
    private static final String PASSWORD = "";

    /**
     * Prevents creation of unnecessary DatabaseConnection objects.
     *
     * <p>All functionality in this class is provided through static
     * methods.</p>
     */
    private DatabaseConnection() {
    }

    /**
     * Opens and returns a connection to the Sasa Fashions database.
     *
     * <p>The returned connection should be closed after use. DAO classes
     * normally close it automatically using a try-with-resources
     * statement.</p>
     *
     * @return an active connection to the MySQL database
     * @throws SQLException if MySQL is unavailable, the credentials are
     *         incorrect or the database cannot be accessed
     */
    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }

    /**
     * Tests the database connection independently from the graphical
     * application.
     *
     * <p>If the connection succeeds, a confirmation is printed in the
     * NetBeans Output window. Otherwise, the database error message is
     * displayed.</p>
     *
     * @param args command-line arguments; they are not used
     */
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