package com.sasafashions.dao.util;

import com.sasafashions.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Generates custom IDs used by Sasa Fashions.
 *
 * @author SASA Group
 * @version 1.0
 */
public final class IdGenerator {

    /**
     * Prevents objects of this utility class from
     * being created.
     */
    
    
    
    private IdGenerator() {
    }

    /**
     * Generates the next customer ID.
     *
     * @return ID such as CUST-0001
     * @throws SQLException when database access fails
     */
    public static String generateNextCustomerId()
            throws SQLException {

        return generateNextId(
                "customers",
                "customer_id",
                "CUST-",
                4
        );
    }

    /**
     * Generates the next employee ID.
     *
     * @return ID such as EMP-0001
     * @throws SQLException when database access fails
     */
    public static String generateNextEmployeeId()
            throws SQLException {

        return generateNextId(
                "employees",
                "employee_id",
                "EMP-",
                4
        );
    }

    /**
     * Generates the next product ID.
     *
     * @return ID such as PROD-0001
     * @throws SQLException when database access fails
     */
    public static String generateNextProductId()
            throws SQLException {

        return generateNextId(
                "products",
                "product_id",
                "PROD-",
                4
        );
    }

    /**
     * Shared method used to generate custom IDs.
     *
     * @param tableName database table
     * @param idColumn column containing the ID
     * @param prefix ID prefix
     * @param numberLength number of digits
     * @return the next generated custom ID
     * @throws SQLException when database access fails
     */
    
    /**
 * Generates the next measurement ID.
 *
 * @return ID such as MEAS-0001
 * @throws SQLException when database access fails
 */
public static String generateNextMeasurementId()
        throws SQLException {

    return generateNextId(
            "measurements",
            "measurement_id",
            "MEAS-",
            4
    );
}
    /**
 * Generates the next order ID.
 *
 * @return ID such as ORD-0001
 * @throws SQLException when database access fails
 */
public static String generateNextOrderId()
        throws SQLException {

    return generateNextId(
            "orders",
            "order_id",
            "ORD-",
            4
    );
}

/**
 * Generates the next order-detail ID.
 *
 * @return ID such as ODET-0001
 * @throws SQLException when database access fails
 */
public static String generateNextOrderDetailId()
        throws SQLException {

    return generateNextId(
            "order_details",
            "order_detail_id",
            "ODET-",
            4
    );
}

/**
 * Generates the next payment ID.
 *
 * @return ID such as PAY-0001
 * @throws SQLException when database access fails
 */
public static String generateNextPaymentId()
        throws SQLException {

    return generateNextId(
            "payments",
            "payment_id",
            "PAY-",
            4
    );
}


/**
 * Generates the next supplier identifier.
 *
 * <p>The generated identifier follows the format
 * SUP-0001, SUP-0002 and so on.</p>
 *
 * @return the next available supplier identifier
 * @throws SQLException when the identifier cannot be generated
 */
public static String generateNextSupplierId()
        throws SQLException {

    return generateNextId(
            "suppliers",
            "supplier_id",
            "SUP-",
            4
    );
}

/**
 * Generates the next material identifier.
 *
 * <p>The generated identifier follows the format
 * MAT-0001, MAT-0002 and so on.</p>
 *
 * @return the next available material identifier
 * @throws SQLException when the identifier cannot be generated
 */
public static String generateNextMaterialId()
        throws SQLException {

    return generateNextId(
            "materials",
            "material_id",
            "MAT-",
            4
    );
}

/**
 * Generates the next purchase identifier.
 *
 * @return an identifier such as PUR-0001
 * @throws SQLException when the identifier cannot be generated
 */
public static String generateNextPurchaseId()
        throws SQLException {

    return generateNextId(
            "purchases",
            "purchase_id",
            "PUR-",
            4
    );
}

/**
 * Generates the next purchase-detail identifier.
 *
 * @return an identifier such as PDT-0001
 * @throws SQLException when the identifier cannot be generated
 */
public static String generateNextPurchaseDetailId()
        throws SQLException {

    return generateNextId(
            "purchase_details",
            "purchase_detail_id",
            "PDT-",
            4
    );
}

/**
 * Generates the next system-user identifier.
 *
 * @return an identifier such as USR-0001
 * @throws SQLException when the identifier cannot be generated
 */
public static String generateNextUserId()
        throws SQLException {

    return generateNextId(
            "users",
            "user_id",
            "USR-",
            4
    );
}
    
    private static String generateNextId(
            String tableName,
            String idColumn,
            String prefix,
            int numberLength
    ) throws SQLException {

        int numberStartPosition =
                prefix.length() + 1;

        String sql =
                "SELECT COALESCE(" +
                "MAX(CAST(SUBSTRING(" +
                idColumn +
                ", ?) AS UNSIGNED)), 0) + 1 " +
                "AS next_number " +
                "FROM " + tableName + " " +
                "WHERE " + idColumn + " LIKE ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    numberStartPosition
            );

            statement.setString(
                    2,
                    prefix + "%"
            );

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    int nextNumber =
                            result.getInt(
                                    "next_number"
                            );

                    return prefix
                            + String.format(
                                    "%0"
                                    + numberLength
                                    + "d",
                                    nextNumber
                            );
                }
            }
        }

        return prefix
                + String.format(
                        "%0"
                        + numberLength
                        + "d",
                        1
                );
    }
}