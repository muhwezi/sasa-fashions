package com.sasafashions.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a tailoring order placed by a customer.
 *
 * @author HP
 * @version 1.0
 */
public class Order {

    private String orderId;
    private String customerId;
    private String employeeId;
    private LocalDate orderDate;
    private LocalDate dueDate;
    private String orderStatus;
    private BigDecimal totalAmount;

    /**
     * Creates an empty Order.
     */
    public Order() {
    }

    /**
     * Creates an Order containing all fields.
     */
    public Order(
            String orderId,
            String customerId,
            String employeeId,
            LocalDate orderDate,
            LocalDate dueDate,
            String orderStatus,
            BigDecimal totalAmount
    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            String customerId
    ) {
        this.customerId = customerId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(
            String employeeId
    ) {
        this.employeeId = employeeId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(
            LocalDate orderDate
    ) {
        this.orderDate = orderDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(
            LocalDate dueDate
    ) {
        this.dueDate = dueDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(
            String orderStatus
    ) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount
    ) {
        this.totalAmount = totalAmount;
    }

    /**
     * Checks whether the order is still active.
     *
     * @return true for Pending, In Progress or Ready
     */
    public boolean isActive() {

        return "Pending".equals(orderStatus)
                || "In Progress".equals(orderStatus)
                || "Ready".equals(orderStatus);
    }

    /**
     * Returns an order description.
     *
     * @return order ID and status
     */
    @Override
    public String toString() {

        return orderId
                + " - "
                + orderStatus;
    }
}