package com.sasafashions.security;

import com.sasafashions.dao.RoleDAO;
import com.sasafashions.dao.UserDAO;
import com.sasafashions.model.Role;
import com.sasafashions.model.User;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Creates the first Administrator account.
 *
 * <p>The utility refuses to create another account when an
 * Administrator already exists. It should normally be run
 * only once during initial system installation.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public final class InitialAdminSetup {

    /**
     * Prevents creation of utility objects.
     */
    private InitialAdminSetup() {
    }

    /**
     * Runs the administrator setup process.
     */
    private static void runSetup() {

        UserDAO userDAO = new UserDAO();
        RoleDAO roleDAO = new RoleDAO();

        try {
            boolean administratorExists =
                    userDAO.findAllUsers()
                            .stream()
                            .anyMatch(
                                    User::isAdministrator
                            );

            if (administratorExists) {

                JOptionPane.showMessageDialog(
                        null,
                        "An Administrator account "
                        + "already exists.\n"
                        + "No new account was created.",
                        "Initial Administrator Setup",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            Role administratorRole =
                    roleDAO.findRoleByName(
                            "Administrator"
                    );

            if (administratorRole == null) {

                JOptionPane.showMessageDialog(
                        null,
                        "The Administrator role was not found.\n"
                        + "Insert the system roles first.",
                        "Setup Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String username =
                    JOptionPane.showInputDialog(
                            null,
                            "Enter the first Administrator username:",
                            "Initial Administrator Setup",
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (username == null
                    || username.trim().isEmpty()) {
                return;
            }

            JPasswordField passwordField =
                    new JPasswordField(20);

            JPasswordField confirmPasswordField =
                    new JPasswordField(20);

            JPanel passwordPanel =
                    new JPanel(
                            new GridLayout(
                                    2,
                                    2,
                                    8,
                                    8
                            )
                    );

            passwordPanel.add(
                    new JLabel("Temporary Password:")
            );

            passwordPanel.add(passwordField);

            passwordPanel.add(
                    new JLabel("Confirm Password:")
            );

            passwordPanel.add(
                    confirmPasswordField
            );

            int option =
                    JOptionPane.showConfirmDialog(
                            null,
                            passwordPanel,
                            "Create Administrator Password",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (option
                    != JOptionPane.OK_OPTION) {
                return;
            }

            char[] password =
                    passwordField.getPassword();

            char[] confirmation =
                    confirmPasswordField
                            .getPassword();

            try {
                if (!Arrays.equals(
                        password,
                        confirmation
                )) {

                    JOptionPane.showMessageDialog(
                            null,
                            "The passwords do not match.",
                            "Setup Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

                User administrator = new User();

                administrator.setEmployeeId(null);

                administrator.setRoleId(
                        administratorRole.getRoleId()
                );

                administrator.setRoleName(
                        administratorRole.getRoleName()
                );

                administrator.setUsername(
                        username.trim()
                );

                administrator.setAccountStatus(
                        "Active"
                );

                userDAO.createUser(
                        administrator,
                        password,
                        null
                );

                JOptionPane.showMessageDialog(
                        null,
                        "Administrator account created successfully.\n\n"
                        + "User ID: "
                        + administrator.getUserId()
                        + "\nUsername: "
                        + administrator.getUsername()
                        + "\n\nThe Administrator must change "
                        + "the temporary password after login.",
                        "Setup Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } finally {

                Arrays.fill(password, '\0');
                Arrays.fill(confirmation, '\0');
            }

        } catch (IllegalArgumentException exception) {

            JOptionPane.showMessageDialog(
                    null,
                    exception.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException exception) {

            JOptionPane.showMessageDialog(
                    null,
                    "Database error:\n"
                    + exception.getMessage(),
                    "Setup Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Starts the initial-administrator setup utility.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                InitialAdminSetup::runSetup
        );
    }
}