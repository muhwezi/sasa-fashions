package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Customer;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Performs all database operations involving customers.
 *
 * This DAO provides methods for saving, finding, updating,
 * deleting and navigating customer records.
 *
 * @author SASA Group
 * @version 1.0
 */
public class CustomerDAO {

    /**
     * Requests the next customer ID from IdGenerator.
     *
     * @return next customer ID, for example CUST-0001
     * @throws SQLException when the database operation fails
     */
    public String generateNextCustomerId() throws SQLException {

        return IdGenerator.generateNextCustomerId();
    }

    /**
     * Saves a new customer in the customers table.
     *
     * @param customer customer to be saved
     * @return true when the customer is saved successfully
     * @throws SQLException when the database operation fails
     */
    public boolean saveCustomer(Customer customer)
            throws SQLException {

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

            statement.setString(
                    1,
                    customer.getCustomerId()
            );

            statement.setString(
                    2,
                    customer.getCustomerName()
            );

            statement.setString(
                    3,
                    customer.getTelephone()
            );

            statement.setString(
                    4,
                    customer.getGender()
            );

            statement.setString(
                    5,
                    customer.getAddress()
            );

            statement.setDate(
                    6,
                    Date.valueOf(
                            customer.getRegistrationDate()
                    )
            );

            statement.setString(
                    7,
                    customer.getCustomerStatus()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Finds a customer using their customer ID.
     *
     * @param customerId customer ID to search for
     * @return matching Customer, or null when not found
     * @throws SQLException when the database operation fails
     */
    public Customer findCustomer(String customerId)
            throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "WHERE customer_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, customerId);

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {
                    return createCustomerFromResult(result);
                }
            }
        }

        return null;
    }

    /**
     * Updates an existing customer.
     *
     * The customer ID is not changed because it is the
     * primary key used to identify the customer.
     *
     * @param customer customer containing updated information
     * @return true when the customer is updated successfully
     * @throws SQLException when the database operation fails
     */
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

            statement.setString(
                    1,
                    customer.getCustomerName()
            );

            statement.setString(
                    2,
                    customer.getTelephone()
            );

            statement.setString(
                    3,
                    customer.getGender()
            );

            statement.setString(
                    4,
                    customer.getAddress()
            );

            statement.setDate(
                    5,
                    Date.valueOf(
                            customer.getRegistrationDate()
                    )
            );

            statement.setString(
                    6,
                    customer.getCustomerStatus()
            );

            statement.setString(
                    7,
                    customer.getCustomerId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a customer using their customer ID.
     *
     * MySQL will prevent deletion when the customer has
     * related records protected by foreign keys.
     *
     * @param customerId customer ID to delete
     * @return true when the customer is deleted
     * @throws SQLException when the database operation fails
     */
    public boolean deleteCustomer(String customerId)
            throws SQLException {

        String sql =
                "DELETE FROM customers " +
                "WHERE customer_id = ?";

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

    /**
     * Retrieves the first customer according to customer ID.
     *
     * @return first Customer, or null when the table is empty
     * @throws SQLException when the database operation fails
     */
    
    
    /**
 * Retrieves all registered customers.
 *
 * @return ArrayList containing all customers
 * @throws SQLException when database access fails
 */
public ArrayList<Customer> findAllCustomers()
        throws SQLException {

    ArrayList<Customer> customers =
            new ArrayList<>();

    String sql =
            "SELECT * FROM customers " +
            "ORDER BY customer_name ASC";

    try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery()
    ) {

        while (result.next()) {

            Customer customer =
                    createCustomerFromResult(result);

            customers.add(customer);
        }
    }

    return customers;
}
    
    
    public Customer getFirstCustomer()
            throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "ORDER BY customer_id ASC " +
                "LIMIT 1";

        return getSingleCustomer(sql, null);
    }

    /**
     * Retrieves the last customer according to customer ID.
     *
     * @return last Customer, or null when the table is empty
     * @throws SQLException when the database operation fails
     */
    public Customer getLastCustomer()
            throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "ORDER BY customer_id DESC " +
                "LIMIT 1";

        return getSingleCustomer(sql, null);
    }

    /**
     * Retrieves the customer immediately after the
     * currently displayed customer.
     *
     * @param currentCustomerId currently displayed customer ID
     * @return next Customer, or null when already at the end
     * @throws SQLException when the database operation fails
     */
    public Customer getNextCustomer(
            String currentCustomerId
    ) throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "WHERE customer_id > ? " +
                "ORDER BY customer_id ASC " +
                "LIMIT 1";

        return getSingleCustomer(
                sql,
                currentCustomerId
        );
    }

    /**
     * Retrieves the customer immediately before the
     * currently displayed customer.
     *
     * @param currentCustomerId currently displayed customer ID
     * @return previous Customer, or null when at the beginning
     * @throws SQLException when the database operation fails
     */
    public Customer getPreviousCustomer(
            String currentCustomerId
    ) throws SQLException {

        String sql =
                "SELECT * FROM customers " +
                "WHERE customer_id < ? " +
                "ORDER BY customer_id DESC " +
                "LIMIT 1";

        return getSingleCustomer(
                sql,
                currentCustomerId
        );
    }

    /**
     * Executes a query expected to return one customer.
     *
     * This shared private method reduces repeated code in
     * the First, Previous, Next and Last operations.
     *
     * @param sql SQL query to execute
     * @param customerId optional customer ID parameter
     * @return matching Customer, or null when none is found
     * @throws SQLException when the database operation fails
     */
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
                statement.setString(
                        1,
                        customerId
                );
            }

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {
                    return createCustomerFromResult(result);
                }
            }
        }

        return null;
    }

    /**
     * Converts the current ResultSet row into a Customer object.
     *
     * @param result database result positioned at a customer row
     * @return constructed Customer object
     * @throws SQLException when a column cannot be read
     */
    private Customer createCustomerFromResult(
            ResultSet result
    ) throws SQLException {

        return new Customer(
                result.getString("customer_id"),
                result.getString("customer_name"),
                result.getString("telephone"),
                result.getString("gender"),
                result.getString("address"),
                result.getDate(
                        "registration_date"
                ).toLocalDate(),
                result.getString("customer_status")
        );
    }
}