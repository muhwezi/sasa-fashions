package com.sasafashions.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a purchase of tailoring materials
 * from a supplier.
 *
 * <p>A purchase contains general transaction information
 * and a collection of purchase-detail lines.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class Purchase {

    private String purchaseId;
    private String supplierId;
    private String employeeId;
    private LocalDate purchaseDate;
    private String invoiceNumber;
    private String purchaseStatus;
    private BigDecimal totalAmount;
    private String notes;

    /*
     * ArrayList is one of the data structures used
     * in the project.
     */
    private ArrayList<PurchaseDetail> details;

    /**
     * Creates an empty purchase.
     */
    public Purchase() {
        details = new ArrayList<>();
    }

    /**
     * Creates a purchase with its general information.
     *
     * @param purchaseId unique purchase identifier
     * @param supplierId supplier providing the materials
     * @param employeeId employee recording the purchase
     * @param purchaseDate purchase date
     * @param invoiceNumber supplier invoice number
     * @param purchaseStatus purchase status
     * @param totalAmount total purchase amount
     * @param notes additional notes
     */
    public Purchase(
            String purchaseId,
            String supplierId,
            String employeeId,
            LocalDate purchaseDate,
            String invoiceNumber,
            String purchaseStatus,
            BigDecimal totalAmount,
            String notes
    ) {
        this.purchaseId = purchaseId;
        this.supplierId = supplierId;
        this.employeeId = employeeId;
        this.purchaseDate = purchaseDate;
        this.invoiceNumber = invoiceNumber;
        this.purchaseStatus = purchaseStatus;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.details = new ArrayList<>();
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(
            String purchaseId
    ) {
        this.purchaseId = purchaseId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(
            String supplierId
    ) {
        this.supplierId = supplierId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(
            String employeeId
    ) {
        this.employeeId = employeeId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(
            LocalDate purchaseDate
    ) {
        this.purchaseDate = purchaseDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(
            String invoiceNumber
    ) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(
            String purchaseStatus
    ) {
        this.purchaseStatus = purchaseStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount
    ) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the purchase-detail collection.
     *
     * @return list of purchase details
     */
    public ArrayList<PurchaseDetail> getDetails() {
        return details;
    }

    /**
     * Replaces the purchase-detail collection.
     *
     * @param details new purchase details
     */
    public void setDetails(
            List<PurchaseDetail> details
    ) {

        this.details = new ArrayList<>();

        if (details != null) {
            this.details.addAll(details);
        }
    }

    /**
     * Adds one material line to the purchase.
     *
     * @param detail purchase detail to add
     */
    public void addDetail(
            PurchaseDetail detail
    ) {

        if (detail != null) {
            details.add(detail);
        }
    }

    /**
     * Removes a material line from the purchase.
     *
     * @param detail purchase detail to remove
     * @return true when the detail was removed
     */
    public boolean removeDetail(
            PurchaseDetail detail
    ) {

        return details.remove(detail);
    }

    /**
     * Calculates the purchase total from its detail lines.
     *
     * @return combined subtotal of all details
     */
    public BigDecimal calculateTotalAmount() {

        BigDecimal calculatedTotal =
                BigDecimal.ZERO;

        for (PurchaseDetail detail : details) {

            calculatedTotal =
                    calculatedTotal.add(
                            detail.calculateSubtotal()
                    );
        }

        return calculatedTotal;
    }

    /**
     * Checks whether the purchase has been completed.
     *
     * @return true when the purchase status is Completed
     */
    public boolean isCompleted() {

        return "Completed".equalsIgnoreCase(
                purchaseStatus
        );
    }

    /**
     * Returns a readable purchase description.
     *
     * @return purchase identifier and supplier identifier
     */
    @Override
    public String toString() {

        return purchaseId
                + " - Supplier: "
                + supplierId;
    }
}