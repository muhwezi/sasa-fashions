package com.sasafashions.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a payment received for a tailoring order.
 *
 * @author HP
 * @version 1.0
 */
public class Payment {

    private String paymentId;
    private String orderId;
    private String receivedBy;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private String paymentMethod;
    private String referenceNumber;
    private String notes;

    /**
     * Creates an empty Payment.
     */
    public Payment() {
    }

    /**
     * Creates a Payment containing all fields.
     */
    public Payment(
            String paymentId,
            String orderId,
            String receivedBy,
            LocalDateTime paymentDate,
            BigDecimal amount,
            String paymentMethod,
            String referenceNumber,
            String notes
    ) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.receivedBy = receivedBy;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
        this.notes = notes;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(
            String paymentId
    ) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(
            String orderId
    ) {
        this.orderId = orderId;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(
            String receivedBy
    ) {
        this.receivedBy = receivedBy;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDateTime paymentDate
    ) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount
    ) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            String paymentMethod
    ) {
        this.paymentMethod = paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber
    ) {
        this.referenceNumber = referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Determines whether the payment has an external
     * transaction reference.
     *
     * @return true when a reference is available
     */
    public boolean hasReferenceNumber() {

        return referenceNumber != null
                && !referenceNumber.isBlank();
    }

    @Override
    public String toString() {

        return paymentId
                + " - "
                + amount;
    }
}