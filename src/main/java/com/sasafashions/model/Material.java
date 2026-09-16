package com.sasafashions.model;

import java.math.BigDecimal;

/**
 * Represents a tailoring material kept in stock
 * by Sasa Fashions.
 *
 * <p>A material has a unit of measurement, stock quantity,
 * reorder level, unit cost and availability status.</p>
 *
 * @author Joshua Muhwezi
 * @version 1.0
 */
public class Material {

    private String materialId;
    private String materialName;
    private String description;
    private String unitOfMeasure;
    private BigDecimal quantityInStock;
    private BigDecimal reorderLevel;
    private BigDecimal unitCost;
    private String materialStatus;

    /**
     * Creates an empty material object.
     */
    public Material() {
    }

    /**
     * Creates a material with complete information.
     *
     * @param materialId unique material identifier
     * @param materialName name of the material
     * @param description material description
     * @param unitOfMeasure measurement unit
     * @param quantityInStock current stock quantity
     * @param reorderLevel quantity at which more stock is required
     * @param unitCost cost per unit
     * @param materialStatus availability status
     */
    public Material(
            String materialId,
            String materialName,
            String description,
            String unitOfMeasure,
            BigDecimal quantityInStock,
            BigDecimal reorderLevel,
            BigDecimal unitCost,
            String materialStatus
    ) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.unitCost = unitCost;
        this.materialStatus = materialStatus;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(
            String materialName
    ) {
        this.materialName = materialName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(
            String unitOfMeasure
    ) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public BigDecimal getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(
            BigDecimal quantityInStock
    ) {
        this.quantityInStock = quantityInStock;
    }

    public BigDecimal getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(
            BigDecimal reorderLevel
    ) {
        this.reorderLevel = reorderLevel;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(
            BigDecimal unitCost
    ) {
        this.unitCost = unitCost;
    }

    public String getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(
            String materialStatus
    ) {
        this.materialStatus = materialStatus;
    }

    /**
     * Calculates the total monetary value of the material stock.
     *
     * @return quantity in stock multiplied by unit cost
     */
    public BigDecimal calculateStockValue() {

        if (quantityInStock == null
                || unitCost == null) {
            return BigDecimal.ZERO;
        }

        return quantityInStock.multiply(unitCost);
    }

    /**
     * Checks whether the material has reached its reorder level.
     *
     * @return true when stock is equal to or below reorder level
     */
    public boolean needsReordering() {

        if (quantityInStock == null
                || reorderLevel == null) {
            return false;
        }

        return quantityInStock.compareTo(
                reorderLevel
        ) <= 0;
    }

    /**
     * Returns a readable material description.
     *
     * @return material ID and name
     */
    @Override
    public String toString() {
        return materialId + " - " + materialName;
    }
}