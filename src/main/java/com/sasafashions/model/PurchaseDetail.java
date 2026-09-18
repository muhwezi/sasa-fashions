package com.sasafashions.model;

import java.math.BigDecimal;

/**
 * Represents one material line contained in a purchase.
 *
 * <p>Each detail identifies the purchased material,
 * quantity and cost per unit.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class PurchaseDetail {

    private String purchaseDetailId;
    private String purchaseId;
    private String materialId;
    private BigDecimal quantity;
    private BigDecimal unitCost;

    /**
     * Creates an empty purchase-detail object.
     */
    public PurchaseDetail() {
    }

    /**
     * Creates a purchase detail without database identifiers.
     *
     * <p>This constructor is useful when entering new purchase
     * lines before the purchase has been saved.</p>
     *
     * @param materialId purchased material identifier
     * @param quantity quantity purchased
     * @param unitCost cost per unit
     */
    public PurchaseDetail(
            String materialId,
            BigDecimal quantity,
            BigDecimal unitCost
    ) {
        this.materialId = materialId;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    /**
     * Creates a complete purchase-detail object.
     *
     * @param purchaseDetailId unique detail identifier
     * @param purchaseId parent purchase identifier
     * @param materialId purchased material identifier
     * @param quantity quantity purchased
     * @param unitCost cost per unit
     */
    public PurchaseDetail(
            String purchaseDetailId,
            String purchaseId,
            String materialId,
            BigDecimal quantity,
            BigDecimal unitCost
    ) {
        this.purchaseDetailId = purchaseDetailId;
        this.purchaseId = purchaseId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public String getPurchaseDetailId() {
        return purchaseDetailId;
    }

    public void setPurchaseDetailId(
            String purchaseDetailId
    ) {
        this.purchaseDetailId =
                purchaseDetailId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(
            String purchaseId
    ) {
        this.purchaseId = purchaseId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(
            String materialId
    ) {
        this.materialId = materialId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(
            BigDecimal quantity
    ) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(
            BigDecimal unitCost
    ) {
        this.unitCost = unitCost;
    }

    /**
     * Calculates the line subtotal.
     *
     * @return quantity multiplied by unit cost
     */
    public BigDecimal calculateSubtotal() {

        if (quantity == null
                || unitCost == null) {
            return BigDecimal.ZERO;
        }

        return quantity.multiply(unitCost);
    }

    /**
     * Returns a readable purchase-detail description.
     *
     * @return material, quantity and unit cost
     */
    @Override
    public String toString() {

        return materialId
                + " | Quantity: "
                + quantity
                + " | Unit cost: "
                + unitCost;
    }
}