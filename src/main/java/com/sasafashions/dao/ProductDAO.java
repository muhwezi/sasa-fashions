package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Performs database operations involving products.
 *
 * This class saves, finds, updates, deletes, lists
 * and navigates product records.
 *
 * @author HP
 * @version 1.0
 */
public class ProductDAO {

    /**
     * Generates the next product ID.
     *
     * @return an ID such as PROD-0001
     * @throws SQLException when database access fails
     */
    public String generateNextProductId()
            throws SQLException {

        return IdGenerator.generateNextProductId();
    }

    /**
     * Saves a new product.
     *
     * @param product product to save
     * @return true when the product is saved
     * @throws SQLException when database access fails
     */
    public boolean saveProduct(Product product)
            throws SQLException {

        String sql =
                "INSERT INTO products (" +
                "product_id, product_name, description, " +
                "price, quantity" +
                ") VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    product.getProductId()
            );

            statement.setString(
                    2,
                    product.getProductName()
            );

            statement.setString(
                    3,
                    product.getDescription()
            );

            statement.setBigDecimal(
                    4,
                    product.getPrice()
            );

            statement.setInt(
                    5,
                    product.getQuantity()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Finds a product using its product ID.
     *
     * @param productId product ID to search for
     * @return matching Product, or null when not found
     * @throws SQLException when database access fails
     */
    public Product findProduct(String productId)
            throws SQLException {

        String sql =
                "SELECT * FROM products " +
                "WHERE product_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, productId);

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return createProductFromResult(
                            result
                    );
                }
            }
        }

        return null;
    }

    /**
     * Updates an existing product.
     *
     * @param product product containing updated information
     * @return true when the product is updated
     * @throws SQLException when database access fails
     */
    public boolean updateProduct(Product product)
            throws SQLException {

        String sql =
                "UPDATE products SET " +
                "product_name = ?, " +
                "description = ?, " +
                "price = ?, " +
                "quantity = ? " +
                "WHERE product_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    product.getProductName()
            );

            statement.setString(
                    2,
                    product.getDescription()
            );

            statement.setBigDecimal(
                    3,
                    product.getPrice()
            );

            statement.setInt(
                    4,
                    product.getQuantity()
            );

            statement.setString(
                    5,
                    product.getProductId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a product using its product ID.
     *
     * MySQL prevents deletion when the product is already
     * referenced by an order-detail record.
     *
     * @param productId product ID to delete
     * @return true when the product is deleted
     * @throws SQLException when database access fails
     */
    public boolean deleteProduct(String productId)
            throws SQLException {

        String sql =
                "DELETE FROM products " +
                "WHERE product_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, productId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all products.
     *
     * @return ArrayList containing all products
     * @throws SQLException when database access fails
     */
    public ArrayList<Product> findAllProducts()
            throws SQLException {

        ArrayList<Product> products =
                new ArrayList<>();

        String sql =
                "SELECT * FROM products " +
                "ORDER BY product_id ASC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                Product product =
                        createProductFromResult(result);

                products.add(product);
            }
        }

        return products;
    }

    /**
     * Retrieves the first product.
     *
     * @return first Product, or null when none exist
     * @throws SQLException when database access fails
     */
    public Product getFirstProduct()
            throws SQLException {

        String sql =
                "SELECT * FROM products " +
                "ORDER BY product_id ASC " +
                "LIMIT 1";

        return getSingleProduct(sql, null);
    }

    /**
     * Retrieves the last product.
     *
     * @return last Product, or null when none exist
     * @throws SQLException when database access fails
     */
    public Product getLastProduct()
            throws SQLException {

        String sql =
                "SELECT * FROM products " +
                "ORDER BY product_id DESC " +
                "LIMIT 1";

        return getSingleProduct(sql, null);
    }

    /**
     * Retrieves the product immediately after the
     * currently displayed product.
     *
     * @param currentProductId currently displayed ID
     * @return next Product, or null at the end
     * @throws SQLException when database access fails
     */
    public Product getNextProduct(
            String currentProductId
    ) throws SQLException {

        String sql =
                "SELECT * FROM products " +
                "WHERE product_id > ? " +
                "ORDER BY product_id ASC " +
                "LIMIT 1";

        return getSingleProduct(
                sql,
                currentProductId
        );
    }

    /**
     * Retrieves the product immediately before the
     * currently displayed product.
     *
     * @param currentProductId currently displayed ID
     * @return previous Product, or null at the beginning
     * @throws SQLException when database access fails
     */
    public Product getPreviousProduct(
            String currentProductId
    ) throws SQLException {

        String sql =
                "SELECT * FROM products " +
                "WHERE product_id < ? " +
                "ORDER BY product_id DESC " +
                "LIMIT 1";

        return getSingleProduct(
                sql,
                currentProductId
        );
    }

    /**
     * Shared method used by product navigation.
     *
     * @param sql query to execute
     * @param productId optional product ID
     * @return matching Product, or null
     * @throws SQLException when database access fails
     */
    private Product getSingleProduct(
            String sql,
            String productId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (productId != null) {

                statement.setString(
                        1,
                        productId
                );
            }

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return createProductFromResult(
                            result
                    );
                }
            }
        }

        return null;
    }

    /**
     * Converts the current database record into a
     * Product object.
     *
     * @param result product database record
     * @return constructed Product
     * @throws SQLException when a column cannot be read
     */
    private Product createProductFromResult(
            ResultSet result
    ) throws SQLException {

        return new Product(
                result.getString("product_id"),
                result.getString("product_name"),
                result.getString("description"),
                result.getBigDecimal("price"),
                result.getInt("quantity")
        );
    }
}