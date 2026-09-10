package com.sasafashions.model;

import java.time.LocalDate;

public class Customer {

    private String customerId;
    private String customerName;
    private String telephone;
    private String gender;
    private String address;
    private LocalDate registrationDate;
    private String customerStatus;

    // Empty constructor
    public Customer() {
    }

    // Constructor containing all customer fields
    public Customer(
            String customerId,
            String customerName,
            String telephone,
            String gender,
            String address,
            LocalDate registrationDate,
            String customerStatus
    ) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.telephone = telephone;
        this.gender = gender;
        this.address = address;
        this.registrationDate = registrationDate;
        this.customerStatus = customerStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
}