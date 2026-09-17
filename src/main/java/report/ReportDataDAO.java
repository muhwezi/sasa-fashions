package com.sasafashions.report;

import com.sasafashions.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Retrieves database information used by the Sasa Fashions reports.
 *
 * <p>Each row is stored in a LinkedHashMap so that the report
 * column order remains the same as the SQL query order. All rows
 * are stored inside an ArrayList.</p>
 *
 * @author Joshua Muhwezi
 */
public class ReportDataDAO {

    /**
     * Retrieves registered customer information.
     *
     * @return customer report rows
     * @throws SQLException when the database query fails
     */
    public List<LinkedHashMap<String, Object>>
            getCustomerReportData() throws SQLException {

        String sql = """
                SELECT
                    customer_id AS 'Customer ID',
                    customer_name AS 'Customer Name',
                    telephone AS 'Telephone',
                    gender AS 'Gender',
                    address AS 'Address',
                    registration_date AS 'Registration Date',
                    customer_status AS 'Status'
                FROM customers
                ORDER BY customer_name
                """;

        return executeReportQuery(sql);
    }

    /**
     * Retrieves orders together with customer and employee names.
     *
     * @return order report rows
     * @throws SQLException when the database query fails
     */
    public List<LinkedHashMap<String, Object>>
            getOrderReportData() throws SQLException {

        String sql = """
                SELECT
                    o.order_id AS 'Order ID',
                    c.customer_name AS 'Customer',
                    e.employee_name AS 'Handled By',
                    o.order_date AS 'Order Date',
                    o.due_date AS 'Due Date',
                    o.order_status AS 'Status',
                    o.total_amount AS 'Total Amount'
                FROM orders o
                INNER JOIN customers c
                    ON o.customer_id = c.customer_id
                INNER JOIN employees e
                    ON o.employee_id = e.employee_id
                ORDER BY o.order_date DESC, o.order_id
                """;

        return executeReportQuery(sql);
    }

    /**
     * Retrieves payment information, including the order,
     * customer and employee who received the payment.
     *
     * @return payment report rows
     * @throws SQLException when the database query fails
     */
    public List<LinkedHashMap<String, Object>>
            getPaymentReportData() throws SQLException {

        String sql = """
                SELECT
                    p.payment_id AS 'Payment ID',
                    p.order_id AS 'Order ID',
                    c.customer_name AS 'Customer',
                    p.payment_date AS 'Payment Date',
                    p.amount AS 'Amount',
                    p.payment_method AS 'Method',
                    COALESCE(
                        p.reference_number,
                        ''
                    ) AS 'Reference',
                    COALESCE(
                        e.employee_name,
                        ''
                    ) AS 'Received By'
                FROM payments p
                INNER JOIN orders o
                    ON p.order_id = o.order_id
                INNER JOIN customers c
                    ON o.customer_id = c.customer_id
                LEFT JOIN employees e
                    ON p.received_by = e.employee_id
                ORDER BY p.payment_date DESC, p.payment_id
                """;

        return executeReportQuery(sql);
    }

    /**
     * Retrieves material stock information.
     *
     * @return material stock report rows
     * @throws SQLException when the database query fails
     */
    public List<LinkedHashMap<String, Object>>
            getMaterialStockReportData() throws SQLException {

        String sql = """
                SELECT
                    material_id AS 'Material ID',
                    name AS 'Material',
                    unit_of_measure AS 'Unit',
                    quantity_in_stock AS 'Quantity',
                    reorder_level AS 'Reorder Level',
                    unit_cost AS 'Unit Cost',
                    material_status AS 'Status',
                    CASE
                        WHEN quantity_in_stock <= reorder_level
                            THEN 'REORDER'
                        ELSE 'SUFFICIENT'
                    END AS 'Stock Condition'
                FROM materials
                ORDER BY name
                """;

        return executeReportQuery(sql);
    }

    /**
     * Retrieves purchases together with supplier and employee names.
     *
     * @return purchase report rows
     * @throws SQLException when the database query fails
     */
    public List<LinkedHashMap<String, Object>>
            getPurchaseReportData() throws SQLException {

        String sql = """
                SELECT
                    p.purchase_id AS 'Purchase ID',
                    s.supplier_name AS 'Supplier',
                    e.employee_name AS 'Received By',
                    p.purchase_date AS 'Purchase Date',
                    COALESCE(
                        p.invoice_number,
                        ''
                    ) AS 'Invoice Number',
                    p.purchase_status AS 'Status',
                    p.total_amount AS 'Total Amount'
                FROM purchases p
                INNER JOIN suppliers s
                    ON p.supplier_id = s.supplier_id
                INNER JOIN employees e
                    ON p.employee_id = e.employee_id
                ORDER BY p.purchase_date DESC, p.purchase_id
                """;

        return executeReportQuery(sql);
    }

    /**
     * Executes a report query and converts the ResultSet into
     * an ArrayList containing LinkedHashMap rows.
     *
     * @param sql SQL report query
     * @return report rows
     * @throws SQLException when execution fails
     */
    private List<LinkedHashMap<String, Object>>
            executeReportQuery(String sql) throws SQLException {

        List<LinkedHashMap<String, Object>> rows =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            ResultSetMetaData metadata =
                    resultSet.getMetaData();

            int columnCount =
                    metadata.getColumnCount();

            while (resultSet.next()) {

                LinkedHashMap<String, Object> row =
                        new LinkedHashMap<>();

                for (
                        int column = 1;
                        column <= columnCount;
                        column++
                ) {

                    String heading =
                            metadata.getColumnLabel(column);

                    Object value =
                            resultSet.getObject(column);

                    row.put(
                            heading,
                            value == null ? "" : value
                    );
                }

                rows.add(row);
            }
        }

        return rows;
    }
}