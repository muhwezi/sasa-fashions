



package com.sasafashions.dao;

import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;




/**
 * Records and retrieves important system actions.
 *
 * <p>Audit entries assist administrators in determining
 * who performed an action, the affected record and when
 * the action occurred.</p>
 *
 *@author SASA Group
 * @version 1.0
 * 
 * 
 * 
 * 
 */





public class AuditLogDAO {

    /**
     * Records an action in the audit log.
     *
     * @param userId user who performed the action
     * @param actionType type of action
     * @param tableName affected database table
     * @param recordId affected record identifier
     * @param description action description
     * @throws SQLException when the log cannot be saved
     */
    public void logAction(
            String userId,
            String actionType,
            String tableName,
            String recordId,
            String description
    ) throws SQLException {

        String sql = """
                INSERT INTO audit_logs (
                    user_id,
                    action_type,
                    table_name,
                    record_id,
                    description
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            setNullableString(
                    statement,
                    1,
                    userId
            );

            statement.setString(
                    2,
                    actionType
            );

            statement.setString(
                    3,
                    tableName
            );

            setNullableString(
                    statement,
                    4,
                    recordId
            );

            setNullableString(
                    statement,
                    5,
                    description
            );

            statement.executeUpdate();
        }
    }

    /**
     * Returns all audit entries, newest first.
     *
     * @return complete audit-log list
     * @throws SQLException when database access fails
     */
    public ArrayList<AuditLog> findAllLogs()
            throws SQLException {

        ArrayList<AuditLog> logs =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM audit_logs
                ORDER BY action_time DESC,
                         audit_id DESC
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

                logs.add(
                        createAuditLog(resultSet)
                );
            }
        }

        return logs;
    }

    /**
     * Returns audit entries recorded for one user.
     *
     * @param userId user identifier
     * @return user audit-log list
     * @throws SQLException when database access fails
     */
    public ArrayList<AuditLog> findLogsByUser(
            String userId
    ) throws SQLException {

        ArrayList<AuditLog> logs =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM audit_logs
                WHERE user_id = ?
                ORDER BY action_time DESC,
                         audit_id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, userId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    logs.add(
                            createAuditLog(resultSet)
                    );
                }
            }
        }

        return logs;
    }

    /**
     * Returns logs recorded between two date-time values.
     *
     * @param fromDateTime beginning date and time
     * @param toDateTime ending date and time
     * @return matching audit entries
     * @throws SQLException when database access fails
     */
    public ArrayList<AuditLog> findLogsByPeriod(
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    ) throws SQLException {

        ArrayList<AuditLog> logs =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM audit_logs
                WHERE action_time BETWEEN ? AND ?
                ORDER BY action_time DESC,
                         audit_id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(fromDateTime)
            );

            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(toDateTime)
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    logs.add(
                            createAuditLog(resultSet)
                    );
                }
            }
        }

        return logs;
    }

    /**
     * Converts a result row into an AuditLog object.
     *
     * @param resultSet audit database result
     * @return created audit-log object
     * @throws SQLException when values cannot be read
     */
    private AuditLog createAuditLog(
            ResultSet resultSet
    ) throws SQLException {

        Timestamp timestamp =
                resultSet.getTimestamp(
                        "action_time"
                );

        return new AuditLog(
                resultSet.getLong("audit_id"),
                resultSet.getString("user_id"),
                resultSet.getString("action_type"),
                resultSet.getString("table_name"),
                resultSet.getString("record_id"),
                resultSet.getString("description"),
                timestamp == null
                        ? null
                        : timestamp.toLocalDateTime()
        );
    }

    /**
     * Assigns a string or SQL NULL to a statement.
     *
     * @param statement prepared statement
     * @param parameterNumber parameter position
     * @param value string value
     * @throws SQLException when assignment fails
     */
    private void setNullableString(
            PreparedStatement statement,
            int parameterNumber,
            String value
    ) throws SQLException {

        if (value == null
                || value.trim().isEmpty()) {

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
    
    
    
}


