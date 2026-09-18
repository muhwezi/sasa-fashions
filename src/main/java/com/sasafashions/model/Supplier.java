package com.sasafashions.model;

import java.time.LocalDate;

/**
 * Represents a supplier who provides tailoring materials
 * to Sasa Fashions.
 *
 * <p>The class stores the supplier's contact information,
 * registration date and operating status.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class Supplier {

    private String supplierId;
    private String supplierName;
    private String telephone;
    private String email;
    private String address;
    private LocalDate registrationDate;
    private String supplierStatus;

    /**
     * Creates an empty supplier object.
     */
    public Supplier() {
    }

    /**
     * Creates a supplier with complete information.
     *
     * @param supplierId unique supplier identifier
     * @param supplierName name of the supplier
     * @param telephone supplier telephone number
     * @param email supplier email address
     * @param address physical or postal address
     * @param registrationDate date the supplier was registered
     * @param supplierStatus current supplier status
     */
    public Supplier(
            String supplierId,
            String supplierName,
            String telephone,
            String email,
            String address,
            LocalDate registrationDate,
            String supplierStatus
    ) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
        this.registrationDate = registrationDate;
        this.supplierStatus = supplierStatus;
    }

    /**
     * Gets the supplier identifier.
     *
     * @return supplier identifier
     */
    public String getSupplierId() {
        return supplierId;
    }

    /**
     * Sets the supplier identifier.
     *
     * @param supplierId supplier identifier
     */
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * Gets the supplier name.
     *
     * @return supplier name
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * Sets the supplier name.
     *
     * @param supplierName supplier name
     */
    public void setSupplierName(
            String supplierName
    ) {
        this.supplierName = supplierName;
    }

    /**
     * Gets the supplier telephone number.
     *
     * @return telephone number
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the supplier telephone number.
     *
     * @param telephone telephone number
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Gets the supplier email address.
     *
     * @return supplier email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the supplier email address.
     *
     * @param email supplier email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the supplier address.
     *
     * @return supplier address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the supplier address.
     *
     * @param address supplier address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the registration date.
     *
     * @return supplier registration date
     */
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Sets the registration date.
     *
     * @param registrationDate registration date
     */
    public void setRegistrationDate(
            LocalDate registrationDate
    ) {
        this.registrationDate = registrationDate;
    }

    /**
     * Gets the supplier status.
     *
     * @return supplier status
     */
    public String getSupplierStatus() {
        return supplierStatus;
    }

    /**
     * Sets the supplier status.
     *
     * @param supplierStatus supplier status
     */
    public void setSupplierStatus(
            String supplierStatus
    ) {
        this.supplierStatus = supplierStatus;
    }

    /**
     * Checks whether the supplier is active.
     *
     * @return true when the supplier is active
     */
    public boolean isActive() {
        return "Active".equalsIgnoreCase(
                supplierStatus
        );
    }

    /**
     * Returns a readable supplier description.
     *
     * @return supplier ID and name
     */
    @Override
    public String toString() {
        return supplierId + " - " + supplierName;
    }
}