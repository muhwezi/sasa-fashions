package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Material;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Provides database operations for tailoring materials.
 *
 * <p>The class separates material database operations from
 * the graphical user interface. It supports saving, finding,
 * updating, deleting and navigating material records.</p>
 *
 * <p>It also provides methods for retrieving available
 * materials and materials requiring restocking.</p>
 *
 * @author Joshua Muhwezi
 * @version 1.0
 */
public class MaterialDAO {

    /**
     * Generates the next material identifier.
     *
     * @return an identifier such as MAT-0001
     * @throws SQLException when the identifier cannot be generated
     */
    public String generateNextMaterialId()
            throws SQLException {

        return IdGenerator.generateNextMaterialId();
    }

    /**
     * Saves a new material.
     *
     * @param material material information to save
     * @throws SQLException when the database operation fails
     * @throws IllegalArgumentException when the material is invalid
     */
    public void saveMaterial(
            Material material
    ) throws SQLException {

        validateMaterial(material);

        if (isBlank(material.getMaterialId())) {

            material.setMaterialId(
                    generateNextMaterialId()
            );
        }

        String sql = """
                INSERT INTO materials (
                    material_id,
                    material_name,
                    description,
                    unit_of_measure,
                    quantity_in_stock,
                    reorder_level,
                    unit_cost,
                    material_status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    material.getMaterialId()
            );

            statement.setString(
                    2,
                    material.getMaterialName().trim()
            );

            setNullableString(
                    statement,
                    3,
                    material.getDescription()
            );

            statement.setString(
                    4,
                    material.getUnitOfMeasure().trim()
            );

            statement.setBigDecimal(
                    5,
                    valueOrZero(
                            material.getQuantityInStock()
                    )
            );

            statement.setBigDecimal(
                    6,
                    valueOrZero(
                            material.getReorderLevel()
                    )
            );

            statement.setBigDecimal(
                    7,
                    valueOrZero(
                            material.getUnitCost()
                    )
            );

            statement.setString(
                    8,
                    material.getMaterialStatus()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Finds a material using its identifier.
     *
     * @param materialId material identifier
     * @return matching material, or null when not found
     * @throws SQLException when the database operation fails
     */
    public Material findMaterial(
            String materialId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM materials
                WHERE material_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, materialId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return createMaterialFromResultSet(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Returns every material in the database.
     *
     * @return list containing all materials
     * @throws SQLException when the database operation fails
     */
    public ArrayList<Material> findAllMaterials()
            throws SQLException {

        ArrayList<Material> materials =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM materials
                ORDER BY material_id
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

                materials.add(
                        createMaterialFromResultSet(
                                resultSet
                        )
                );
            }
        }

        return materials;
    }

    /**
     * Returns materials that are available for use.
     *
     * @return list of available materials
     * @throws SQLException when the database operation fails
     */
    public ArrayList<Material> findAvailableMaterials()
            throws SQLException {

        ArrayList<Material> materials =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM materials
                WHERE material_status = 'Available'
                ORDER BY material_name
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

                materials.add(
                        createMaterialFromResultSet(
                                resultSet
                        )
                );
            }
        }

        return materials;
    }

    /**
     * Finds materials whose quantity has reached or fallen
     * below the configured reorder level.
     *
     * @return list of materials requiring restocking
     * @throws SQLException when the database operation fails
     */
    public ArrayList<Material> findLowStockMaterials()
            throws SQLException {

        ArrayList<Material> materials =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM materials
                WHERE quantity_in_stock <= reorder_level
                  AND material_status <> 'Discontinued'
                ORDER BY quantity_in_stock ASC,
                         material_name ASC
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

                materials.add(
                        createMaterialFromResultSet(
                                resultSet
                        )
                );
            }
        }

        return materials;
    }

    /**
     * Updates an existing material.
     *
     * @param material material containing updated information
     * @return true when the material was updated
     * @throws SQLException when the database operation fails
     * @throws IllegalArgumentException when the material is invalid
     */
    public boolean updateMaterial(
            Material material
    ) throws SQLException {

        validateMaterial(material);

        if (isBlank(material.getMaterialId())) {

            throw new IllegalArgumentException(
                    "Material ID is required when editing."
            );
        }

        String sql = """
                UPDATE materials
                SET material_name = ?,
                    description = ?,
                    unit_of_measure = ?,
                    quantity_in_stock = ?,
                    reorder_level = ?,
                    unit_cost = ?,
                    material_status = ?
                WHERE material_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    material.getMaterialName().trim()
            );

            setNullableString(
                    statement,
                    2,
                    material.getDescription()
            );

            statement.setString(
                    3,
                    material.getUnitOfMeasure().trim()
            );

            statement.setBigDecimal(
                    4,
                    valueOrZero(
                            material.getQuantityInStock()
                    )
            );

            statement.setBigDecimal(
                    5,
                    valueOrZero(
                            material.getReorderLevel()
                    )
            );

            statement.setBigDecimal(
                    6,
                    valueOrZero(
                            material.getUnitCost()
                    )
            );

            statement.setString(
                    7,
                    material.getMaterialStatus()
            );

            statement.setString(
                    8,
                    material.getMaterialId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a material.
     *
     * <p>After purchase-detail records refer to the material,
     * its foreign key will prevent unsafe deletion.</p>
     *
     * @param materialId material identifier
     * @return true when the material was deleted
     * @throws SQLException when the database operation fails
     */
    public boolean deleteMaterial(
            String materialId
    ) throws SQLException {

        String sql = """
                DELETE FROM materials
                WHERE material_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, materialId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Calculates the value of all material stock.
     *
     * @return total value of all materials currently in stock
     * @throws SQLException when the database operation fails
     */
    public BigDecimal calculateTotalStockValue()
            throws SQLException {

        String sql = """
                SELECT COALESCE(
                    SUM(quantity_in_stock * unit_cost),
                    0.00
                ) AS total_stock_value
                FROM materials
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
                        "total_stock_value"
                );
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Returns the first material.
     *
     * @return first material, or null when no records exist
     * @throws SQLException when the database operation fails
     */
    public Material findFirstMaterial()
            throws SQLException {

        String sql = """
                SELECT *
                FROM materials
                ORDER BY material_id ASC
                LIMIT 1
                """;

        return findSingleMaterial(sql, null);
    }

    /**
     * Returns the last material.
     *
     * @return last material, or null when no records exist
     * @throws SQLException when the database operation fails
     */
    public Material findLastMaterial()
            throws SQLException {

        String sql = """
                SELECT *
                FROM materials
                ORDER BY material_id DESC
                LIMIT 1
                """;

        return findSingleMaterial(sql, null);
    }

    /**
     * Returns the material after the current record.
     *
     * @param currentMaterialId current material identifier
     * @return next material, or null when no next record exists
     * @throws SQLException when the database operation fails
     */
    public Material findNextMaterial(
            String currentMaterialId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM materials
                WHERE material_id > ?
                ORDER BY material_id ASC
                LIMIT 1
                """;

        return findSingleMaterial(
                sql,
                currentMaterialId
        );
    }

    /**
     * Returns the material before the current record.
     *
     * @param currentMaterialId current material identifier
     * @return previous material, or null when none exists
     * @throws SQLException when the database operation fails
     */
    public Material findPreviousMaterial(
            String currentMaterialId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM materials
                WHERE material_id < ?
                ORDER BY material_id DESC
                LIMIT 1
                """;

        return findSingleMaterial(
                sql,
                currentMaterialId
        );
    }

    /**
     * Runs a navigation query that returns a maximum
     * of one material.
     *
     * @param sql navigation SQL statement
     * @param materialId optional current material identifier
     * @return matching material, or null when none exists
     * @throws SQLException when the query fails
     */
    private Material findSingleMaterial(
            String sql,
            String materialId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            if (materialId != null) {
                statement.setString(1, materialId);
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return createMaterialFromResultSet(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Converts the current database row into a Material object.
     *
     * @param resultSet database result containing material data
     * @return material created from the result
     * @throws SQLException when result values cannot be read
     */
    private Material createMaterialFromResultSet(
            ResultSet resultSet
    ) throws SQLException {

        return new Material(
                resultSet.getString("material_id"),
                resultSet.getString("material_name"),
                resultSet.getString("description"),
                resultSet.getString("unit_of_measure"),
                resultSet.getBigDecimal(
                        "quantity_in_stock"
                ),
                resultSet.getBigDecimal(
                        "reorder_level"
                ),
                resultSet.getBigDecimal(
                        "unit_cost"
                ),
                resultSet.getString(
                        "material_status"
                )
        );
    }

    /**
     * Validates information required to save or
     * update a material.
     *
     * @param material material to validate
     * @throws IllegalArgumentException when information is invalid
     */
    private void validateMaterial(
            Material material
    ) {

        if (material == null) {

            throw new IllegalArgumentException(
                    "Material information is required."
            );
        }

        if (isBlank(material.getMaterialName())) {

            throw new IllegalArgumentException(
                    "Material name is required."
            );
        }

        if (isBlank(material.getUnitOfMeasure())) {

            throw new IllegalArgumentException(
                    "Unit of measurement is required."
            );
        }

        validateNonNegative(
                material.getQuantityInStock(),
                "Quantity in stock"
        );

        validateNonNegative(
                material.getReorderLevel(),
                "Reorder level"
        );

        validateNonNegative(
                material.getUnitCost(),
                "Unit cost"
        );

        if (isBlank(material.getMaterialStatus())) {

            throw new IllegalArgumentException(
                    "Material status is required."
            );
        }
    }

    /**
     * Confirms that a decimal value is not negative.
     *
     * @param value value to validate
     * @param fieldName name displayed in validation errors
     * @throws IllegalArgumentException when the value is negative
     */
    private void validateNonNegative(
            BigDecimal value,
            String fieldName
    ) {

        if (value != null
                && value.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

            throw new IllegalArgumentException(
                    fieldName + " cannot be negative."
            );
        }
    }

    /**
     * Converts a null decimal into zero.
     *
     * @param value possible null decimal
     * @return original decimal or zero
     */
    private BigDecimal valueOrZero(
            BigDecimal value
    ) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }

    /**
     * Assigns a string parameter or SQL NULL
     * when the supplied value is blank.
     *
     * @param statement prepared SQL statement
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
     * @param value string to check
     * @return true when the string is null or blank
     */
    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}