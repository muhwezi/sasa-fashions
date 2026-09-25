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
 * <p>This class separates database-related network operations from
 * {@link ClientHandler}. The client handler is therefore responsible
 * for communication while this service is responsible for retrieving
 * approved business information.</p>
 *
 * <p>The service currently supports:</p>
 *
 * <ul>
 *     <li>Customer lookup using {@link CustomerDAO}</li>
 *     <li>System summary information</li>
 *     <li>Order lookup</li>
 * </ul>
 *
 * <p>Remote clients do not execute arbitrary SQL and do not receive
 * database credentials. Only predefined server-side operations are
 * available.</p>
 *
 * @author SASA Group
 * @version 1.2
 */
public class NetworkDataService {

    /**
     * DAO used for retrieving customer records.
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
     * @param customerId customer business ID such as {@code CUST-0001}
     * @return formatted customer information or an error message
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
     * Produces a summary of major Sasa Fashions database records.
     *
     * <p>The summary currently counts customers, orders and
     * payments. Only fixed table names are used; the client cannot
     * supply a table name or SQL statement.</p>
     *
     * @return formatted system summary or an error message
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
     * Retrieves an order using its business order ID.
     *
     * <p>The method returns the principal order information from
     * the {@code orders} table. A prepared statement is used so
     * that the supplied value remains separate from the SQL
     * instruction.</p>
     *
     * @param orderId order business ID such as {@code ORD-0001}
     * @return formatted order information or an error message
     */
    public String findOrder(String orderId) {

        if (orderId == null
                || orderId.isBlank()) {

            return "ERROR: Order ID is required.";
        }

        /*
         * Allow letters, numbers and hyphens only.
         * This accommodates business IDs while rejecting
         * unexpected command characters.
         */
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
                    status,
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
                                        "status"
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
     * Executes one fixed count query.
     *
     * <p>This method is private because SQL statements must not
     * originate from network clients. Only this class supplies
     * the predefined count statements.</p>
     *
     * @param sql fixed SQL count query
     * @return number of rows counted
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
     * Formats one Customer object for transmission to a client.
     *
     * @param customer customer retrieved from the database
     * @return single-line customer description
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
     * Converts a value into safe single-line text.
     *
     * @param value value to format
     * @return safe textual value
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