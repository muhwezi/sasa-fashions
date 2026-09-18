package com.sasafashions.view;

import com.sasafashions.dao.UserDAO;
import com.sasafashions.model.User;
import com.sasafashions.security.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides administrator functions for managing system users.
 *
 * <p>The form supports saving, finding, editing, deleting,
 * password resetting, account unlocking and record navigation.</p>
 *
 * @author SASA Group
 */
public class UserForm extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Maps role names to database role IDs.
     */
    private final Map<String, String> roles =
            new LinkedHashMap<>();

    /**
     * Stores users used by the navigation buttons.
     */
    private List<User> userRecords =
            new ArrayList<>();

    private int currentIndex = -1;

    private final JTextField txtUserId =
            new JTextField(20);

    private final JTextField txtEmployeeId =
            new JTextField(20);

    private final JTextField txtUsername =
            new JTextField(20);

    private final JPasswordField txtPassword =
            new JPasswordField(20);

    private final JPasswordField txtConfirmPassword =
            new JPasswordField(20);

    private final JComboBox<String> cmbRole =
            new JComboBox<>();

    private final JComboBox<String> cmbStatus =
            new JComboBox<>(
                    new String[]{
                        "Active",
                        "Inactive"
                    }
            );

    private final JCheckBox chkMustChangePassword =
            new JCheckBox(
                    "Require password change at next login"
            );

    private final JButton btnNew =
            new JButton("New");

    private final JButton btnSave =
            new JButton("Save");

    private final JButton btnFind =
            new JButton("Find");

    private final JButton btnEdit =
            new JButton("Edit");

    private final JButton btnDelete =
            new JButton("Delete");

    private final JButton btnResetPassword =
            new JButton("Reset Password");

    private final JButton btnUnlock =
            new JButton("Unlock");

    private final JButton btnFirst =
            new JButton("First");

    private final JButton btnPrevious =
            new JButton("Previous");

    private final JButton btnNext =
            new JButton("Next");

    private final JButton btnLast =
            new JButton("Last");

    private final JButton btnClose =
            new JButton("Close");

    /**
     * Creates the user-management form.
     */
    public UserForm() {

        if (!SessionManager.isAdministrator()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Only an administrator can manage users.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
            );

            dispose();
            return;
        }

        setTitle("Sasa Fashions - User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        prepareRoles();
        createInterface();
        registerEvents();
        prepareNewUser();
    }

    /**
     * Adds the available system roles.
     */
private void prepareRoles() {

    roles.clear();
    cmbRole.removeAllItems();

    roles.put("Administrator", "ROLE-001");
    roles.put("Manager", "ROLE-002");
    roles.put("Cashier", "ROLE-003");
    roles.put("Tailor", "ROLE-004");
    roles.put("Staff", "ROLE-005");

    for (String roleName : roles.keySet()) {
        cmbRole.addItem(roleName);
    }
}

    /**
     * Creates the visible interface.
     */
    private void createInterface() {

        setLayout(new BorderLayout());

        JPanel titlePanel =
                new JPanel(new BorderLayout());

        titlePanel.setBackground(
                new Color(74, 45, 125)
        );

        titlePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        25,
                        18,
                        25
                )
        );

        JLabel lblTitle = new JLabel(
                "SASA FASHIONS - USER MANAGEMENT"
        );

        lblTitle.setForeground(Color.WHITE);

        lblTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        21
                )
        );

        JLabel lblAdministrator = new JLabel(
                "Administrator: "
                        + SessionManager.getCurrentUsername()
        );

        lblAdministrator.setForeground(Color.WHITE);

        titlePanel.add(
                lblTitle,
                BorderLayout.WEST
        );

        titlePanel.add(
                lblAdministrator,
                BorderLayout.EAST
        );

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        40,
                        20,
                        40
                )
        );

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(8, 8, 8, 8);

        constraints.anchor =
                GridBagConstraints.WEST;

        addFormRow(
                formPanel,
                constraints,
                0,
                "User ID",
                txtUserId
        );

        addFormRow(
                formPanel,
                constraints,
                1,
                "Employee ID",
                txtEmployeeId
        );

        addFormRow(
                formPanel,
                constraints,
                2,
                "Username",
                txtUsername
        );

        addFormRow(
                formPanel,
                constraints,
                3,
                "Role",
                cmbRole
        );

        addFormRow(
                formPanel,
                constraints,
                4,
                "Account Status",
                cmbStatus
        );

        addFormRow(
                formPanel,
                constraints,
                5,
                "Temporary Password",
                txtPassword
        );

        addFormRow(
                formPanel,
                constraints,
                6,
                "Confirm Password",
                txtConfirmPassword
        );

        constraints.gridx = 1;
        constraints.gridy = 7;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        chkMustChangePassword.setSelected(true);

        formPanel.add(
                chkMustChangePassword,
                constraints
        );

        constraints.gridy = 8;

        JLabel lblEmployeeHint = new JLabel(
                "Employee ID may be left blank."
        );

        lblEmployeeHint.setForeground(Color.GRAY);

        formPanel.add(
                lblEmployeeHint,
                constraints
        );

        constraints.gridy = 9;

        JLabel lblPasswordHint = new JLabel(
                "Passwords must contain at least 8 characters."
        );

        lblPasswordHint.setForeground(Color.GRAY);

        formPanel.add(
                lblPasswordHint,
                constraints
        );

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        7,
                        10
                )
        );

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        10,
                        18,
                        10
                )
        );

        buttonPanel.add(btnNew);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnFind);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnResetPassword);
        buttonPanel.add(btnUnlock);
        buttonPanel.add(btnFirst);
        buttonPanel.add(btnPrevious);
        buttonPanel.add(btnNext);
        buttonPanel.add(btnLast);
        buttonPanel.add(btnClose);

        txtUserId.setEditable(false);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Adds one label and input component to the form.
     */
    private void addFormRow(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String labelText,
            JComponent component
    ) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.fill =
                GridBagConstraints.NONE;

        constraints.weightx = 0;

        panel.add(
                new JLabel(labelText),
                constraints
        );

        constraints.gridx = 1;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        constraints.weightx = 1;

        panel.add(
                component,
                constraints
        );
    }

    /**
     * Registers the form's button events.
     */
    private void registerEvents() {

        btnNew.addActionListener(
                event -> prepareNewUser()
        );

        btnSave.addActionListener(
                event -> saveUser()
        );

        btnFind.addActionListener(
                event -> findUser()
        );

        btnEdit.addActionListener(
                event -> editUser()
        );

        btnDelete.addActionListener(
                event -> deleteUser()
        );

        btnResetPassword.addActionListener(
                event -> resetPassword()
        );

        btnUnlock.addActionListener(
                event -> unlockUser()
        );

        btnFirst.addActionListener(
                event -> showFirst()
        );

        btnPrevious.addActionListener(
                event -> showPrevious()
        );

        btnNext.addActionListener(
                event -> showNext()
        );

        btnLast.addActionListener(
                event -> showLast()
        );

        btnClose.addActionListener(
                event -> dispose()
        );
    }

    /**
     * Clears the form and generates the next user ID.
     */
    private void prepareNewUser() {

        try {

            txtUserId.setText(
                    userDAO.generateNextUserId()
            );

            txtEmployeeId.setText("");
            txtUsername.setText("");
            txtPassword.setText("");
            txtConfirmPassword.setText("");

            cmbRole.setSelectedIndex(0);
            cmbStatus.setSelectedItem("Active");

            chkMustChangePassword.setSelected(true);

            currentIndex = -1;

            txtUsername.requestFocusInWindow();

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Saves a new system user.
     */
    private void saveUser() {

        char[] password =
                txtPassword.getPassword();

        char[] confirmation =
                txtConfirmPassword.getPassword();

        try {

            validateForm(
                    true,
                    password,
                    confirmation
            );

            User user =
                    readUserFromForm();

            userDAO.createUser(
                    user,
                    password,
                    SessionManager.getCurrentUserId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "The user was saved successfully.",
                    "User Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshUserRecords();
            prepareNewUser();

        } catch (IllegalArgumentException exception) {

            showValidationError(
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            showDatabaseError(exception);

        } finally {

            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');

            txtPassword.setText("");
            txtConfirmPassword.setText("");
        }
    }

    /**
     * Finds a user using their User ID.
     */
    private void findUser() {

        String userId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the User ID:",
                        "Find User",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (userId == null
                || userId.isBlank()) {

            return;
        }

        try {

            User user = findUserById(
                    userId.trim()
            );

            if (user == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "No user was found with that ID.",
                        "User Not Found",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            displayUser(user);

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Updates the displayed user.
     */
    private void editUser() {

        try {

            validateForm(
                    false,
                    null,
                    null
            );

            User user =
                    readUserFromForm();

            userDAO.updateUser(
                    user,
                    SessionManager.getCurrentUserId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "The user was updated successfully.",
                    "User Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshUserRecords();
            displayUser(user);

        } catch (IllegalArgumentException exception) {

            showValidationError(
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Deletes the displayed user.
     */
    private void deleteUser() {

        String userId =
                txtUserId.getText().trim();

        if (userId.isEmpty()) {

            showValidationError(
                    "Select a user first."
            );

            return;
        }

        if (userId.equals(
                SessionManager.getCurrentUserId()
        )) {

            showValidationError(
                    "You cannot delete your currently logged-in account."
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete user " + userId + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (answer
                != JOptionPane.YES_OPTION) {

            return;
        }

        try {

            userDAO.deleteUser(
                    userId,
                    SessionManager.getCurrentUserId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "The user was deleted successfully.",
                    "User Deleted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshUserRecords();
            prepareNewUser();

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Resets the selected user's password.
     */
    private void resetPassword() {

        String userId =
                txtUserId.getText().trim();

        if (userId.isEmpty()) {

            showValidationError(
                    "Select a user first."
            );

            return;
        }

        JPasswordField temporaryPassword =
                new JPasswordField();

        JPasswordField confirmPassword =
                new JPasswordField();

        JPanel passwordPanel =
                new JPanel(
                        new GridLayout(
                                0,
                                1,
                                5,
                                5
                        )
                );

        passwordPanel.add(
                new JLabel(
                        "New temporary password:"
                )
        );

        passwordPanel.add(
                temporaryPassword
        );

        passwordPanel.add(
                new JLabel(
                        "Confirm temporary password:"
                )
        );

        passwordPanel.add(
                confirmPassword
        );

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        passwordPanel,
                        "Reset Password",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (answer
                != JOptionPane.OK_OPTION) {

            return;
        }

        char[] password =
                temporaryPassword.getPassword();

        char[] confirmation =
                confirmPassword.getPassword();

        try {

            validatePassword(
                    password,
                    confirmation
            );

            userDAO.resetPassword(
                    userId,
                    password,
                    SessionManager.getCurrentUserId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    """
                    Password reset successfully.

                    The user must change the temporary password
                    during their next login.
                    """,
                    "Password Reset",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshUserRecords();

            User refreshedUser =
                    findUserById(userId);

            if (refreshedUser != null) {
                displayUser(refreshedUser);
            }

        } catch (IllegalArgumentException exception) {

            showValidationError(
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            showDatabaseError(exception);

        } finally {

            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');

            temporaryPassword.setText("");
            confirmPassword.setText("");
        }
    }

    /**
     * Unlocks the selected user account.
     */
    private void unlockUser() {

        String userId =
                txtUserId.getText().trim();

        if (userId.isEmpty()) {

            showValidationError(
                    "Select a user first."
            );

            return;
        }

        try {

            userDAO.unlockAccount(
                    userId,
                    SessionManager.getCurrentUserId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "The user account was unlocked successfully.",
                    "Account Unlocked",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshUserRecords();

            User refreshedUser =
                    findUserById(userId);

            if (refreshedUser != null) {
                displayUser(refreshedUser);
            }

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Creates a User object from the form fields.
     */
    private User readUserFromForm() {

        User user = new User();

        user.setUserId(
                txtUserId.getText().trim()
        );

        String employeeId =
                txtEmployeeId.getText().trim();

        if (employeeId.isEmpty()) {

            user.setEmployeeId(null);

        } else {

            user.setEmployeeId(
                    employeeId.toUpperCase()
            );
        }

        user.setUsername(
                txtUsername.getText().trim()
        );

        String selectedRole =
                (String) cmbRole.getSelectedItem();

        user.setRoleId(
                roles.get(selectedRole)
        );

        user.setAccountStatus(
                (String) cmbStatus.getSelectedItem()
        );

        user.setMustChangePassword(
                chkMustChangePassword.isSelected()
        );

        return user;
    }

    /**
     * Finds a user by searching the list returned by UserDAO.
     *
     * @param userId user ID to search for
     * @return matching user, or null when no user is found
     * @throws SQLException when the database query fails
     */
    private User findUserById(String userId)
            throws SQLException {

        if (userId == null
                || userId.isBlank()) {

            return null;
        }

        List<User> users =
                userDAO.findAllUsers();

        for (User user : users) {

            if (user.getUserId() != null
                    && user.getUserId()
                            .equalsIgnoreCase(
                                    userId.trim()
                            )) {

                return user;
            }
        }

        return null;
    }

    /**
     * Displays a user record on the form.
     */
    private void displayUser(User user) {

        txtUserId.setText(
                user.getUserId()
        );

        if (user.getEmployeeId() == null) {

            txtEmployeeId.setText("");

        } else {

            txtEmployeeId.setText(
                    user.getEmployeeId()
            );
        }

        txtUsername.setText(
                user.getUsername()
        );

        selectRole(
                user.getRoleId()
        );

        cmbStatus.setSelectedItem(
                user.getAccountStatus()
        );

        chkMustChangePassword.setSelected(
                user.isMustChangePassword()
        );

        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }

    /**
     * Selects a role using its database role ID.
     */
    private void selectRole(String roleId) {

        for (Map.Entry<String, String> entry
                : roles.entrySet()) {

            if (entry.getValue().equals(roleId)) {

                cmbRole.setSelectedItem(
                        entry.getKey()
                );

                return;
            }
        }
    }

    /**
     * Validates the user form.
     */
    private void validateForm(
            boolean creating,
            char[] password,
            char[] confirmation
    ) {

        if (txtUserId.getText().isBlank()) {

            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        if (txtUsername.getText().isBlank()) {

            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (txtUsername
                .getText()
                .trim()
                .length() < 3) {

            throw new IllegalArgumentException(
                    "Username must contain at least 3 characters."
            );
        }

        if (cmbRole.getSelectedItem() == null) {

            throw new IllegalArgumentException(
                    "Select a user role."
            );
        }

        if (cmbStatus.getSelectedItem() == null) {

            throw new IllegalArgumentException(
                    "Select an account status."
            );
        }

        if (creating) {

            validatePassword(
                    password,
                    confirmation
            );
        }
    }

    /**
     * Validates a password and its confirmation.
     */
    private void validatePassword(
            char[] password,
            char[] confirmation
    ) {

        if (password == null
                || password.length < 8) {

            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        if (!Arrays.equals(
                password,
                confirmation
        )) {

            throw new IllegalArgumentException(
                    "The passwords do not match."
            );
        }
    }

    /**
     * Reloads all user records from the database.
     */
    private void refreshUserRecords()
            throws SQLException {

        userRecords = new ArrayList<>(
                userDAO.findAllUsers()
        );

        currentIndex = -1;
    }

    /**
     * Loads records when navigation is first used.
     */
    private void ensureRecordsLoaded()
            throws SQLException {

        if (userRecords.isEmpty()) {
            refreshUserRecords();
        }
    }

    /**
     * Displays the first user.
     */
    private void showFirst() {

        try {

            refreshUserRecords();

            if (userRecords.isEmpty()) {

                showValidationError(
                        "There are no user records."
                );

                return;
            }

            currentIndex = 0;

            displayUser(
                    userRecords.get(currentIndex)
            );

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Displays the previous user.
     */
    private void showPrevious() {

        try {

            ensureRecordsLoaded();

            if (userRecords.isEmpty()) {

                showValidationError(
                        "There are no user records."
                );

                return;
            }

            if (currentIndex <= 0) {

                currentIndex = 0;

            } else {

                currentIndex--;
            }

            displayUser(
                    userRecords.get(currentIndex)
            );

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Displays the next user.
     */
    private void showNext() {

        try {

            ensureRecordsLoaded();

            if (userRecords.isEmpty()) {

                showValidationError(
                        "There are no user records."
                );

                return;
            }

            if (currentIndex < 0) {

                currentIndex = 0;

            } else if (
                    currentIndex
                    < userRecords.size() - 1
            ) {

                currentIndex++;
            }

            displayUser(
                    userRecords.get(currentIndex)
            );

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Displays the last user.
     */
    private void showLast() {

        try {

            refreshUserRecords();

            if (userRecords.isEmpty()) {

                showValidationError(
                        "There are no user records."
                );

                return;
            }

            currentIndex =
                    userRecords.size() - 1;

            displayUser(
                    userRecords.get(currentIndex)
            );

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Displays a validation warning.
     */
    private void showValidationError(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Displays a database error.
     */
    private void showDatabaseError(
            SQLException exception
    ) {

        JOptionPane.showMessageDialog(
                this,
                "A database error occurred.\n"
                        + exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}