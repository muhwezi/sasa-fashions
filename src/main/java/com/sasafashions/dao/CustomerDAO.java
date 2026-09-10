package com.sasafashions.dao;

import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    // Generate IDs such as CUST-0001 and CUST-0002
    public String generateNextCustomerId() throws SQLException {

        String sql =
                "SELECT COALESCE(" +
                "MAX(CAST(SUBSTRING(customer_id, 6) AS UNSIGNED)), 0) + 1 " +
                "AS next_number " +
                "FROM customers " +
                "WHERE customer_id LIKE 'CUST-%'";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result = statement.executeQuery()
        ) {

            if (result.next()) {
                int nextNumber = result.getInt("next_number");

                return String.format(
                        "CUST-%04d",
                        nextNumber
                );
            }
        }

        return "CUST-0001";
    }

    // Save a new customer
    public boolean saveCustomer(Customer customer) throws SQLException {

        String sql =
                "INSERT INTO customers (" +
                "customer_id, customer_name, telephone, gender, " +
                "address, registration_date, customer_status" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, customer.getCustomerId());
            statement.setString(2, customer.getCustomerName());
            statement.setString(3, customer.getTelephone());
            statement.setString(4, customer.getGender());
            statement.setString(5, customer.getAddress());

            statement.setDate(
                    6,
                    Date.valueOf(customer.getRegistrationDate())
            );

            statement.setString(
                    7,
                    customer.getCustomerStatus()
            );

            return statement.executeUpdate() > 0;
        }
    }

    // Find a customer using the customer ID
    public Customer findCustomer(String customerId)
            throws SQLException {

        String sql =
                "SELECT * FROM customers WHERE customer_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, customerId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return createCustomerFromResult(result);
                }
            }
        }

        return null;
    }

    // Edit an existing customer
    public boolean updateCustomer(Customer customer)
            throws SQLException {

        String sql =
                "UPDATE customers SET " +
                "customer_name = ?, " +
                "telephone = ?, " +
                "gender = ?, " +
                "address = ?, " +
                "registration_date = ?, " +
                "customer_status = ? " +
                "WHERE customer_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, customer.getCustomerName());
            statement.setString(2, customer.getTelephone());
            statement.setString(3, customer.getGender());
            statement.setString(4, customer.getAddress());

            statement.setDate(
                    5,
                    Date.valueOf(customer.getRegistrationDate())
            );

            statement.setString(
                    6,
                    customer.getCustomerStatus()
            );

            statement.setString(7, customer.getCustomerId());

            return statement.executeUpdate() > 0;
        }
    }

    // Delete a customer
    public boolean deleteCustomer(String customerId)
            throws SQLException {

        String sql =
                "DELETE FROM customers WHERE customer_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, customerId);

            return statement.executeUpdate() > 0;
        }
    }

    // Get the first customer
    public Customer getFirstCustomer() throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "ORDER BY customer_id ASC LIMIT 1";

        return getSingleCustomer(sql, null);
    }

    // Get the last customer
    public Customer getLastCustomer() throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "ORDER BY customer_id DESC LIMIT 1";

        return getSingleCustomer(sql, null);
    }

    // Get the next customer
    public Customer getNextCustomer(String currentCustomerId)
            throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "WHERE customer_id > ? " +
                "ORDER BY customer_id ASC LIMIT 1";

        return getSingleCustomer(sql, currentCustomerId);
    }

    // Get the previous customer
    public Customer getPreviousCustomer(String currentCustomerId)
            throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "WHERE customer_id < ? " +
                "ORDER BY customer_id DESC LIMIT 1";

        return getSingleCustomer(sql, currentCustomerId);
    }

    // Shared method used by the navigation operations
    private Customer getSingleCustomer(
            String sql,
            String customerId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (customerId != null) {
                statement.setString(1, customerId);
            }

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return createCustomerFromResult(result);
                }
            }
        }

        return null;
    }

    // Convert one database record into a Customer object
    private Customer createCustomerFromResult(ResultSet result)
            throws SQLException {

        return new Customer(
                result.getString("customer_id"),
                result.getString("customer_name"),
                result.getString("telephone"),
                result.getString("gender"),
                result.getString("address"),
                result.getDate("registration_date").toLocalDate(),
                result.getString("customer_status")
        );
    }
}