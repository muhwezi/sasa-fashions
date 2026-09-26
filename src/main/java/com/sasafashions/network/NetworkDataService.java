package com.sasafashions.network;

import com.sasafashions.dao.CustomerDAO;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides controlled access to Sasa Fashions business information
 * for the networking layer.
 *
 * <p>This service separates business-data retrieval from socket
 * communication. ClientHandler manages network communication while
 * NetworkDataService performs approved DAO and JDBC operations.</p>
 *
 * <p>Remote clients cannot submit arbitrary SQL statements and do not
 * receive database credentials. Only predefined operations are exposed.</p>
 *
 * @author SASA Group
 * @version 1.3
 */
public class NetworkDataService {

    /**
     * DAO used for customer-related database operations.
     */
    private final CustomerDAO customerDAO;

    /**
     * Creates a new network data service.
     */
    public NetworkDataService() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Finds a customer using the existing CustomerDAO.
     *
     * @param customerId customer business ID such as {@code CUST-0005}
     * @return formatted customer details or an error message
     */
    public String findCustomer(String customerId) {

        if (customerId == null
                || !customerId.matches("CUST-\\d+")) {

            return "ERROR: Invalid customer ID format.";
        }

        try {

            Customer customer =
                    customerDAO.findCustomer(customerId);

            if (customer == null) {

                return "ERROR: Customer "
                        + customerId
                        + " was not found.";
            }

            return formatCustomer(customer);

        } catch (SQLException e) {

            System.err.println(
                    "Customer lookup database error: "
                    + e.getMessage()
            );

            return "ERROR: Unable to retrieve customer.";
        }
    }

    /**
     * Generates a summary of major records in the
     * Sasa Fashions database.
     *
     * @return formatted system summary
     */
    public String getSummary() {

        try {

            int customers =
                    countRecords(
                            "SELECT COUNT(*) FROM customers"
                    );

            int orders =
                    countRecords(
                            "SELECT COUNT(*) FROM orders"
                    );

            int payments =
                    countRecords(
                            "SELECT COUNT(*) FROM payments"
                    );

            return "SYSTEM SUMMARY"
                    + " | CUSTOMERS=" + customers
                    + " | ORDERS=" + orders
                    + " | PAYMENTS=" + payments;

        } catch (SQLException e) {

            System.err.println(
                    "Summary database error: "
                    + e.getMessage()
            );

            return "ERROR: Unable to generate system summary.";
        }
    }

    /**
     * Finds an order using its business order ID.
     *
     * <p>The method retrieves the principal order information
     * including customer, employee, order date, due date,
     * order status and total amount.</p>
     *
     * @param orderId order business ID such as {@code ORD-0001}
     * @return formatted order details or an error message
     */
    public String findOrder(String orderId) {

        if (orderId == null
                || orderId.isBlank()) {

            return "ERROR: Order ID is required.";
        }

        if (!orderId.matches("[A-Za-z0-9\\-]{1,15}")) {

            return "ERROR: Invalid order ID format.";
        }

        String sql =
                """
                SELECT
                    order_id,
                    customer_id,
                    employee_id,
                    order_date,
                    due_date,
                    order_status,
                    total_amount
                FROM orders
                WHERE order_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    orderId.toUpperCase()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {

                    return "ERROR: Order "
                            + orderId
                            + " was not found.";
                }

                return "ORDER FOUND"
                        + " | ID="
                        + safe(
                                resultSet.getString(
                                        "order_id"
                                )
                        )

                        + " | CUSTOMER="
                        + safe(
                                resultSet.getString(
                                        "customer_id"
                                )
                        )

                        + " | EMPLOYEE="
                        + safe(
                                resultSet.getString(
                                        "employee_id"
                                )
                        )

                        + " | ORDER DATE="
                        + safe(
                                resultSet.getDate(
                                        "order_date"
                                )
                        )

                        + " | DUE DATE="
                        + safe(
                                resultSet.getDate(
                                        "due_date"
                                )
                        )

                        + " | STATUS="
                        + safe(
                                resultSet.getString(
                                        "order_status"
                                )
                        )

                        + " | TOTAL="
                        + safe(
                                resultSet.getBigDecimal(
                                        "total_amount"
                                )
                        );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Order lookup database error: "
                    + e.getMessage()
            );

            return "ERROR: Unable to retrieve order.";
        }
    }

    /**
     * Executes a predefined SQL count query.
     *
     * @param sql fixed SQL count statement
     * @return number of records counted
     * @throws SQLException when the database operation fails
     */
    private int countRecords(String sql)
            throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Formats a Customer object for transmission through the
     * TCP connection.
     *
     * @param customer customer object retrieved from the database
     * @return formatted one-line customer information
     */
    private String formatCustomer(Customer customer) {

        return "CUSTOMER FOUND"
                + " | ID="
                + safe(customer.getCustomerId())

                + " | NAME="
                + safe(customer.getCustomerName())

                + " | TELEPHONE="
                + safe(customer.getTelephone())

                + " | GENDER="
                + safe(customer.getGender())

                + " | ADDRESS="
                + safe(customer.getAddress())

                + " | REGISTERED="
                + safe(customer.getRegistrationDate())

                + " | STATUS="
                + safe(customer.getCustomerStatus());
    }

    /**
     * Converts a value into safe single-line network text.
     *
     * @param value value to convert
     * @return cleaned string value
     */
    private String safe(Object value) {

        if (value == null) {
            return "";
        }

        return value.toString()
                .replace("\r", " ")
                .replace("\n", " ");
    }
}