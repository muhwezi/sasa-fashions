CREATE DATABASE IF NOT EXISTS sasa_fashions;

USE sasa_fashions;

CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(15) PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL UNIQUE,
    gender ENUM('Male', 'Female') NOT NULL,
    address VARCHAR(150),
    registration_date DATE NOT NULL,
    customer_status ENUM('Active', 'Inactive')
        NOT NULL DEFAULT 'Active'
);