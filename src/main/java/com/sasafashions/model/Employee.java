package com.sasafashions.model;

/**
 * Represents an employee working at Sasa Fashions.
 *
 * An Employee object corresponds to one record in
 * the employees database table.
 *
 * @author SASA Group
 * @version 1.0
 */
public class Employee {

    private String employeeId;
    private String employeeName;
    private String telephone;
    private String role;

    /**
     * Creates an empty Employee object.
     */
    public Employee() {
    }

    /**
     * Creates an Employee containing all fields.
     *
     * @param employeeId unique employee ID
     * @param employeeName employee's full name
     * @param telephone employee's telephone number
     * @param role employee's business role
     */
    public Employee(
            String employeeId,
            String employeeName,
            String telephone,
            String role
    ) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.telephone = telephone;
        this.role = role;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(
            String employeeId
    ) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(
            String employeeName
    ) {
        this.employeeName = employeeName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(
            String telephone
    ) {
        this.telephone = telephone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(
            String role
    ) {
        this.role = role;
    }

    /**
     * Returns a readable employee description.
     *
     * This is useful when an Employee object is placed
     * inside a JComboBox.
     *
     * @return employee ID and employee name
     */
    @Override
    public String toString() {

        return employeeId
                + " - "
                + employeeName;
    }
}