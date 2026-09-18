package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Order;
import com.sasafashions.model.OrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Performs database operations involving orders and
 * their order-detail records.
 *
 * @author SASA Group
 * @version 1.0
 */
public class OrderDAO {

    /**
     * Generates the next order ID.
     */
    public String generateNextOrderId()
            throws SQLException {

        return IdGenerator.generateNextOrderId();
    }

    /**
     * Saves an order and all its details as one transaction.
     *
     * If any statement fails, the whole transaction is
     * rolled back so that an incomplete order is not saved.
     */
    public boolean saveOrderWithDetails(
            Order order,
            ArrayList<OrderDetail> details
    ) throws SQLException {

        validateDetails(details);

        BigDecimal totalAmount =
                calculateTotal(details);

        order.setTotalAmount(totalAmount);

        Connection connection =
                DatabaseConnection.getConnection();

        try {

            connection.setAutoCommit(false);

            insertOrder(connection, order);

            int nextDetailNumber =
                    getNextOrderDetailNumber(
                            connection
                    );

            for (OrderDetail detail : details) {

                detail.setOrderId(
                        order.getOrderId()
                );

                detail.setOrderDetailId(
                        String.format(
                                "ODET-%04d",
                                nextDetailNumber
                        )
                );

                insertOrderDetail(
                        connection,
                        detail
                );

                nextDetailNumber++;
            }

            connection.commit();
            return true;

        } catch (SQLException error) {

            connection.rollback();
            throw error;

        } finally {

            try {
                connection.setAutoCommit(true);
            } finally {
                connection.close();
            }
        }
    }

    /**
     * Updates an order and replaces its details as one
     * database transaction.
     */
    public boolean updateOrderWithDetails(
            Order order,
            ArrayList<OrderDetail> details
    ) throws SQLException {

        validateDetails(details);

        BigDecimal totalAmount =
                calculateTotal(details);

        order.setTotalAmount(totalAmount);

        Connection connection =
                DatabaseConnection.getConnection();

        try {

            connection.setAutoCommit(false);

            boolean updated =
                    updateOrderHeader(
                            connection,
                            order
                    );

            if (!updated) {
                connection.rollback();
                return false;
            }

            deleteOrderDetails(
                    connection,
                    order.getOrderId()
            );

            int nextDetailNumber =
                    getNextOrderDetailNumber(
                            connection
                    );

            for (OrderDetail detail : details) {

                detail.setOrderId(
                        order.getOrderId()
                );

                detail.setOrderDetailId(
                        String.format(
                                "ODET-%04d",
                                nextDetailNumber
                        )
                );

                insertOrderDetail(
                        connection,
                        detail
                );

                nextDetailNumber++;
            }

            connection.commit();
            return true;

        } catch (SQLException error) {

            connection.rollback();
            throw error;

        } finally {

            try {
                connection.setAutoCommit(true);
            } finally {
                connection.close();
            }
        }
    }

    /**
     * Inserts the main order record.
     */
    private void insertOrder(
            Connection connection,
            Order order
    ) throws SQLException {

        String sql =
                "INSERT INTO orders (" +
                "order_id, customer_id, employee_id, " +
                "order_date, due_date, order_status, " +
                "total_amount" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            setOrderStatementValues(
                    statement,
                    order,
                    false
            );

            statement.executeUpdate();
        }
    }

    /**
     * Inserts one order-detail record.
     *
     * Subtotal is excluded because MySQL generates it.
     */
    private void insertOrderDetail(
            Connection connection,
            OrderDetail detail
    ) throws SQLException {

        String sql =
                "INSERT INTO order_details (" +
                "order_detail_id, order_id, product_id, " +
                "quantity, unit_price" +
                ") VALUES (?, ?, ?, ?, ?)";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    detail.getOrderDetailId()
            );

            statement.setString(
                    2,
                    detail.getOrderId()
            );

            statement.setString(
                    3,
                    detail.getProductId()
            );

            statement.setInt(
                    4,
                    detail.getQuantity()
            );

            statement.setBigDecimal(
                    5,
                    detail.getUnitPrice()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Finds an order using its ID.
     */
    public Order findOrder(String orderId)
            throws SQLException {

        String sql =
                "SELECT * FROM orders " +
                "WHERE order_id = ?";

        return findUsingSingleParameter(
                sql,
                orderId
        );
    }

    /**
     * Retrieves all details belonging to an order.
     */
    public ArrayList<OrderDetail> findOrderDetails(
            String orderId
    ) throws SQLException {

        ArrayList<OrderDetail> details =
                new ArrayList<>();

        String sql =
                "SELECT * FROM order_details " +
                "WHERE order_id = ? " +
                "ORDER BY order_detail_id ASC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, orderId);

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                while (result.next()) {

                    details.add(
                            createOrderDetailFromResult(
                                    result
                            )
                    );
                }
            }
        }

        return details;
    }

    /**
     * Retrieves all orders.
     */
    public ArrayList<Order> findAllOrders()
            throws SQLException {

        ArrayList<Order> orders =
                new ArrayList<>();

        String sql =
                "SELECT * FROM orders " +
                "ORDER BY order_date ASC, order_id ASC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                orders.add(
                        createOrderFromResult(result)
                );
            }
        }

        return orders;
    }

    /**
     * Retrieves active orders for the tailoring queue.
     */
    public ArrayList<Order> findActiveOrders()
            throws SQLException {

        ArrayList<Order> orders =
                new ArrayList<>();

        String sql =
                "SELECT * FROM orders " +
                "WHERE order_status IN (" +
                "'Pending', 'In Progress', 'Ready'" +
                ") " +
                "ORDER BY order_date ASC, order_id ASC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                orders.add(
                        createOrderFromResult(result)
                );
            }
        }

        return orders;
    }

    /**
     * Updates only an order's status.
     */
    public boolean updateOrderStatus(
            String orderId,
            String orderStatus
    ) throws SQLException {

        String sql =
                "UPDATE orders SET order_status = ? " +
                "WHERE order_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, orderStatus);
            statement.setString(2, orderId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an order.
     *
     * MySQL automatically deletes its order details
     * because the foreign key uses ON DELETE CASCADE.
     */
    public boolean deleteOrder(String orderId)
            throws SQLException {

        String sql =
                "DELETE FROM orders " +
                "WHERE order_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, orderId);

            return statement.executeUpdate() > 0;
        }
    }

    public Order getFirstOrder()
            throws SQLException {

        String sql =
                "SELECT * FROM orders " +
                "ORDER BY order_id ASC LIMIT 1";

        return getSingleOrder(sql, null);
    }

    public Order getLastOrder()
            throws SQLException {

        String sql =
                "SELECT * FROM orders " +
                "ORDER BY order_id DESC LIMIT 1";

        return getSingleOrder(sql, null);
    }

    public Order getNextOrder(
            String currentOrderId
    ) throws SQLException {

        String sql =
                "SELECT * FROM orders " +
                "WHERE order_id > ? " +
                "ORDER BY order_id ASC LIMIT 1";

        return getSingleOrder(
                sql,
                currentOrderId
        );
    }

    public Order getPreviousOrder(
            String currentOrderId
    ) throws SQLException {

        String sql =
                "SELECT * FROM orders " +
                "WHERE order_id < ? " +
                "ORDER BY order_id DESC LIMIT 1";

        return getSingleOrder(
                sql,
                currentOrderId
        );
    }

    /**
     * Updates the main order record using an existing
     * transaction connection.
     */
    private boolean updateOrderHeader(
            Connection connection,
            Order order
    ) throws SQLException {

        String sql =
                "UPDATE orders SET " +
                "customer_id = ?, " +
                "employee_id = ?, " +
                "order_date = ?, " +
                "due_date = ?, " +
                "order_status = ?, " +
                "total_amount = ? " +
                "WHERE order_id = ?";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            setOrderStatementValues(
                    statement,
                    order,
                    true
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Sets INSERT or UPDATE order parameters.
     */
    private void setOrderStatementValues(
            PreparedStatement statement,
            Order order,
            boolean update
    ) throws SQLException {

        if (update) {

            statement.setString(
                    1,
                    order.getCustomerId()
            );

            statement.setString(
                    2,
                    order.getEmployeeId()
            );

            statement.setDate(
                    3,
                    Date.valueOf(order.getOrderDate())
            );

            statement.setDate(
                    4,
                    Date.valueOf(order.getDueDate())
            );

            statement.setString(
                    5,
                    order.getOrderStatus()
            );

            statement.setBigDecimal(
                    6,
                    order.getTotalAmount()
            );

            statement.setString(
                    7,
                    order.getOrderId()
            );

        } else {

            statement.setString(
                    1,
                    order.getOrderId()
            );

            statement.setString(
                    2,
                    order.getCustomerId()
            );

            statement.setString(
                    3,
                    order.getEmployeeId()
            );

            statement.setDate(
                    4,
                    Date.valueOf(order.getOrderDate())
            );

            statement.setDate(
                    5,
                    Date.valueOf(order.getDueDate())
            );

            statement.setString(
                    6,
                    order.getOrderStatus()
            );

            statement.setBigDecimal(
                    7,
                    order.getTotalAmount()
            );
        }
    }

    /**
     * Deletes existing details before an order is rebuilt.
     */
    private void deleteOrderDetails(
            Connection connection,
            String orderId
    ) throws SQLException {

        String sql =
                "DELETE FROM order_details " +
                "WHERE order_id = ?";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, orderId);
            statement.executeUpdate();
        }
    }

    /**
     * Calculates the order total from its details.
     */
    private BigDecimal calculateTotal(
            ArrayList<OrderDetail> details
    ) {

        BigDecimal total =
                BigDecimal.ZERO;

        for (OrderDetail detail : details) {

            total = total.add(
                    detail.calculateSubtotal()
            );
        }

        return total;
    }

    /**
     * Ensures that an order contains at least one item.
     */
    private void validateDetails(
            ArrayList<OrderDetail> details
    ) {

        if (details == null || details.isEmpty()) {

            throw new IllegalArgumentException(
                    "An order must contain at least one product."
            );
        }
    }

    /**
     * Determines the next global order-detail number.
     */
    private int getNextOrderDetailNumber(
            Connection connection
    ) throws SQLException {

        String sql =
                "SELECT COALESCE(" +
                "MAX(CAST(SUBSTRING(" +
                "order_detail_id, 6) AS UNSIGNED)), " +
                "0) + 1 AS next_number " +
                "FROM order_details " +
                "WHERE order_detail_id LIKE 'ODET-%'";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            if (result.next()) {
                return result.getInt("next_number");
            }
        }

        return 1;
    }

    private Order findUsingSingleParameter(
            String sql,
            String value
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, value);

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {
                    return createOrderFromResult(result);
                }
            }
        }

        return null;
    }

    private Order getSingleOrder(
            String sql,
            String orderId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (orderId != null) {
                statement.setString(1, orderId);
            }

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {
                    return createOrderFromResult(result);
                }
            }
        }

        return null;
    }

    private Order createOrderFromResult(
            ResultSet result
    ) throws SQLException {

        return new Order(
                result.getString("order_id"),
                result.getString("customer_id"),
                result.getString("employee_id"),
                result.getDate("order_date")
                        .toLocalDate(),
                result.getDate("due_date")
                        .toLocalDate(),
                result.getString("order_status"),
                result.getBigDecimal("total_amount")
        );
    }

    private OrderDetail createOrderDetailFromResult(
            ResultSet result
    ) throws SQLException {

        return new OrderDetail(
                result.getString("order_detail_id"),
                result.getString("order_id"),
                result.getString("product_id"),
                result.getInt("quantity"),
                result.getBigDecimal("unit_price"),
                result.getBigDecimal("subtotal")
        );
    }
}