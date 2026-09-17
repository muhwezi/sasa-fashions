package com.sasafashions.view;

import com.sasafashions.dao.UserDAO;
import com.sasafashions.model.User;
import com.sasafashions.security.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Login window for the Sasa Fashions Management System.
 *
 * <p>The form authenticates users, creates an application session,
 * enforces compulsory password changes and opens the main menu.</p>
 *
 * @author Joshua Muhwezi
 */
public class LoginForm extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    private final JTextField txtUsername =
            new JTextField(22);

    private final JPasswordField txtPassword =
            new JPasswordField(22);

    private final JCheckBox chkShowPassword =
            new JCheckBox("Show password");

    private final JButton btnLogin =
            new JButton("Login");

    private final JButton btnForgotPassword =
            new JButton("Forgot Password?");

    private final JButton btnExit =
            new JButton("Exit");

    private final char originalEchoCharacter;

    /**
     * Creates the login form.
     */
    public LoginForm() {

        setTitle("Sasa Fashions - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 470);
        setResizable(false);
        setLocationRelativeTo(null);

        originalEchoCharacter =
                txtPassword.getEchoChar();

        createInterface();
        registerEvents();
    }

    /**
     * Creates the visible login interface.
     */
    private void createInterface() {

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(
                new BoxLayout(
                        headerPanel,
                        BoxLayout.Y_AXIS
                )
        );

        headerPanel.setBackground(
                new Color(74, 45, 125)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        28, 20, 28, 20
                )
        );

        JLabel lblBusinessName =
                new JLabel("SASA FASHIONS");

        lblBusinessName.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        lblBusinessName.setForeground(Color.WHITE);
        lblBusinessName.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        JLabel lblSystemName = new JLabel(
                "Tailoring Business Management System"
        );

        lblSystemName.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        lblSystemName.setForeground(
                new Color(230, 225, 245)
        );

        lblSystemName.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        headerPanel.add(lblBusinessName);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(lblSystemName);

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 45, 10, 45
                )
        );

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(9, 8, 9, 8);

        constraints.anchor =
                GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridy = 0;

        formPanel.add(
                new JLabel("Username"),
                constraints
        );

        constraints.gridx = 1;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        formPanel.add(txtUsername, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill =
                GridBagConstraints.NONE;

        formPanel.add(
                new JLabel("Password"),
                constraints
        );

        constraints.gridx = 1;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        formPanel.add(txtPassword, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;

        formPanel.add(
                chkShowPassword,
                constraints
        );

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        15
                )
        );

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnForgotPassword);
        buttonPanel.add(btnExit);

        JLabel lblFooter = new JLabel(
                "Authorized users only",
                SwingConstants.CENTER
        );

        lblFooter.setForeground(Color.GRAY);
        lblFooter.setBorder(
                BorderFactory.createEmptyBorder(
                        5, 5, 12, 5
                )
        );

        JPanel southPanel =
                new JPanel(new BorderLayout());

        southPanel.add(
                buttonPanel,
                BorderLayout.CENTER
        );

        southPanel.add(
                lblFooter,
                BorderLayout.SOUTH
        );

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnLogin);

        SwingUtilities.invokeLater(
                () -> txtUsername.requestFocusInWindow()
        );
    }

    /**
     * Registers the form events.
     */
    private void registerEvents() {

        btnLogin.addActionListener(
                event -> login()
        );

        btnForgotPassword.addActionListener(
                event -> showPasswordHelp()
        );

        btnExit.addActionListener(
                event -> exitApplication()
        );

        chkShowPassword.addActionListener(
                event -> showOrHidePassword()
        );
    }

    /**
     * Authenticates the entered username and password.
     */
    private void login() {

        String username =
                txtUsername.getText().trim();

        char[] password =
                txtPassword.getPassword();

        try {
            if (username.isEmpty()) {

                showWarning(
                        "Enter your username."
                );

                txtUsername.requestFocus();
                return;
            }

            if (password.length == 0) {

                showWarning(
                        "Enter your password."
                );

                txtPassword.requestFocus();
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("Signing in...");

            User authenticatedUser =
                    userDAO.authenticate(
                            username,
                            password
                    );

            if (authenticatedUser == null) {

                showWarning(
                        """
                        Incorrect username or password.

                        The account will be temporarily locked
                        after three failed attempts.
                        """
                );

                txtPassword.setText("");
                txtPassword.requestFocus();
                return;
            }

            SessionManager.login(
                    authenticatedUser
            );

            dispose();

            if (authenticatedUser
                    .isMustChangePassword()) {

                openRequiredPasswordChange(
                        authenticatedUser
                );

            } else {

                openMainMenu();
            }

        } catch (SQLException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to log in.\n"
                            + exception.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            Arrays.fill(password, '\0');

            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }

    /**
     * Opens the compulsory password-change screen.
     */
    private void openRequiredPasswordChange(
            User user
    ) {

        ChangePasswordForm changePasswordForm =
                new ChangePasswordForm(
                        user,
                        this::openMainMenu
                );

        changePasswordForm.setVisible(true);
    }

    /**
     * Opens the main application menu.
     */
    private void openMainMenu() {

        SwingUtilities.invokeLater(
                () -> new MainMenuFrame()
                        .setVisible(true)
        );
    }

    /**
     * Shows or hides the password.
     */
    private void showOrHidePassword() {

        if (chkShowPassword.isSelected()) {
            txtPassword.setEchoChar((char) 0);
        } else {
            txtPassword.setEchoChar(
                    originalEchoCharacter
            );
        }
    }

    /**
     * Explains how forgotten passwords are handled.
     */
    private void showPasswordHelp() {

        JOptionPane.showMessageDialog(
                this,
                """
                Contact the system administrator.

                The administrator can reset your password and
                provide a temporary password. You will be required
                to change it during your next login.
                """,
                "Forgot Password",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Confirms before closing the application.
     */
    private void exitApplication() {

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you want to exit Sasa Fashions?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            System.exit(0);
        }
    }

    /**
     * Displays a validation or authentication warning.
     *
     * @param message warning message
     */
    private void showWarning(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Login",
                JOptionPane.WARNING_MESSAGE
        );
    }
}