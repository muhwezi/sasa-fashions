package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Payment;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PaymentDAO {

    /**
     * Generates payment IDs such as:
     * PAY-0001
     * PAY-0002
     */
    public String generateNextPaymentId() throws SQLException {
        return IdGenerator.generateNextPaymentId();
    }

    /*
     * Saves a new payment.
     *
     * Before saving, it checks:
     * 1. The order exists.
     * 2. The payment amount is greater than zero.
     * 3. The payment does not exceed the outstanding balance.
     */
    public void savePayment(Payment payment) throws SQLException {

        validatePayment(payment);

        if (isBlank(payment.getPaymentId())) {
            payment.setPaymentId(generateNextPaymentId());
        }

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        String sql = """
                INSERT INTO payments (
                    payment_id,
                    order_id,
                    received_by,
                    payment_date,
                    amount,
                    payment_method,
                    reference_number,
                    notes
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                BigDecimal orderTotal =
                        getOrderTotalForUpdate(
                                connection,
                                payment.getOrderId()
                        );

                BigDecimal alreadyPaid =
                        getTotalPaid(
                                connection,
                                payment.getOrderId()
                        );

                BigDecimal outstandingBalance =
                        orderTotal.subtract(alreadyPaid);

                if (payment.getAmount()
                        .compareTo(outstandingBalance) > 0) {

                    throw new IllegalArgumentException(
                            "Payment cannot exceed the outstanding balance.\n"
                            + "Order total: " + orderTotal + "\n"
                            + "Already paid: " + alreadyPaid + "\n"
                            + "Outstanding balance: "
                            + outstandingBalance
                    );
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(
                            1,
                            payment.getPaymentId()
                    );

                    statement.setString(
                            2,
                            payment.getOrderId()
                    );

                    setNullableString(
                            statement,
                            3,
                            payment.getReceivedBy()
                    );

                    statement.setTimestamp(
                            4,
                            Timestamp.valueOf(
                                    payment.getPaymentDate()
                            )
                    );

                    statement.setBigDecimal(
                            5,
                            payment.getAmount()
                    );

                    statement.setString(
                            6,
                            payment.getPaymentMethod()
                    );

                    setNullableString(
                            statement,
                            7,
                            payment.getReferenceNumber()
                    );

                    setNullableString(
                            statement,
                            8,
                            payment.getNotes()
                    );

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException | RuntimeException exception) {
                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /*
     * Finds one payment using its payment ID.
     */
    public Payment findPayment(
            String paymentId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM payments
                WHERE payment_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, paymentId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return createPaymentFromResultSet(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /*
     * Returns every payment in the database.
     */
    public ArrayList<Payment> findAllPayments()
            throws SQLException {

        ArrayList<Payment> payments =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM payments
                ORDER BY payment_date DESC,
                         payment_id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                payments.add(
                        createPaymentFromResultSet(
                                resultSet
                        )
                );
            }
        }

        return payments;
    }

    /*
     * Returns all payments belonging to one order.
     */
    public ArrayList<Payment> findPaymentsByOrder(
            String orderId
    ) throws SQLException {

        ArrayList<Payment> payments =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM payments
                WHERE order_id = ?
                ORDER BY payment_date,
                         payment_id
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    payments.add(
                            createPaymentFromResultSet(
                                    resultSet
                            )
                    );
                }
            }
        }

        return payments;
    }

    /*
     * Updates an existing payment.
     *
     * The selected payment is excluded when calculating
     * how much was previously paid.
     */
    public void updatePayment(
            Payment payment
    ) throws SQLException {

        validatePayment(payment);

        if (isBlank(payment.getPaymentId())) {
            throw new IllegalArgumentException(
                    "Payment ID is required when editing."
            );
        }

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(
                    LocalDateTime.now()
            );
        }

        String sql = """
                UPDATE payments
                SET order_id = ?,
                    received_by = ?,
                    payment_date = ?,
                    amount = ?,
                    payment_method = ?,
                    reference_number = ?,
                    notes = ?
                WHERE payment_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                BigDecimal orderTotal =
                        getOrderTotalForUpdate(
                                connection,
                                payment.getOrderId()
                        );

                BigDecimal otherPaymentsTotal =
                        getOtherPaymentsTotal(
                                connection,
                                payment.getOrderId(),
                                payment.getPaymentId()
                        );

                BigDecimal maximumAllowed =
                        orderTotal.subtract(
                                otherPaymentsTotal
                        );

                if (payment.getAmount()
                        .compareTo(maximumAllowed) > 0) {

                    throw new IllegalArgumentException(
                            "Payment cannot exceed the outstanding balance.\n"
                            + "Order total: " + orderTotal + "\n"
                            + "Other payments: "
                            + otherPaymentsTotal + "\n"
                            + "Maximum allowed: "
                            + maximumAllowed
                    );
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(
                            1,
                            payment.getOrderId()
                    );

                    setNullableString(
                            statement,
                            2,
                            payment.getReceivedBy()
                    );

                    statement.setTimestamp(
                            3,
                            Timestamp.valueOf(
                                    payment.getPaymentDate()
                            )
                    );

                    statement.setBigDecimal(
                            4,
                            payment.getAmount()
                    );

                    statement.setString(
                            5,
                            payment.getPaymentMethod()
                    );

                    setNullableString(
                            statement,
                            6,
                            payment.getReferenceNumber()
                    );

                    setNullableString(
                            statement,
                            7,
                            payment.getNotes()
                    );

                    statement.setString(
                            8,
                            payment.getPaymentId()
                    );

                    int affectedRows =
                            statement.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException(
                                "Payment was not found: "
                                + payment.getPaymentId()
                        );
                    }
                }

                connection.commit();

            } catch (SQLException | RuntimeException exception) {
                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /*
     * Deletes a payment.
     */
    public boolean deletePayment(
            String paymentId
    ) throws SQLException {

        String sql = """
                DELETE FROM payments
                WHERE payment_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, paymentId);

            return statement.executeUpdate() > 0;
        }
    }

    /*
     * Gets the total value of an order.
     */
    public BigDecimal getOrderTotal(
            String orderId
    ) throws SQLException {

        String sql = """
                SELECT total_amount
                FROM orders
                WHERE order_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBigDecimal(
                            "total_amount"
                    );
                }
            }
        }

        throw new SQLException(
                "Order was not found: " + orderId
        );
    }

    /*
     * Gets the total amount already paid for an order.
     */
    public BigDecimal getTotalPaid(
            String orderId
    ) throws SQLException {

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            return getTotalPaid(
                    connection,
                    orderId
            );
        }
    }

    /*
     * Calculates:
     *
     * Outstanding balance =
     * Order total - Total payments
     */
    public BigDecimal getOutstandingBalance(
            String orderId
    ) throws SQLException {

        String sql = """
                SELECT
                    o.total_amount,
                    COALESCE(SUM(p.amount), 0.00)
                        AS total_paid
                FROM orders o
                LEFT JOIN payments p
                    ON o.order_id = p.order_id
                WHERE o.order_id = ?
                GROUP BY o.order_id,
                         o.total_amount
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    BigDecimal orderTotal =
                            resultSet.getBigDecimal(
                                    "total_amount"
                            );

                    BigDecimal totalPaid =
                            resultSet.getBigDecimal(
                                    "total_paid"
                            );

                    return orderTotal.subtract(
                            totalPaid
                    );
                }
            }
        }

        throw new SQLException(
                "Order was not found: " + orderId
        );
    }

    /*
     * Navigation methods
     */

    public Payment findFirstPayment()
            throws SQLException {

        String sql = """
                SELECT *
                FROM payments
                ORDER BY payment_id ASC
                LIMIT 1
                """;

        return findSinglePayment(sql, null);
    }

    public Payment findLastPayment()
            throws SQLException {

        String sql = """
                SELECT *
                FROM payments
                ORDER BY payment_id DESC
                LIMIT 1
                """;

        return findSinglePayment(sql, null);
    }

    public Payment findNextPayment(
            String currentPaymentId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM payments
                WHERE payment_id > ?
                ORDER BY payment_id ASC
                LIMIT 1
                """;

        return findSinglePayment(
                sql,
                currentPaymentId
        );
    }

    public Payment findPreviousPayment(
            String currentPaymentId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM payments
                WHERE payment_id < ?
                ORDER BY payment_id DESC
                LIMIT 1
                """;

        return findSinglePayment(
                sql,
                currentPaymentId
        );
    }

    /*
     * Private helper methods
     */

    private Payment findSinglePayment(
            String sql,
            String paymentId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            if (paymentId != null) {
                statement.setString(1, paymentId);
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return createPaymentFromResultSet(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    private Payment createPaymentFromResultSet(
            ResultSet resultSet
    ) throws SQLException {

        Timestamp paymentTimestamp =
                resultSet.getTimestamp(
                        "payment_date"
                );

        LocalDateTime paymentDate = null;

        if (paymentTimestamp != null) {
            paymentDate =
                    paymentTimestamp.toLocalDateTime();
        }

        return new Payment(
                resultSet.getString("payment_id"),
                resultSet.getString("order_id"),
                resultSet.getString("received_by"),
                paymentDate,
                resultSet.getBigDecimal("amount"),
                resultSet.getString("payment_method"),
                resultSet.getString("reference_number"),
                resultSet.getString("notes")
        );
    }

    /*
     * Locks the order row during payment validation.
     *
     * This prevents two payments from being processed
     * against the same balance at exactly the same time.
     */
    private BigDecimal getOrderTotalForUpdate(
            Connection connection,
            String orderId
    ) throws SQLException {

        String sql = """
                SELECT total_amount
                FROM orders
                WHERE order_id = ?
                FOR UPDATE
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBigDecimal(
                            "total_amount"
                    );
                }
            }
        }

        throw new SQLException(
                "Order was not found: " + orderId
        );
    }

    private BigDecimal getTotalPaid(
            Connection connection,
            String orderId
    ) throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(amount), 0.00)
                    AS total_paid
                FROM payments
                WHERE order_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBigDecimal(
                            "total_paid"
                    );
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getOtherPaymentsTotal(
            Connection connection,
            String orderId,
            String excludedPaymentId
    ) throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(amount), 0.00)
                    AS total_paid
                FROM payments
                WHERE order_id = ?
                  AND payment_id <> ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, orderId);
            statement.setString(
                    2,
                    excludedPaymentId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBigDecimal(
                            "total_paid"
                    );
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private void validatePayment(
            Payment payment
    ) {
        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment information is required."
            );
        }

        if (isBlank(payment.getOrderId())) {
            throw new IllegalArgumentException(
                    "Please select an order."
            );
        }

        if (payment.getAmount() == null
                || payment.getAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        if (isBlank(payment.getPaymentMethod())) {
            throw new IllegalArgumentException(
                    "Please select a payment method."
            );
        }
    }

    private void setNullableString(
            PreparedStatement statement,
            int parameterNumber,
            String value
    ) throws SQLException {

        if (isBlank(value)) {
            statement.setNull(
                    parameterNumber,
                    Types.VARCHAR
            );
        } else {
            statement.setString(
                    parameterNumber,
                    value.trim()
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null
                || value.trim().isEmpty();
    }

    private void rollbackQuietly(
            Connection connection
    ) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Preserve the original exception.
        }
    }
}