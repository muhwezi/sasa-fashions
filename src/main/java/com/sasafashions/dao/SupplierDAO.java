package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Supplier;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Provides database operations for supplier records.
 *
 * <p>This class implements the Data Access Object pattern.
 * It separates supplier database operations from the graphical
 * user interface and business model.</p>
 *
 * <p>The supported operations include saving, finding, editing,
 * deleting and navigating supplier records.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class SupplierDAO {

    /**
     * Generates the next supplier identifier.
     *
     * @return an identifier such as SUP-0001
     * @throws SQLException when the identifier cannot be generated
     */
    public String generateNextSupplierId()
            throws SQLException {

        return IdGenerator.generateNextSupplierId();
    }

    /**
     * Saves a new supplier in the database.
     *
     * @param supplier supplier information to save
     * @throws SQLException when the database operation fails
     * @throws IllegalArgumentException when required information is missing
     */
    public void saveSupplier(
            Supplier supplier
    ) throws SQLException {

        validateSupplier(supplier);

        if (isBlank(supplier.getSupplierId())) {
            supplier.setSupplierId(
                    generateNextSupplierId()
            );
        }

        if (supplier.getRegistrationDate() == null) {
            supplier.setRegistrationDate(
                    LocalDate.now()
            );
        }

        if (isBlank(supplier.getSupplierStatus())) {
            supplier.setSupplierStatus("Active");
        }

        String sql = """
                INSERT INTO suppliers (
                    supplier_id,
                    supplier_name,
                    telephone,
                    email,
                    address,
                    registration_date,
                    supplier_status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    supplier.getSupplierId()
            );

            statement.setString(
                    2,
                    supplier.getSupplierName().trim()
            );

            statement.setString(
                    3,
                    supplier.getTelephone().trim()
            );

            setNullableString(
                    statement,
                    4,
                    supplier.getEmail()
            );

            setNullableString(
                    statement,
                    5,
                    supplier.getAddress()
            );

            statement.setDate(
                    6,
                    Date.valueOf(
                            supplier.getRegistrationDate()
                    )
            );

            statement.setString(
                    7,
                    supplier.getSupplierStatus()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Finds a supplier using the supplier identifier.
     *
     * @param supplierId supplier identifier to search for
     * @return the matching supplier, or null when not found
     * @throws SQLException when the database operation fails
     */
    public Supplier findSupplier(
            String supplierId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM suppliers
                WHERE supplier_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    supplierId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return createSupplierFromResultSet(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Returns all suppliers in supplier-ID order.
     *
     * @return list containing all suppliers
     * @throws SQLException when the database operation fails
     */
    public ArrayList<Supplier> findAllSuppliers()
            throws SQLException {

        ArrayList<Supplier> suppliers =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM suppliers
                ORDER BY supplier_id
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {

                suppliers.add(
                        createSupplierFromResultSet(
                                resultSet
                        )
                );
            }
        }

        return suppliers;
    }

    /**
     * Returns suppliers whose status is Active.
     *
     * <p>This method will be used when creating purchases
     * because new purchases should normally be associated
     * with active suppliers.</p>
     *
     * @return list of active suppliers
     * @throws SQLException when the database operation fails
     */
    public ArrayList<Supplier> findActiveSuppliers()
            throws SQLException {

        ArrayList<Supplier> suppliers =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM suppliers
                WHERE supplier_status = 'Active'
                ORDER BY supplier_name
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {

                suppliers.add(
                        createSupplierFromResultSet(
                                resultSet
                        )
                );
            }
        }

        return suppliers;
    }

    /**
     * Updates an existing supplier record.
     *
     * @param supplier supplier containing the updated information
     * @return true when the supplier was updated
     * @throws SQLException when the database operation fails
     * @throws IllegalArgumentException when required information is missing
     */
    public boolean updateSupplier(
            Supplier supplier
    ) throws SQLException {

        validateSupplier(supplier);

        if (isBlank(supplier.getSupplierId())) {

            throw new IllegalArgumentException(
                    "Supplier ID is required when editing."
            );
        }

        if (supplier.getRegistrationDate() == null) {

            throw new IllegalArgumentException(
                    "Supplier registration date is required."
            );
        }

        String sql = """
                UPDATE suppliers
                SET supplier_name = ?,
                    telephone = ?,
                    email = ?,
                    address = ?,
                    registration_date = ?,
                    supplier_status = ?
                WHERE supplier_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    supplier.getSupplierName().trim()
            );

            statement.setString(
                    2,
                    supplier.getTelephone().trim()
            );

            setNullableString(
                    statement,
                    3,
                    supplier.getEmail()
            );

            setNullableString(
                    statement,
                    4,
                    supplier.getAddress()
            );

            statement.setDate(
                    5,
                    Date.valueOf(
                            supplier.getRegistrationDate()
                    )
            );

            statement.setString(
                    6,
                    supplier.getSupplierStatus()
            );

            statement.setString(
                    7,
                    supplier.getSupplierId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a supplier from the database.
     *
     * <p>After purchases are attached to the supplier, the
     * database foreign key will prevent unsafe deletion.</p>
     *
     * @param supplierId identifier of the supplier to delete
     * @return true when the supplier was deleted
     * @throws SQLException when the database operation fails
     */
    public boolean deleteSupplier(
            String supplierId
    ) throws SQLException {

        String sql = """
                DELETE FROM suppliers
                WHERE supplier_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    supplierId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Returns the first supplier record.
     *
     * @return first supplier, or null when the table is empty
     * @throws SQLException when the database operation fails
     */
    public Supplier findFirstSupplier()
            throws SQLException {

        String sql = """
                SELECT *
                FROM suppliers
                ORDER BY supplier_id ASC
                LIMIT 1
                """;

        return findSingleSupplier(
                sql,
                null
        );
    }

    /**
     * Returns the last supplier record.
     *
     * @return last supplier, or null when the table is empty
     * @throws SQLException when the database operation fails
     */
    public Supplier findLastSupplier()
            throws SQLException {

        String sql = """
                SELECT *
                FROM suppliers
                ORDER BY supplier_id DESC
                LIMIT 1
                """;

        return findSingleSupplier(
                sql,
                null
        );
    }

    /**
     * Returns the supplier immediately after the current supplier.
     *
     * @param currentSupplierId current supplier identifier
     * @return next supplier, or null when there is no next record
     * @throws SQLException when the database operation fails
     */
    public Supplier findNextSupplier(
            String currentSupplierId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM suppliers
                WHERE supplier_id > ?
                ORDER BY supplier_id ASC
                LIMIT 1
                """;

        return findSingleSupplier(
                sql,
                currentSupplierId
        );
    }

    /**
     * Returns the supplier immediately before the current supplier.
     *
     * @param currentSupplierId current supplier identifier
     * @return previous supplier, or null when there is no previous record
     * @throws SQLException when the database operation fails
     */
    public Supplier findPreviousSupplier(
            String currentSupplierId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM suppliers
                WHERE supplier_id < ?
                ORDER BY supplier_id DESC
                LIMIT 1
                """;

        return findSingleSupplier(
                sql,
                currentSupplierId
        );
    }

    /**
     * Runs a navigation query that returns a maximum
     * of one supplier.
     *
     * @param sql navigation SQL statement
     * @param supplierId optional current supplier identifier
     * @return matching supplier, or null when no record exists
     * @throws SQLException when the database operation fails
     */
    private Supplier findSingleSupplier(
            String sql,
            String supplierId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            if (supplierId != null) {
                statement.setString(
                        1,
                        supplierId
                );
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return createSupplierFromResultSet(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Converts the current database result row into
     * a Supplier object.
     *
     * @param resultSet database result containing supplier information
     * @return supplier created from the result
     * @throws SQLException when result values cannot be read
     */
    private Supplier createSupplierFromResultSet(
            ResultSet resultSet
    ) throws SQLException {

        Date registrationDate =
                resultSet.getDate(
                        "registration_date"
                );

        LocalDate localRegistrationDate = null;

        if (registrationDate != null) {
            localRegistrationDate =
                    registrationDate.toLocalDate();
        }

        return new Supplier(
                resultSet.getString("supplier_id"),
                resultSet.getString("supplier_name"),
                resultSet.getString("telephone"),
                resultSet.getString("email"),
                resultSet.getString("address"),
                localRegistrationDate,
                resultSet.getString("supplier_status")
        );
    }

    /**
     * Validates information required for saving or
     * updating a supplier.
     *
     * @param supplier supplier to validate
     * @throws IllegalArgumentException when required information is missing
     */
    private void validateSupplier(
            Supplier supplier
    ) {

        if (supplier == null) {

            throw new IllegalArgumentException(
                    "Supplier information is required."
            );
        }

        if (isBlank(supplier.getSupplierName())) {

            throw new IllegalArgumentException(
                    "Supplier name is required."
            );
        }

        if (isBlank(supplier.getTelephone())) {

            throw new IllegalArgumentException(
                    "Supplier telephone is required."
            );
        }

        if (!isBlank(supplier.getEmail())
                && !supplier.getEmail().contains("@")) {

            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }

        if (isBlank(supplier.getSupplierStatus())) {

            throw new IllegalArgumentException(
                    "Supplier status is required."
            );
        }
    }

    /**
     * Assigns a string to a prepared-statement parameter,
     * or SQL NULL when the string is blank.
     *
     * @param statement prepared SQL statement
     * @param parameterNumber parameter position
     * @param value value to assign
     * @throws SQLException when the parameter cannot be assigned
     */
    private void setNullableString(
            PreparedStatement statement,
            int parameterNumber,
            String value
    ) throws SQLException {

        if (isBlank(value)) {

            statement.setNull(
                    parameterNumber,
                    Types.VARCHAR
            );

        } else {

            statement.setString(
                    parameterNumber,
                    value.trim()
            );
        }
    }

    /**
     * Checks whether a string is null or contains
     * only whitespace.
     *
     * @param value string to inspect
     * @return true when the value is null or blank
     */
    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}