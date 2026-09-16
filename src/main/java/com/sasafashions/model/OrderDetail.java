package com.sasafashions.model;

import java.math.BigDecimal;

/**
 * Represents one product line contained in an order.
 *
 * @author HP
 * @version 1.0
 */
public class OrderDetail {

    private String orderDetailId;
    private String orderId;
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    /**
     * Creates an empty OrderDetail.
     */
    public OrderDetail() {
    }

    /**
     * Creates a new detail before it is saved.
     *
     * The subtotal is calculated in Java for display.
     * MySQL calculates and stores it again automatically.
     */
    public OrderDetail(
            String orderDetailId,
            String orderId,
            String productId,
            int quantity,
            BigDecimal unitPrice
    ) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }

    /**
     * Creates a detail retrieved from the database.
     */
    public OrderDetail(
            String orderDetailId,
            String orderId,
            String productId,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(
            String orderDetailId
    ) {
        this.orderDetailId = orderDetailId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(
            String productId
    ) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(
            BigDecimal unitPrice
    ) {
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }

    public BigDecimal getSubtotal() {

        if (subtotal == null) {
            subtotal = calculateSubtotal();
        }

        return subtotal;
    }

    public void setSubtotal(
            BigDecimal subtotal
    ) {
        this.subtotal = subtotal;
    }

    /**
     * Calculates quantity multiplied by unit price.
     *
     * @return calculated subtotal
     */
    public BigDecimal calculateSubtotal() {

        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }

        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    @Override
    public String toString() {

        return orderDetailId
                + " - "
                + productId;
    }
}