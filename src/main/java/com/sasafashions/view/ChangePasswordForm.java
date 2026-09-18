package com.sasafashions.view;

import com.sasafashions.dao.UserDAO;
import com.sasafashions.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Allows an authenticated user to change their password.
 *
 * <p>The form can enforce a compulsory password change when a user
 * logs in with a temporary password.</p>
 *
 * @author SASA Group
 */
public class ChangePasswordForm extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    private final User user;
    private final Runnable afterPasswordChange;
    private final boolean passwordChangeRequired;

    private final JPasswordField txtCurrentPassword =
            new JPasswordField(22);

    private final JPasswordField txtNewPassword =
            new JPasswordField(22);

    private final JPasswordField txtConfirmPassword =
            new JPasswordField(22);

    private final JButton btnChangePassword =
            new JButton("Change Password");

    private final JButton btnCancel =
            new JButton("Cancel");

    /**
     * Creates the password-change form.
     *
     * @param user currently authenticated user
     * @param afterPasswordChange action to run after a successful change
     */
    public ChangePasswordForm(
            User user,
            Runnable afterPasswordChange
    ) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "A logged-in user is required."
            );
        }

        this.user = user;
        this.afterPasswordChange = afterPasswordChange;
        this.passwordChangeRequired =
                user.isMustChangePassword();

        setTitle("Sasa Fashions - Change Password");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(560, 430);
        setResizable(false);
        setLocationRelativeTo(null);

        createInterface();
        registerEvents();
    }

    /**
     * Creates a normal password-change form without a callback.
     *
     * @param user currently authenticated user
     */
    public ChangePasswordForm(User user) {
        this(user, null);
    }

    /**
     * Creates the graphical interface.
     */
    private void createInterface() {

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(
                new Color(74, 45, 125)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        18, 10, 18, 10
                )
        );

        JLabel lblTitle =
                new JLabel("CHANGE PASSWORD");

        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(
                new Font("Arial", Font.BOLD, 21)
        );

        headerPanel.add(lblTitle);

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 30, 10, 30
                )
        );

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(8, 8, 8, 8);

        constraints.anchor =
                GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel(
                "Username: " + user.getUsername()
        );

        lblUsername.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        formPanel.add(lblUsername, constraints);

        constraints.gridy = 1;

        JLabel lblInstruction = new JLabel(
                passwordChangeRequired
                        ? "You must change your temporary password before continuing."
                        : "Enter your current password and choose a new password."
        );

        lblInstruction.setForeground(
                passwordChangeRequired
                        ? new Color(180, 60, 30)
                        : Color.DARK_GRAY
        );

        formPanel.add(lblInstruction, constraints);

        constraints.gridwidth = 1;

        addFormRow(
                formPanel,
                constraints,
                2,
                "Current Password",
                txtCurrentPassword
        );

        addFormRow(
                formPanel,
                constraints,
                3,
                "New Password",
                txtNewPassword
        );

        addFormRow(
                formPanel,
                constraints,
                4,
                "Confirm Password",
                txtConfirmPassword
        );

        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        JLabel lblPasswordRule = new JLabel(
                "Minimum: 8 characters"
        );

        lblPasswordRule.setForeground(Color.GRAY);
        formPanel.add(lblPasswordRule, constraints);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        12,
                        15
                )
        );

        buttonPanel.add(btnChangePassword);
        buttonPanel.add(btnCancel);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(
                btnChangePassword
        );

        txtCurrentPassword.requestFocusInWindow();
    }

    /**
     * Adds one label and field to the form.
     */
    private void addFormRow(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String label,
            JComponent component
    ) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.fill =
                GridBagConstraints.NONE;

        panel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        panel.add(component, constraints);
    }

    /**
     * Registers button and window events.
     */
    private void registerEvents() {

        btnChangePassword.addActionListener(
                event -> changePassword()
        );

        btnCancel.addActionListener(
                event -> cancelPasswordChange()
        );

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent event
                    ) {
                        cancelPasswordChange();
                    }
                }
        );
    }

    /**
     * Validates and changes the user's password.
     */
    private void changePassword() {

        char[] currentPassword =
                txtCurrentPassword.getPassword();

        char[] newPassword =
                txtNewPassword.getPassword();

        char[] confirmation =
                txtConfirmPassword.getPassword();

        try {
            validatePasswords(
                    currentPassword,
                    newPassword,
                    confirmation
            );

            userDAO.changePassword(
                    user.getUserId(),
                    currentPassword,
                    newPassword
            );

            /*
             * Update the in-memory User object after the database
             * password has been changed.
             */
            user.setMustChangePassword(false);

            JOptionPane.showMessageDialog(
                    this,
                    "Your password was changed successfully.",
                    "Password Changed",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            if (afterPasswordChange != null) {
                afterPasswordChange.run();
            }

        } catch (IllegalArgumentException exception) {

            showWarning(exception.getMessage());

        } catch (SQLException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to change the password.\n"
                            + exception.getMessage(),
                    "Password Change Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            Arrays.fill(currentPassword, '\0');
            Arrays.fill(newPassword, '\0');
            Arrays.fill(confirmation, '\0');

            txtCurrentPassword.setText("");
            txtNewPassword.setText("");
            txtConfirmPassword.setText("");
        }
    }

    /**
     * Validates the entered password information.
     */
    private void validatePasswords(
            char[] currentPassword,
            char[] newPassword,
            char[] confirmation
    ) {

        if (currentPassword.length == 0) {
            throw new IllegalArgumentException(
                    "Enter your current password."
            );
        }

        if (newPassword.length < 8) {
            throw new IllegalArgumentException(
                    "The new password must contain at least 8 characters."
            );
        }

        if (!Arrays.equals(
                newPassword,
                confirmation
        )) {
            throw new IllegalArgumentException(
                    "The new password and confirmation do not match."
            );
        }

        if (Arrays.equals(
                currentPassword,
                newPassword
        )) {
            throw new IllegalArgumentException(
                    "The new password must be different from the current password."
            );
        }
    }

    /**
     * Handles cancellation of the password-change operation.
     */
    private void cancelPasswordChange() {

        if (passwordChangeRequired) {

            JOptionPane.showMessageDialog(
                    this,
                    """
                    You must change the temporary password
                    before accessing the system.
                    """,
                    "Password Change Required",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        dispose();
    }

    /**
     * Displays a validation warning.
     *
     * @param message warning message
     */
    private void showWarning(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );
    }
}