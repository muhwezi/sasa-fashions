package com.sasafashions.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents tailoring measurements recorded for a customer.
 *
 * Each Measurement object corresponds to one record in
 * the measurements database table.
 *
 * @author HP
 * @version 1.0
 */
public class Measurement {

    private String measurementId;
    private String customerId;
    private BigDecimal chest;
    private BigDecimal waist;
    private BigDecimal hip;
    private BigDecimal shoulder;
    private BigDecimal sleeveLength;
    private BigDecimal trouserLength;
    private LocalDate dateTaken;

    /**
     * Creates an empty Measurement object.
     */
    public Measurement() {
    }

    /**
     * Creates a Measurement containing all fields.
     *
     * @param measurementId unique measurement ID
     * @param customerId measured customer
     * @param chest chest measurement
     * @param waist waist measurement
     * @param hip hip measurement
     * @param shoulder shoulder measurement
     * @param sleeveLength sleeve length
     * @param trouserLength trouser length
     * @param dateTaken date measurements were taken
     */
    public Measurement(
            String measurementId,
            String customerId,
            BigDecimal chest,
            BigDecimal waist,
            BigDecimal hip,
            BigDecimal shoulder,
            BigDecimal sleeveLength,
            BigDecimal trouserLength,
            LocalDate dateTaken
    ) {
        this.measurementId = measurementId;
        this.customerId = customerId;
        this.chest = chest;
        this.waist = waist;
        this.hip = hip;
        this.shoulder = shoulder;
        this.sleeveLength = sleeveLength;
        this.trouserLength = trouserLength;
        this.dateTaken = dateTaken;
    }

    public String getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(
            String measurementId
    ) {
        this.measurementId = measurementId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            String customerId
    ) {
        this.customerId = customerId;
    }

    public BigDecimal getChest() {
        return chest;
    }

    public void setChest(BigDecimal chest) {
        this.chest = chest;
    }

    public BigDecimal getWaist() {
        return waist;
    }

    public void setWaist(BigDecimal waist) {
        this.waist = waist;
    }

    public BigDecimal getHip() {
        return hip;
    }

    public void setHip(BigDecimal hip) {
        this.hip = hip;
    }

    public BigDecimal getShoulder() {
        return shoulder;
    }

    public void setShoulder(
            BigDecimal shoulder
    ) {
        this.shoulder = shoulder;
    }

    public BigDecimal getSleeveLength() {
        return sleeveLength;
    }

    public void setSleeveLength(
            BigDecimal sleeveLength
    ) {
        this.sleeveLength = sleeveLength;
    }

    public BigDecimal getTrouserLength() {
        return trouserLength;
    }

    public void setTrouserLength(
            BigDecimal trouserLength
    ) {
        this.trouserLength = trouserLength;
    }

    public LocalDate getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(
            LocalDate dateTaken
    ) {
        this.dateTaken = dateTaken;
    }

    /**
     * Returns a readable measurement description.
     *
     * @return measurement ID and customer ID
     */
    @Override
    public String toString() {

        return measurementId
                + " - "
                + customerId;
    }
}