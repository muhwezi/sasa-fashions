package com.sasafashions.dao;

import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Provides database-reading operations for security roles.
 *
 * <p>Roles are configuration records and are normally
 * maintained directly during system setup.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class RoleDAO {

    /**
     * Finds a role using its identifier.
     *
     * @param roleId role identifier
     * @return matching role, or null
     * @throws SQLException when database access fails
     */
    public Role findRole(
            String roleId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM roles
                WHERE role_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, roleId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return createRole(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Finds a role using its name.
     *
     * @param roleName role name
     * @return matching role, or null
     * @throws SQLException when database access fails
     */
    public Role findRoleByName(
            String roleName
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM roles
                WHERE role_name = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, roleName);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return createRole(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Returns all system roles.
     *
     * @return list containing all roles
     * @throws SQLException when database access fails
     */
    public ArrayList<Role> findAllRoles()
            throws SQLException {

        ArrayList<Role> roles =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM roles
                ORDER BY role_name
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
                roles.add(createRole(resultSet));
            }
        }

        return roles;
    }

    /**
     * Converts a result row into a Role object.
     *
     * @param resultSet role database result
     * @return created role
     * @throws SQLException when values cannot be read
     */
    private Role createRole(
            ResultSet resultSet
    ) throws SQLException {

        return new Role(
                resultSet.getString("role_id"),
                resultSet.getString("role_name"),
                resultSet.getString("description")
        );
    }
}