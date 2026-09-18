package com.sasafashions.view;

import com.sasafashions.dao.EmployeeDAO;
import com.sasafashions.model.Employee;
import com.sasafashions.model.shared.ContactDetails;
import com.sasafashions.view.components.FormStyler;
import com.sasafashions.view.components.MessageDialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Provides the user interface for managing employees.
 *
 * The form supports saving, finding, editing, deleting,
 * navigating and displaying employee records.
 *
 * @author SASA Group
 * @version 1.0
 */
public class EmployeeForm extends JFrame {

    private final EmployeeDAO employeeDAO =
            new EmployeeDAO();

    /*
     * ArrayList is the first major data structure used
     * in the Sasa Fashions application.
     */
    private ArrayList<Employee> employeeList =
            new ArrayList<>();

    private final JTextField txtEmployeeId =
            new JTextField(20);

    private final JTextField txtEmployeeName =
            new JTextField(20);

    private final JTextField txtTelephone =
            new JTextField(20);

    private final JComboBox<String> cmbRole =
            new JComboBox<>(
                    new String[]{
                        "Manager",
                        "Tailor",
                        "Cashier",
                        "Designer",
                        "Receptionist",
                        "Storekeeper"
                    }
            );

    private final JButton btnNew =
            new JButton("NEW");

    private final JButton btnSave =
            new JButton("SAVE");

    private final JButton btnFind =
            new JButton("FIND");

    private final JButton btnEdit =
            new JButton("EDIT");

    private final JButton btnDelete =
            new JButton("DELETE");

    private final JButton btnFirst =
            new JButton("FIRST");

    private final JButton btnPrevious =
            new JButton("PREVIOUS");

    private final JButton btnNext =
            new JButton("NEXT");

    private final JButton btnLast =
            new JButton("LAST");

    private final JButton btnClose =
            new JButton("CLOSE");

    /**
     * Table model controls the columns and rows shown
     * in the employee JTable.
     */
    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                        "Employee ID",
                        "Employee Name",
                        "Telephone",
                        "Role"
                    },
                    0
            ) {

                /**
                 * Prevents users from directly editing
                 * cells inside the JTable.
                 *
                 * @param row table row
                 * @param column table column
                 * @return false because cells are read-only
                 */
                @Override
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

    private final JTable tblEmployees =
            new JTable(tableModel);

    /**
     * Creates and prepares the Employee Form.
     */
    public EmployeeForm() {

        initialiseForm();
        attachEvents();

        FormStyler.prepareFrame(this);

        FormStyler.styleButtons(
                btnNew,
                btnSave,
                btnFind,
                btnEdit,
                btnDelete,
                btnFirst,
                btnPrevious,
                btnNext,
                btnLast,
                btnClose
        );

        prepareNewEmployee();
        loadEmployeeTable();
    }

    /**
     * Creates and arranges the Swing components.
     */
    private void initialiseForm() {

        setTitle(
                "Sasa Fashions - Employee Management"
        );

        setSize(950, 600);

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                new EmptyBorder(15, 20, 15, 20)
        );

        JLabel lblHeading =
                new JLabel("SASA FASHIONS");

        lblHeading.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        JLabel lblSubheading =
                new JLabel("EMPLOYEE MANAGEMENT");

        lblSubheading.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        JPanel headingPanel =
                new JPanel(new BorderLayout());

        headingPanel.add(
                lblHeading,
                BorderLayout.NORTH
        );

        headingPanel.add(
                lblSubheading,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                headingPanel,
                BorderLayout.NORTH
        );

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(6, 6, 6, 6);

        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        addFormField(
                formPanel,
                constraints,
                0,
                "Employee ID",
                txtEmployeeId
        );

        addFormField(
                formPanel,
                constraints,
                1,
                "Employee Name",
                txtEmployeeName
        );

        addFormField(
                formPanel,
                constraints,
                2,
                "Telephone",
                txtTelephone
        );

        addFormField(
                formPanel,
                constraints,
                3,
                "Role",
                cmbRole
        );

        txtEmployeeId.setEditable(false);

        JPanel actionButtons =
                new JPanel(new FlowLayout(
                        FlowLayout.CENTER
                ));

        actionButtons.add(btnNew);
        actionButtons.add(btnSave);
        actionButtons.add(btnFind);
        actionButtons.add(btnEdit);
        actionButtons.add(btnDelete);

        JPanel navigationButtons =
                new JPanel(new FlowLayout(
                        FlowLayout.CENTER
                ));

        navigationButtons.add(btnFirst);
        navigationButtons.add(btnPrevious);
        navigationButtons.add(btnNext);
        navigationButtons.add(btnLast);
        navigationButtons.add(btnClose);

        JPanel formSection =
                new JPanel(new BorderLayout());

        formSection.add(
                formPanel,
                BorderLayout.CENTER
        );

        JPanel buttonSection =
                new JPanel(new BorderLayout());

        buttonSection.add(
                actionButtons,
                BorderLayout.NORTH
        );

        buttonSection.add(
                navigationButtons,
                BorderLayout.SOUTH
        );

        formSection.add(
                buttonSection,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                formSection,
                BorderLayout.WEST
        );

        tblEmployees.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tblEmployees.setRowHeight(24);

        JScrollPane tableScrollPane =
                new JScrollPane(tblEmployees);

        mainPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        setContentPane(mainPanel);
    }

    /**
     * Adds one label and input component to the form.
     *
     * @param panel form panel
     * @param constraints layout constraints
     * @param row row position
     * @param labelText field label
     * @param component input component
     */
    private void addFormField(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String labelText,
            java.awt.Component component
    ) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;

        panel.add(
                new JLabel(labelText),
                constraints
        );

        constraints.gridx = 1;
        constraints.weightx = 1;

        panel.add(
                component,
                constraints
        );
    }

    /**
     * Connects buttons and the JTable to their actions.
     */
    private void attachEvents() {

        btnNew.addActionListener(
                event -> prepareNewEmployee()
        );

        btnSave.addActionListener(
                event -> saveEmployee()
        );

        btnFind.addActionListener(
                event -> findEmployee()
        );

        btnEdit.addActionListener(
                event -> editEmployee()
        );

        btnDelete.addActionListener(
                event -> deleteEmployee()
        );

        btnFirst.addActionListener(
                event -> showFirstEmployee()
        );

        btnPrevious.addActionListener(
                event -> showPreviousEmployee()
        );

        btnNext.addActionListener(
                event -> showNextEmployee()
        );

        btnLast.addActionListener(
                event -> showLastEmployee()
        );

        btnClose.addActionListener(
                event -> closeForm()
        );

        tblEmployees
                .getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        displaySelectedTableEmployee();
                    }
                });
    }

    /**
     * Clears the form and generates a new employee ID.
     */
    private void prepareNewEmployee() {

        txtEmployeeName.setText("");
        txtTelephone.setText("");
        cmbRole.setSelectedIndex(0);

        try {

            String employeeId =
                    employeeDAO.generateNextEmployeeId();

            txtEmployeeId.setText(employeeId);

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to generate employee ID.",
                    error
            );

            txtEmployeeId.setText("");
        }

        tblEmployees.clearSelection();
        txtEmployeeName.requestFocus();
    }

    /**
     * Validates the form and constructs an Employee.
     *
     * @return Employee, or null when validation fails
     */
    private Employee readEmployeeFromForm() {

        String employeeId =
                txtEmployeeId.getText().trim();

        String employeeName =
                txtEmployeeName.getText().trim();

        String telephone =
                txtTelephone.getText().trim();

        String role =
                cmbRole.getSelectedItem().toString();

        if (employeeId.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "An employee ID has not been generated.",
                    "Missing Employee ID"
            );

            return null;
        }

        if (employeeName.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Please enter the employee name.",
                    "Missing Employee Name"
            );

            txtEmployeeName.requestFocus();
            return null;
        }

        if (!ContactDetails
                .isValidUgandanTelephone(telephone)) {

            MessageDialog.showWarning(
                    this,
                    "Enter a valid telephone number.\n"
                            + "Example: 0772000000 "
                            + "or +256772000000",
                    "Invalid Telephone"
            );

            txtTelephone.requestFocus();
            return null;
        }

        telephone =
                ContactDetails.cleanTelephone(
                        telephone
                );

        return new Employee(
                employeeId,
                employeeName,
                telephone,
                role
        );
    }

    /**
     * Saves a new employee.
     */
    private void saveEmployee() {

        Employee employee =
                readEmployeeFromForm();

        if (employee == null) {
            return;
        }

        try {

            boolean saved =
                    employeeDAO.saveEmployee(employee);

            if (saved) {

                MessageDialog.showInformation(
                        this,
                        "Employee saved successfully.",
                        "Success"
                );

                loadEmployeeTable();
                prepareNewEmployee();
            }

        } catch (SQLException error) {

            if (error.getErrorCode() == 1062) {

                MessageDialog.showWarning(
                        this,
                        "The employee ID already exists.",
                        "Duplicate Employee"
                );

            } else {

                showDatabaseError(
                        "Unable to save the employee.",
                        error
                );
            }
        }
    }

    /**
     * Finds an employee using an entered ID.
     */
    private void findEmployee() {

        String employeeId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the employee ID:",
                        "Find Employee",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (employeeId == null) {
            return;
        }

        employeeId =
                employeeId.trim().toUpperCase();

        if (employeeId.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Please enter an employee ID.",
                    "Missing Employee ID"
            );

            return;
        }

        try {

            Employee employee =
                    employeeDAO.findEmployee(
                            employeeId
                    );

            if (employee != null) {

                displayEmployee(employee);

            } else {

                MessageDialog.showInformation(
                        this,
                        "No employee was found with ID: "
                                + employeeId,
                        "Employee Not Found"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to find the employee.",
                    error
            );
        }
    }

    /**
     * Updates the currently displayed employee.
     */
    private void editEmployee() {

        Employee employee =
                readEmployeeFromForm();

        if (employee == null) {
            return;
        }

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Save changes to employee "
                                + employee.getEmployeeId()
                                + "?",
                        "Confirm Edit"
                );

        if (!confirmed) {
            return;
        }

        try {

            boolean updated =
                    employeeDAO.updateEmployee(
                            employee
                    );

            if (updated) {

                MessageDialog.showInformation(
                        this,
                        "Employee updated successfully.",
                        "Success"
                );

                loadEmployeeTable();

            } else {

                MessageDialog.showWarning(
                        this,
                        "The employee was not found.",
                        "Update Failed"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to update the employee.",
                    error
            );
        }
    }

    /**
     * Deletes the currently displayed employee.
     */
    private void deleteEmployee() {

        String employeeId =
                txtEmployeeId.getText().trim();

        String employeeName =
                txtEmployeeName.getText().trim();

        if (employeeId.isEmpty()
                || employeeName.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Find or select an employee "
                            + "before deleting.",
                    "No Employee Selected"
            );

            return;
        }

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Delete this employee?\n\n"
                                + employeeId
                                + " - "
                                + employeeName,
                        "Confirm Deletion"
                );

        if (!confirmed) {
            return;
        }

        try {

            boolean deleted =
                    employeeDAO.deleteEmployee(
                            employeeId
                    );

            if (deleted) {

                MessageDialog.showInformation(
                        this,
                        "Employee deleted successfully.",
                        "Success"
                );

                loadEmployeeTable();
                prepareNewEmployee();

            } else {

                MessageDialog.showWarning(
                        this,
                        "The employee was not found.",
                        "Delete Failed"
                );
            }

        } catch (SQLException error) {

            if (error.getErrorCode() == 1451) {

                MessageDialog.showWarning(
                        this,
                        "This employee cannot be deleted "
                                + "because related records exist.",
                        "Employee In Use"
                );

            } else {

                showDatabaseError(
                        "Unable to delete the employee.",
                        error
                );
            }
        }
    }

    /**
     * Loads all employees into the ArrayList and JTable.
     */
    private void loadEmployeeTable() {

        try {

            employeeList =
                    employeeDAO.findAllEmployees();

            tableModel.setRowCount(0);

            for (Employee employee : employeeList) {

                tableModel.addRow(
                        new Object[]{
                            employee.getEmployeeId(),
                            employee.getEmployeeName(),
                            employee.getTelephone(),
                            employee.getRole()
                        }
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to load employee records.",
                    error
            );
        }
    }

    /**
     * Displays the employee selected in the JTable.
     */
    private void displaySelectedTableEmployee() {

        int selectedRow =
                tblEmployees.getSelectedRow();

        if (selectedRow < 0
                || selectedRow >= employeeList.size()) {
            return;
        }

        Employee employee =
                employeeList.get(selectedRow);

        displayEmployee(employee);
    }

    /**
     * Displays an Employee object on the form.
     *
     * @param employee employee to display
     */
    private void displayEmployee(Employee employee) {

        txtEmployeeId.setText(
                employee.getEmployeeId()
        );

        txtEmployeeName.setText(
                employee.getEmployeeName()
        );

        txtTelephone.setText(
                employee.getTelephone()
        );

        cmbRole.setSelectedItem(
                employee.getRole()
        );
    }

    /**
     * Displays the first employee.
     */
    private void showFirstEmployee() {

        try {

            Employee employee =
                    employeeDAO.getFirstEmployee();

            displayNavigationResult(
                    employee,
                    "There are no employees to display."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate employee records.",
                    error
            );
        }
    }

    /**
     * Displays the last employee.
     */
    private void showLastEmployee() {

        try {

            Employee employee =
                    employeeDAO.getLastEmployee();

            displayNavigationResult(
                    employee,
                    "There are no employees to display."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate employee records.",
                    error
            );
        }
    }

    /**
     * Displays the next employee.
     */
    private void showNextEmployee() {

        String currentId =
                txtEmployeeId.getText().trim();

        if (currentId.isEmpty()) {
            showFirstEmployee();
            return;
        }

        try {

            Employee employee =
                    employeeDAO.getNextEmployee(
                            currentId
                    );

            displayNavigationResult(
                    employee,
                    "You have reached the last employee."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate employee records.",
                    error
            );
        }
    }

    /**
     * Displays the previous employee.
     */
    private void showPreviousEmployee() {

        String currentId =
                txtEmployeeId.getText().trim();

        if (currentId.isEmpty()) {
            showLastEmployee();
            return;
        }

        try {

            Employee employee =
                    employeeDAO.getPreviousEmployee(
                            currentId
                    );

            displayNavigationResult(
                    employee,
                    "You have reached the first employee."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate employee records.",
                    error
            );
        }
    }

    /**
     * Displays a navigation result or an information message.
     *
     * @param employee employee returned by the DAO
     * @param emptyMessage message when no employee is returned
     */
    private void displayNavigationResult(
            Employee employee,
            String emptyMessage
    ) {

        if (employee != null) {

            displayEmployee(employee);

        } else {

            MessageDialog.showInformation(
                    this,
                    emptyMessage,
                    "Employee Navigation"
            );
        }
    }

    /**
     * Displays a standard database error.
     *
     * @param message user-friendly error message
     * @param error SQL error
     */
    private void showDatabaseError(
            String message,
            SQLException error
    ) {

        MessageDialog.showError(
                this,
                message + "\n" + error.getMessage(),
                "Database Error"
        );
    }

    /**
     * Requests confirmation before closing the form.
     */
    private void closeForm() {

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Do you want to close "
                                + "Employee Management?",
                        "Confirm Close"
                );

        if (confirmed) {
            dispose();
        }
    }

    /**
     * Starts the Employee Form.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(
                () -> new EmployeeForm()
                        .setVisible(true)
        );
    }
}