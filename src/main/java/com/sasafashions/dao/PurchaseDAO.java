package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Purchase;
import com.sasafashions.model.PurchaseDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides database operations for purchases and
 * purchase-detail records.
 *
 * <p>The class uses transactions to ensure that purchase
 * records, purchase details and material stock quantities
 * are updated as one complete operation.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class PurchaseDAO {

    /**
     * Generates the next purchase identifier.
     *
     * @return an identifier such as PUR-0001
     * @throws SQLException when the identifier cannot be generated
     */
    public String generateNextPurchaseId()
            throws SQLException {

        return IdGenerator.generateNextPurchaseId();
    }

    /**
     * Saves a purchase, its details and related stock changes
     * in one database transaction.
     *
     * @param purchase purchase to save
     * @throws SQLException when the database operation fails
     * @throws IllegalArgumentException when purchase data is invalid
     */
    public void savePurchaseWithDetails(
            Purchase purchase
    ) throws SQLException {

        preparePurchase(purchase);
        validatePurchase(purchase);

        if (isBlank(purchase.getPurchaseId())) {

            purchase.setPurchaseId(
                    generateNextPurchaseId()
            );
        }

        String purchaseSql = """
                INSERT INTO purchases (
                    purchase_id,
                    supplier_id,
                    employee_id,
                    purchase_date,
                    invoice_number,
                    purchase_status,
                    total_amount,
                    notes
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     purchaseSql
                             )) {

                    setPurchaseParameters(
                            statement,
                            purchase
                    );

                    statement.executeUpdate();
                }

                insertPurchaseDetails(
                        connection,
                        purchase
                );

                connection.commit();

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Updates a purchase and replaces its detail lines.
     *
     * <p>If the old purchase was completed, its previous stock
     * quantities are reversed before the new details are saved.
     * Any error rolls back the complete operation.</p>
     *
     * @param purchase purchase containing updated information
     * @throws SQLException when the database operation fails
     * @throws IllegalArgumentException when purchase data is invalid
     */
    public void updatePurchaseWithDetails(
            Purchase purchase
    ) throws SQLException {

        preparePurchase(purchase);
        validatePurchase(purchase);

        if (isBlank(purchase.getPurchaseId())) {

            throw new IllegalArgumentException(
                    "Purchase ID is required when editing."
            );
        }

        String updateSql = """
                UPDATE purchases
                SET supplier_id = ?,
                    employee_id = ?,
                    purchase_date = ?,
                    invoice_number = ?,
                    purchase_status = ?,
                    total_amount = ?,
                    notes = ?
                WHERE purchase_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                String oldStatus =
                        findPurchaseStatusForUpdate(
                                connection,
                                purchase.getPurchaseId()
                        );

                ArrayList<PurchaseDetail> oldDetails =
                        findPurchaseDetails(
                                connection,
                                purchase.getPurchaseId()
                        );

                if ("Completed".equalsIgnoreCase(
                        oldStatus
                )) {
                    reverseStock(
                            connection,
                            oldDetails
                    );
                }

                deletePurchaseDetails(
                        connection,
                        purchase.getPurchaseId()
                );

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     updateSql
                             )) {

                    statement.setString(
                            1,
                            purchase.getSupplierId()
                    );

                    statement.setString(
                            2,
                            purchase.getEmployeeId()
                    );

                    statement.setDate(
                            3,
                            Date.valueOf(
                                    purchase.getPurchaseDate()
                            )
                    );

                    setNullableString(
                            statement,
                            4,
                            purchase.getInvoiceNumber()
                    );

                    statement.setString(
                            5,
                            purchase.getPurchaseStatus()
                    );

                    statement.setBigDecimal(
                            6,
                            purchase.getTotalAmount()
                    );

                    setNullableString(
                            statement,
                            7,
                            purchase.getNotes()
                    );

                    statement.setString(
                            8,
                            purchase.getPurchaseId()
                    );

                    int affectedRows =
                            statement.executeUpdate();

                    if (affectedRows == 0) {

                        throw new SQLException(
                                "Purchase was not found: "
                                + purchase.getPurchaseId()
                        );
                    }
                }

                insertPurchaseDetails(
                        connection,
                        purchase
                );

                connection.commit();

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Finds a purchase and its detail lines.
     *
     * @param purchaseId purchase identifier
     * @return matching purchase, or null when not found
     * @throws SQLException when the database operation fails
     */
    public Purchase findPurchase(
            String purchaseId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM purchases
                WHERE purchase_id = ?
                """;

        Purchase purchase = null;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, purchaseId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    purchase =
                            createPurchaseFromResultSet(
                                    resultSet
                            );
                }
            }
        }

        if (purchase != null) {

            purchase.setDetails(
                    findPurchaseDetails(
                            purchase.getPurchaseId()
                    )
            );
        }

        return purchase;
    }

    /**
     * Returns all purchase-detail lines belonging to a purchase.
     *
     * @param purchaseId purchase identifier
     * @return list of purchase details
     * @throws SQLException when the database operation fails
     */
    public ArrayList<PurchaseDetail> findPurchaseDetails(
            String purchaseId
    ) throws SQLException {

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            return findPurchaseDetails(
                    connection,
                    purchaseId
            );
        }
    }

    /**
     * Returns every purchase in the database.
     *
     * @return list containing all purchases
     * @throws SQLException when the database operation fails
     */
    public ArrayList<Purchase> findAllPurchases()
            throws SQLException {

        ArrayList<Purchase> purchases =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM purchases
                ORDER BY purchase_date DESC,
                         purchase_id DESC
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

                purchases.add(
                        createPurchaseFromResultSet(
                                resultSet
                        )
                );
            }
        }

        return purchases;
    }

    /**
     * Returns purchases belonging to a particular supplier.
     *
     * @param supplierId supplier identifier
     * @return supplier purchase list
     * @throws SQLException when the database operation fails
     */
    public ArrayList<Purchase> findPurchasesBySupplier(
            String supplierId
    ) throws SQLException {

        ArrayList<Purchase> purchases =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM purchases
                WHERE supplier_id = ?
                ORDER BY purchase_date DESC,
                         purchase_id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, supplierId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    purchases.add(
                            createPurchaseFromResultSet(
                                    resultSet
                            )
                    );
                }
            }
        }

        return purchases;
    }

    /**
     * Deletes a purchase and reverses its stock quantities.
     *
     * <p>Deleting the purchase header also deletes its details
     * through the database cascade relationship.</p>
     *
     * @param purchaseId purchase identifier
     * @return true when the purchase was deleted
     * @throws SQLException when the database operation fails
     */
    public boolean deletePurchase(
            String purchaseId
    ) throws SQLException {

        String deleteSql = """
                DELETE FROM purchases
                WHERE purchase_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                String purchaseStatus =
                        findPurchaseStatusForUpdate(
                                connection,
                                purchaseId
                        );

                ArrayList<PurchaseDetail> details =
                        findPurchaseDetails(
                                connection,
                                purchaseId
                        );

                if ("Completed".equalsIgnoreCase(
                        purchaseStatus
                )) {
                    reverseStock(
                            connection,
                            details
                    );
                }

                boolean deleted;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     deleteSql
                             )) {

                    statement.setString(
                            1,
                            purchaseId
                    );

                    deleted =
                            statement.executeUpdate() > 0;
                }

                connection.commit();
                return deleted;

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Calculates the total value of completed purchases.
     *
     * @return total value of completed purchases
     * @throws SQLException when the database operation fails
     */
    public BigDecimal calculateCompletedPurchaseTotal()
            throws SQLException {

        String sql = """
                SELECT COALESCE(
                    SUM(total_amount),
                    0.00
                ) AS purchase_total
                FROM purchases
                WHERE purchase_status = 'Completed'
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            if (resultSet.next()) {

                return resultSet.getBigDecimal(
                        "purchase_total"
                );
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Returns the first purchase.
     *
     * @return first purchase, or null when none exists
     * @throws SQLException when the database operation fails
     */
    public Purchase findFirstPurchase()
            throws SQLException {

        String sql = """
                SELECT *
                FROM purchases
                ORDER BY purchase_id ASC
                LIMIT 1
                """;

        return findSinglePurchase(sql, null);
    }

    /**
     * Returns the last purchase.
     *
     * @return last purchase, or null when none exists
     * @throws SQLException when the database operation fails
     */
    public Purchase findLastPurchase()
            throws SQLException {

        String sql = """
                SELECT *
                FROM purchases
                ORDER BY purchase_id DESC
                LIMIT 1
                """;

        return findSinglePurchase(sql, null);
    }

    /**
     * Returns the purchase after the current purchase.
     *
     * @param currentPurchaseId current purchase identifier
     * @return next purchase, or null when none exists
     * @throws SQLException when the database operation fails
     */
    public Purchase findNextPurchase(
            String currentPurchaseId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM purchases
                WHERE purchase_id > ?
                ORDER BY purchase_id ASC
                LIMIT 1
                """;

        return findSinglePurchase(
                sql,
                currentPurchaseId
        );
    }

    /**
     * Returns the purchase before the current purchase.
     *
     * @param currentPurchaseId current purchase identifier
     * @return previous purchase, or null when none exists
     * @throws SQLException when the database operation fails
     */
    public Purchase findPreviousPurchase(
            String currentPurchaseId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM purchases
                WHERE purchase_id < ?
                ORDER BY purchase_id DESC
                LIMIT 1
                """;

        return findSinglePurchase(
                sql,
                currentPurchaseId
        );
    }

    /**
     * Prepares default purchase values and calculates
     * the purchase total.
     *
     * @param purchase purchase to prepare
     */
    private void preparePurchase(
            Purchase purchase
    ) {

        if (purchase == null) {
            return;
        }

        if (purchase.getPurchaseDate() == null) {

            purchase.setPurchaseDate(
                    LocalDate.now()
            );
        }

        if (isBlank(purchase.getPurchaseStatus())) {

            purchase.setPurchaseStatus(
                    "Completed"
            );
        }

        purchase.setTotalAmount(
                purchase.calculateTotalAmount()
        );
    }

    /**
     * Validates a purchase and its detail lines.
     *
     * @param purchase purchase to validate
     * @throws IllegalArgumentException when purchase data is invalid
     */
    private void validatePurchase(
            Purchase purchase
    ) {

        if (purchase == null) {

            throw new IllegalArgumentException(
                    "Purchase information is required."
            );
        }

        if (isBlank(purchase.getSupplierId())) {

            throw new IllegalArgumentException(
                    "Please select a supplier."
            );
        }

        if (isBlank(purchase.getEmployeeId())) {

            throw new IllegalArgumentException(
                    "Please select an employee."
            );
        }

        if (purchase.getPurchaseDate() == null) {

            throw new IllegalArgumentException(
                    "Purchase date is required."
            );
        }

        if (purchase.getDetails() == null
                || purchase.getDetails().isEmpty()) {

            throw new IllegalArgumentException(
                    "Add at least one material "
                    + "to the purchase."
            );
        }

        /*
         * HashSet prevents the same material from appearing
         * twice in one purchase.
         */
        Set<String> materialIds =
                new HashSet<>();

        for (PurchaseDetail detail
                : purchase.getDetails()) {

            if (detail == null
                    || isBlank(
                            detail.getMaterialId()
                    )) {

                throw new IllegalArgumentException(
                        "Every purchase line must "
                        + "contain a material."
                );
            }

            if (!materialIds.add(
                    detail.getMaterialId()
            )) {

                throw new IllegalArgumentException(
                        "The same material cannot appear "
                        + "twice in one purchase."
                );
            }

            if (detail.getQuantity() == null
                    || detail.getQuantity()
                            .compareTo(
                                    BigDecimal.ZERO
                            ) <= 0) {

                throw new IllegalArgumentException(
                        "Material quantity must be "
                        + "greater than zero."
                );
            }

            if (detail.getUnitCost() == null
                    || detail.getUnitCost()
                            .compareTo(
                                    BigDecimal.ZERO
                            ) < 0) {

                throw new IllegalArgumentException(
                        "Material unit cost cannot "
                        + "be negative."
                );
            }
        }
    }

    /**
     * Assigns purchase values to an insert statement.
     *
     * @param statement prepared insert statement
     * @param purchase purchase being saved
     * @throws SQLException when parameters cannot be assigned
     */
    private void setPurchaseParameters(
            PreparedStatement statement,
            Purchase purchase
    ) throws SQLException {

        statement.setString(
                1,
                purchase.getPurchaseId()
        );

        statement.setString(
                2,
                purchase.getSupplierId()
        );

        statement.setString(
                3,
                purchase.getEmployeeId()
        );

        statement.setDate(
                4,
                Date.valueOf(
                        purchase.getPurchaseDate()
                )
        );

        setNullableString(
                statement,
                5,
                purchase.getInvoiceNumber()
        );

        statement.setString(
                6,
                purchase.getPurchaseStatus()
        );

        statement.setBigDecimal(
                7,
                purchase.getTotalAmount()
        );

        setNullableString(
                statement,
                8,
                purchase.getNotes()
        );
    }

    /**
     * Saves purchase-detail lines and applies their
     * stock changes.
     *
     * @param connection active transaction connection
     * @param purchase parent purchase
     * @throws SQLException when details cannot be saved
     */
    private void insertPurchaseDetails(
            Connection connection,
            Purchase purchase
    ) throws SQLException {

        String sql = """
                INSERT INTO purchase_details (
                    purchase_detail_id,
                    purchase_id,
                    material_id,
                    quantity,
                    unit_cost
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        int nextDetailNumber =
                findNextDetailNumber(connection);

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            for (PurchaseDetail detail
                    : purchase.getDetails()) {

                String detailId =
                        String.format(
                                "PDT-%04d",
                                nextDetailNumber++
                        );

                detail.setPurchaseDetailId(
                        detailId
                );

                detail.setPurchaseId(
                        purchase.getPurchaseId()
                );

                statement.setString(1, detailId);

                statement.setString(
                        2,
                        purchase.getPurchaseId()
                );

                statement.setString(
                        3,
                        detail.getMaterialId()
                );

                statement.setBigDecimal(
                        4,
                        detail.getQuantity()
                );

                statement.setBigDecimal(
                        5,
                        detail.getUnitCost()
                );

                statement.addBatch();

                if (purchase.isCompleted()) {

                    increaseMaterialStock(
                            connection,
                            detail
                    );
                }
            }

            statement.executeBatch();
        }
    }

    /**
     * Finds the next purchase-detail number using
     * existing custom identifiers.
     *
     * @param connection active database connection
     * @return next numeric detail value
     * @throws SQLException when the number cannot be generated
     */
    private int findNextDetailNumber(
            Connection connection
    ) throws SQLException {

        String sql = """
                SELECT COALESCE(
                    MAX(
                        CAST(
                            SUBSTRING(
                                purchase_detail_id,
                                5
                            ) AS UNSIGNED
                        )
                    ),
                    0
                ) + 1 AS next_number
                FROM purchase_details
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            if (resultSet.next()) {

                return resultSet.getInt(
                        "next_number"
                );
            }
        }

        return 1;
    }

    /**
     * Increases material stock after a completed purchase.
     *
     * <p>The latest purchase cost becomes the material's
     * current unit cost.</p>
     *
     * @param connection active transaction connection
     * @param detail purchased material information
     * @throws SQLException when the material cannot be updated
     */
    private void increaseMaterialStock(
            Connection connection,
            PurchaseDetail detail
    ) throws SQLException {

        lockMaterial(
                connection,
                detail.getMaterialId()
        );

        String sql = """
                UPDATE materials
                SET quantity_in_stock =
                        quantity_in_stock + ?,
                    unit_cost = ?,
                    material_status =
                        CASE
                            WHEN material_status =
                                 'Discontinued'
                            THEN material_status
                            ELSE 'Available'
                        END
                WHERE material_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(
                    1,
                    detail.getQuantity()
            );

            statement.setBigDecimal(
                    2,
                    detail.getUnitCost()
            );

            statement.setString(
                    3,
                    detail.getMaterialId()
            );

            if (statement.executeUpdate() == 0) {

                throw new SQLException(
                        "Material was not found: "
                        + detail.getMaterialId()
                );
            }
        }
    }

    /**
     * Reverses material quantities previously added by
     * a completed purchase.
     *
     * @param connection active transaction connection
     * @param details details whose quantities must be reversed
     * @throws SQLException when stock cannot be reversed safely
     */
    private void reverseStock(
            Connection connection,
            ArrayList<PurchaseDetail> details
    ) throws SQLException {

        String sql = """
                UPDATE materials
                SET quantity_in_stock =
                        quantity_in_stock - ?
                WHERE material_id = ?
                """;

        for (PurchaseDetail detail : details) {

            BigDecimal currentQuantity =
                    lockMaterial(
                            connection,
                            detail.getMaterialId()
                    );

            if (currentQuantity.compareTo(
                    detail.getQuantity()
            ) < 0) {

                throw new SQLException(
                        "Purchase cannot be changed because "
                        + "the current stock for material "
                        + detail.getMaterialId()
                        + " is lower than the quantity "
                        + "being reversed."
                );
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setBigDecimal(
                        1,
                        detail.getQuantity()
                );

                statement.setString(
                        2,
                        detail.getMaterialId()
                );

                statement.executeUpdate();
            }
        }
    }

    /**
     * Locks a material row and returns its current quantity.
     *
     * @param connection active transaction connection
     * @param materialId material identifier
     * @return current material quantity
     * @throws SQLException when the material is not found
     */
    private BigDecimal lockMaterial(
            Connection connection,
            String materialId
    ) throws SQLException {

        String sql = """
                SELECT quantity_in_stock
                FROM materials
                WHERE material_id = ?
                FOR UPDATE
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, materialId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return resultSet.getBigDecimal(
                            "quantity_in_stock"
                    );
                }
            }
        }

        throw new SQLException(
                "Material was not found: "
                + materialId
        );
    }

    /**
     * Returns the status of a purchase while locking
     * the purchase row.
     *
     * @param connection active transaction connection
     * @param purchaseId purchase identifier
     * @return existing purchase status
     * @throws SQLException when the purchase is not found
     */
    private String findPurchaseStatusForUpdate(
            Connection connection,
            String purchaseId
    ) throws SQLException {

        String sql = """
                SELECT purchase_status
                FROM purchases
                WHERE purchase_id = ?
                FOR UPDATE
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, purchaseId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return resultSet.getString(
                            "purchase_status"
                    );
                }
            }
        }

        throw new SQLException(
                "Purchase was not found: "
                + purchaseId
        );
    }

    /**
     * Finds purchase details using an existing connection.
     *
     * @param connection database connection
     * @param purchaseId purchase identifier
     * @return purchase-detail list
     * @throws SQLException when the query fails
     */
    private ArrayList<PurchaseDetail> findPurchaseDetails(
            Connection connection,
            String purchaseId
    ) throws SQLException {

        ArrayList<PurchaseDetail> details =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM purchase_details
                WHERE purchase_id = ?
                ORDER BY purchase_detail_id
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, purchaseId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    details.add(
                            new PurchaseDetail(
                                    resultSet.getString(
                                            "purchase_detail_id"
                                    ),
                                    resultSet.getString(
                                            "purchase_id"
                                    ),
                                    resultSet.getString(
                                            "material_id"
                                    ),
                                    resultSet.getBigDecimal(
                                            "quantity"
                                    ),
                                    resultSet.getBigDecimal(
                                            "unit_cost"
                                    )
                            )
                    );
                }
            }
        }

        return details;
    }

    /**
     * Deletes existing purchase details before replacement.
     *
     * @param connection active transaction connection
     * @param purchaseId purchase identifier
     * @throws SQLException when details cannot be deleted
     */
    private void deletePurchaseDetails(
            Connection connection,
            String purchaseId
    ) throws SQLException {

        String sql = """
                DELETE FROM purchase_details
                WHERE purchase_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, purchaseId);
            statement.executeUpdate();
        }
    }

    /**
     * Runs a navigation query that returns one purchase.
     *
     * @param sql navigation query
     * @param purchaseId optional current purchase identifier
     * @return matching purchase, or null
     * @throws SQLException when the query fails
     */
    private Purchase findSinglePurchase(
            String sql,
            String purchaseId
    ) throws SQLException {

        Purchase purchase = null;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            if (purchaseId != null) {
                statement.setString(1, purchaseId);
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    purchase =
                            createPurchaseFromResultSet(
                                    resultSet
                            );
                }
            }
        }

        if (purchase != null) {

            purchase.setDetails(
                    findPurchaseDetails(
                            purchase.getPurchaseId()
                    )
            );
        }

        return purchase;
    }

    /**
     * Converts a database row into a Purchase object.
     *
     * @param resultSet purchase database result
     * @return purchase created from the result
     * @throws SQLException when values cannot be read
     */
    private Purchase createPurchaseFromResultSet(
            ResultSet resultSet
    ) throws SQLException {

        Date purchaseDate =
                resultSet.getDate(
                        "purchase_date"
                );

        return new Purchase(
                resultSet.getString("purchase_id"),
                resultSet.getString("supplier_id"),
                resultSet.getString("employee_id"),
                purchaseDate == null
                        ? null
                        : purchaseDate.toLocalDate(),
                resultSet.getString("invoice_number"),
                resultSet.getString("purchase_status"),
                resultSet.getBigDecimal("total_amount"),
                resultSet.getString("notes")
        );
    }

    /**
     * Assigns a string or SQL NULL to a statement parameter.
     *
     * @param statement prepared statement
     * @param parameterNumber parameter position
     * @param value string value
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
     * Checks whether a string is null or blank.
     *
     * @param value string to inspect
     * @return true when the string is null or blank
     */
    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }

    /**
     * Attempts to roll back a database transaction.
     *
     * @param connection active database connection
     */
    private void rollbackQuietly(
            Connection connection
    ) {

        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Preserve the original exception.
        }
 }
    }
