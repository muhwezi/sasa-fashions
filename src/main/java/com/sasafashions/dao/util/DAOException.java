package com.sasafashions.dao.util;

/**
 * Represents an error that occurs while a DAO class
 * is communicating with the database.
 *
 * @author SASA Group
 */
public class DAOException extends RuntimeException {

    /**
     * Creates an exception containing an error message.
     *
     * @param message description of the error
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Creates an exception containing an error message
     * and the original cause.
     *
     * @param message description of the error
     * @param cause original exception
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}