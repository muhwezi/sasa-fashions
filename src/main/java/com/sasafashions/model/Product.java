package com.sasafashions.model;

import java.math.BigDecimal;

/**
 * Represents a tailoring product offered by Sasa Fashions.
 *
 * A Product object corresponds to one record in the
 * products database table.
 *
 * @author SASA Group
 * @version 1.0
 */
public class Product {

    private String productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private int quantity;

    /**
     * Creates an empty Product object.
     */
    public Product() {
    }

    /**
     * Creates a Product containing all fields.
     *
     * @param productId unique product ID
     * @param productName product name
     * @param description product description
     * @param price selling price
     * @param quantity available quantity
     */
    public Product(
            String productId,
            String productName,
            String description,
            BigDecimal price,
            int quantity
    ) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(
            String productId
    ) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(
            String productName
    ) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(
            BigDecimal price
    ) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(
            int quantity
    ) {
        this.quantity = quantity;
    }

    /**
     * Calculates the total value of the available product.
     *
     * @return price multiplied by quantity
     */
    public BigDecimal calculateStockValue() {

        return price.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    /**
     * Returns a readable product description.
     *
     * @return product ID and product name
     */
    @Override
    public String toString() {

        return productId
                + " - "
                + productName;
    }
}