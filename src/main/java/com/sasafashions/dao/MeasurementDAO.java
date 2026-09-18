package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Measurement;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Performs database operations involving measurements.
 *
 * This DAO saves, finds, updates, deletes, lists and
 * navigates customer measurement records.
 *
 * @author SASA Group
 * @version 1.0
 */
public class MeasurementDAO {

    /**
     * Generates the next measurement ID.
     *
     * @return ID such as MEAS-0001
     * @throws SQLException when database access fails
     */
    public String generateNextMeasurementId()
            throws SQLException {

        return IdGenerator
                .generateNextMeasurementId();
    }

    /**
     * Saves a new measurement record.
     *
     * @param measurement measurement to save
     * @return true when the record is saved
     * @throws SQLException when database access fails
     */
    public boolean saveMeasurement(
            Measurement measurement
    ) throws SQLException {

        String sql =
                "INSERT INTO measurements (" +
                "measurement_id, customer_id, chest, " +
                "waist, hip, shoulder, sleeve_length, " +
                "trouser_length, date_taken" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    measurement.getMeasurementId()
            );

            statement.setString(
                    2,
                    measurement.getCustomerId()
            );

            setNullableDecimal(
                    statement,
                    3,
                    measurement.getChest()
            );

            setNullableDecimal(
                    statement,
                    4,
                    measurement.getWaist()
            );

            setNullableDecimal(
                    statement,
                    5,
                    measurement.getHip()
            );

            setNullableDecimal(
                    statement,
                    6,
                    measurement.getShoulder()
            );

            setNullableDecimal(
                    statement,
                    7,
                    measurement.getSleeveLength()
            );

            setNullableDecimal(
                    statement,
                    8,
                    measurement.getTrouserLength()
            );

            statement.setDate(
                    9,
                    Date.valueOf(
                            measurement.getDateTaken()
                    )
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Finds a measurement using its measurement ID.
     *
     * @param measurementId ID to search for
     * @return matching Measurement, or null
     * @throws SQLException when database access fails
     */
    public Measurement findMeasurement(
            String measurementId
    ) throws SQLException {

        String sql =
                "SELECT * FROM measurements " +
                "WHERE measurement_id = ?";

        return findUsingSingleParameter(
                sql,
                measurementId
        );
    }

    /**
     * Finds the measurement record belonging to a customer.
     *
     * The current database design permits only one
     * measurement record for each customer.
     *
     * @param customerId customer ID to search for
     * @return customer's Measurement, or null
     * @throws SQLException when database access fails
     */
    public Measurement findByCustomerId(
            String customerId
    ) throws SQLException {

        String sql =
                "SELECT * FROM measurements " +
                "WHERE customer_id = ?";

        return findUsingSingleParameter(
                sql,
                customerId
        );
    }

    /**
     * Updates an existing measurement.
     *
     * @param measurement updated measurement information
     * @return true when the record is updated
     * @throws SQLException when database access fails
     */
    public boolean updateMeasurement(
            Measurement measurement
    ) throws SQLException {

        String sql =
                "UPDATE measurements SET " +
                "customer_id = ?, " +
                "chest = ?, " +
                "waist = ?, " +
                "hip = ?, " +
                "shoulder = ?, " +
                "sleeve_length = ?, " +
                "trouser_length = ?, " +
                "date_taken = ? " +
                "WHERE measurement_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    measurement.getCustomerId()
            );

            setNullableDecimal(
                    statement,
                    2,
                    measurement.getChest()
            );

            setNullableDecimal(
                    statement,
                    3,
                    measurement.getWaist()
            );

            setNullableDecimal(
                    statement,
                    4,
                    measurement.getHip()
            );

            setNullableDecimal(
                    statement,
                    5,
                    measurement.getShoulder()
            );

            setNullableDecimal(
                    statement,
                    6,
                    measurement.getSleeveLength()
            );

            setNullableDecimal(
                    statement,
                    7,
                    measurement.getTrouserLength()
            );

            statement.setDate(
                    8,
                    Date.valueOf(
                            measurement.getDateTaken()
                    )
            );

            statement.setString(
                    9,
                    measurement.getMeasurementId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a measurement record.
     *
     * @param measurementId measurement ID to delete
     * @return true when the record is deleted
     * @throws SQLException when database access fails
     */
    public boolean deleteMeasurement(
            String measurementId
    ) throws SQLException {

        String sql =
                "DELETE FROM measurements " +
                "WHERE measurement_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    measurementId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves every measurement record.
     *
     * @return ArrayList containing measurements
     * @throws SQLException when database access fails
     */
    public ArrayList<Measurement> findAllMeasurements()
            throws SQLException {

        ArrayList<Measurement> measurements =
                new ArrayList<>();

        String sql =
                "SELECT * FROM measurements " +
                "ORDER BY measurement_id ASC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                measurements.add(
                        createMeasurementFromResult(
                                result
                        )
                );
            }
        }

        return measurements;
    }

    /**
     * Retrieves the first measurement.
     *
     * @return first Measurement, or null
     * @throws SQLException when database access fails
     */
    public Measurement getFirstMeasurement()
            throws SQLException {

        String sql =
                "SELECT * FROM measurements " +
                "ORDER BY measurement_id ASC " +
                "LIMIT 1";

        return getSingleMeasurement(sql, null);
    }

    /**
     * Retrieves the last measurement.
     *
     * @return last Measurement, or null
     * @throws SQLException when database access fails
     */
    public Measurement getLastMeasurement()
            throws SQLException {

        String sql =
                "SELECT * FROM measurements " +
                "ORDER BY measurement_id DESC " +
                "LIMIT 1";

        return getSingleMeasurement(sql, null);
    }

    /**
     * Retrieves the next measurement.
     *
     * @param currentMeasurementId currently displayed ID
     * @return next Measurement, or null
     * @throws SQLException when database access fails
     */
    public Measurement getNextMeasurement(
            String currentMeasurementId
    ) throws SQLException {

        String sql =
                "SELECT * FROM measurements " +
                "WHERE measurement_id > ? " +
                "ORDER BY measurement_id ASC " +
                "LIMIT 1";

        return getSingleMeasurement(
                sql,
                currentMeasurementId
        );
    }

    /**
     * Retrieves the previous measurement.
     *
     * @param currentMeasurementId currently displayed ID
     * @return previous Measurement, or null
     * @throws SQLException when database access fails
     */
    public Measurement getPreviousMeasurement(
            String currentMeasurementId
    ) throws SQLException {

        String sql =
                "SELECT * FROM measurements " +
                "WHERE measurement_id < ? " +
                "ORDER BY measurement_id DESC " +
                "LIMIT 1";

        return getSingleMeasurement(
                sql,
                currentMeasurementId
        );
    }

    /**
     * Executes a query containing one required parameter.
     *
     * @param sql query to execute
     * @param value parameter value
     * @return matching Measurement, or null
     * @throws SQLException when database access fails
     */
    private Measurement findUsingSingleParameter(
            String sql,
            String value
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, value);

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return createMeasurementFromResult(
                            result
                    );
                }
            }
        }

        return null;
    }

    /**
     * Shared method used by navigation operations.
     *
     * @param sql query to execute
     * @param measurementId optional measurement ID
     * @return matching Measurement, or null
     * @throws SQLException when database access fails
     */
    private Measurement getSingleMeasurement(
            String sql,
            String measurementId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (measurementId != null) {

                statement.setString(
                        1,
                        measurementId
                );
            }

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return createMeasurementFromResult(
                            result
                    );
                }
            }
        }

        return null;
    }

    /**
     * Places a decimal value in a statement or stores
     * SQL NULL when the measurement was not supplied.
     *
     * @param statement prepared statement
     * @param position parameter position
     * @param value decimal measurement value
     * @throws SQLException when database access fails
     */
    private void setNullableDecimal(
            PreparedStatement statement,
            int position,
            BigDecimal value
    ) throws SQLException {

        if (value == null) {

            statement.setNull(
                    position,
                    Types.DECIMAL
            );

        } else {

            statement.setBigDecimal(
                    position,
                    value
            );
        }
    }

    /**
     * Converts a database record into a Measurement.
     *
     * @param result measurement database record
     * @return constructed Measurement
     * @throws SQLException when a column cannot be read
     */
    private Measurement createMeasurementFromResult(
            ResultSet result
    ) throws SQLException {

        return new Measurement(
                result.getString("measurement_id"),
                result.getString("customer_id"),
                result.getBigDecimal("chest"),
                result.getBigDecimal("waist"),
                result.getBigDecimal("hip"),
                result.getBigDecimal("shoulder"),
                result.getBigDecimal("sleeve_length"),
                result.getBigDecimal("trouser_length"),
                result.getDate(
                        "date_taken"
                ).toLocalDate()
        );
    }
}