package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Performs database operations involving employees.
 *
 * This DAO can save, find, update, delete, list and
 * navigate employee records.
 *
 * @author HP
 * @version 1.0
 */
public class EmployeeDAO {

    /**
     * Generates the next employee ID.
     *
     * @return an ID such as EMP-0001
     * @throws SQLException when database access fails
     */
    public String generateNextEmployeeId()
            throws SQLException {

        return IdGenerator.generateNextEmployeeId();
    }

    /**
     * Saves a new employee.
     *
     * @param employee employee to save
     * @return true when the employee is saved
     * @throws SQLException when database access fails
     */
    public boolean saveEmployee(Employee employee)
            throws SQLException {

        String sql =
                "INSERT INTO employees (" +
                "employee_id, employee_name, " +
                "telephone, employee_role" +
                ") VALUES (?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    employee.getEmployeeId()
            );

            statement.setString(
                    2,
                    employee.getEmployeeName()
            );

            statement.setString(
                    3,
                    employee.getTelephone()
            );

            statement.setString(
                    4,
                    employee.getRole()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Finds an employee using the employee ID.
     *
     * @param employeeId employee ID to search for
     * @return matching Employee, or null when not found
     * @throws SQLException when database access fails
     */
    public Employee findEmployee(String employeeId)
            throws SQLException {

        String sql =
                "SELECT * FROM employees " +
                "WHERE employee_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, employeeId);

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return createEmployeeFromResult(
                            result
                    );
                }
            }
        }

        return null;
    }

    /**
     * Updates an existing employee.
     *
     * @param employee employee containing updated information
     * @return true when the record is updated
     * @throws SQLException when database access fails
     */
    public boolean updateEmployee(Employee employee)
            throws SQLException {

        String sql =
                "UPDATE employees SET " +
                "employee_name = ?, " +
                "telephone = ?, " +
                "employee_role = ? " +
                "WHERE employee_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    employee.getEmployeeName()
            );

            statement.setString(
                    2,
                    employee.getTelephone()
            );

            statement.setString(
                    3,
                    employee.getRole()
            );

            statement.setString(
                    4,
                    employee.getEmployeeId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an employee.
     *
     * MySQL will prevent deletion when the employee is
     * referenced by orders, payments, purchases or users.
     *
     * @param employeeId employee ID to delete
     * @return true when the employee is deleted
     * @throws SQLException when database access fails
     */
    public boolean deleteEmployee(String employeeId)
            throws SQLException {

        String sql =
                "DELETE FROM employees " +
                "WHERE employee_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, employeeId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves every employee from the database.
     *
     * ArrayList is used because it can dynamically grow
     * as employee records are retrieved.
     *
     * @return ArrayList containing employees
     * @throws SQLException when database access fails
     */
    public ArrayList<Employee> findAllEmployees()
            throws SQLException {

        ArrayList<Employee> employees =
                new ArrayList<>();

        String sql =
                "SELECT * FROM employees " +
                "ORDER BY employee_id ASC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                Employee employee =
                        createEmployeeFromResult(result);

                employees.add(employee);
            }
        }

        return employees;
    }

    /**
     * Retrieves the first employee.
     *
     * @return first Employee, or null when none exist
     * @throws SQLException when database access fails
     */
    public Employee getFirstEmployee()
            throws SQLException {

        String sql =
                "SELECT * FROM employees " +
                "ORDER BY employee_id ASC " +
                "LIMIT 1";

        return getSingleEmployee(sql, null);
    }

    /**
     * Retrieves the last employee.
     *
     * @return last Employee, or null when none exist
     * @throws SQLException when database access fails
     */
    public Employee getLastEmployee()
            throws SQLException {

        String sql =
                "SELECT * FROM employees " +
                "ORDER BY employee_id DESC " +
                "LIMIT 1";

        return getSingleEmployee(sql, null);
    }

    /**
     * Retrieves the employee immediately after the
     * currently displayed employee.
     *
     * @param currentEmployeeId currently displayed ID
     * @return next Employee, or null at the end
     * @throws SQLException when database access fails
     */
    public Employee getNextEmployee(
            String currentEmployeeId
    ) throws SQLException {

        String sql =
                "SELECT * FROM employees " +
                "WHERE employee_id > ? " +
                "ORDER BY employee_id ASC " +
                "LIMIT 1";

        return getSingleEmployee(
                sql,
                currentEmployeeId
        );
    }

    /**
     * Retrieves the employee immediately before the
     * currently displayed employee.
     *
     * @param currentEmployeeId currently displayed ID
     * @return previous Employee, or null at the beginning
     * @throws SQLException when database access fails
     */
    public Employee getPreviousEmployee(
            String currentEmployeeId
    ) throws SQLException {

        String sql =
                "SELECT * FROM employees " +
                "WHERE employee_id < ? " +
                "ORDER BY employee_id DESC " +
                "LIMIT 1";

        return getSingleEmployee(
                sql,
                currentEmployeeId
        );
    }

    /**
     * Shared method used by navigation operations.
     *
     * @param sql query to execute
     * @param employeeId optional employee ID
     * @return matching Employee, or null
     * @throws SQLException when database access fails
     */
    private Employee getSingleEmployee(
            String sql,
            String employeeId
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (employeeId != null) {

                statement.setString(
                        1,
                        employeeId
                );
            }

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return createEmployeeFromResult(
                            result
                    );
                }
            }
        }

        return null;
    }

    /**
     * Converts a database record into an Employee object.
     *
     * @param result employee database record
     * @return constructed Employee
     * @throws SQLException when a column cannot be read
     */
    private Employee createEmployeeFromResult(
            ResultSet result
    ) throws SQLException {

        return new Employee(
                result.getString("employee_id"),
                result.getString("employee_name"),
                result.getString("telephone"),
                result.getString("employee_role")
        );
    }
}