package com.sasafashions.view;

import com.sasafashions.model.User;
import com.sasafashions.security.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Main navigation window for the Sasa Fashions Management System.
 *
 * <p>The window contains the ten required data-menu items and five
 * report-menu items.</p>
 *
 * @author Joshua Muhwezi
 */
public class MainMenuFrame extends JFrame {

    private final JMenuBar menuBar = new JMenuBar();

    private final JMenu dataMenu =
            new JMenu("Data");

    private final JMenu reportsMenu =
            new JMenu("Reports");

    private final JLabel lblLoggedInUser =
            new JLabel();

    /**
     * Creates the main application menu.
     */
    public MainMenuFrame() {

        SessionManager.requireLogin();

        setTitle("Sasa Fashions Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        createMenuBar();
        createDashboard();
        registerWindowClosing();
    }

    /**
     * Creates the Data and Reports menus.
     */
    private void createMenuBar() {

        createDataMenu();
        createReportsMenu();

        menuBar.add(dataMenu);
        menuBar.add(reportsMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Creates the ten required Data menu items.
     */
    private void createDataMenu() {

        JMenuItem customerItem =
                new JMenuItem("1. Customers");

        JMenuItem employeeItem =
                new JMenuItem("2. Employees");

        JMenuItem productItem =
                new JMenuItem("3. Products");

        JMenuItem measurementItem =
                new JMenuItem("4. Measurements");

        JMenuItem orderItem =
                new JMenuItem("5. Orders");

        JMenuItem paymentItem =
                new JMenuItem("6. Payments");

        JMenuItem supplierItem =
                new JMenuItem("7. Suppliers");

        JMenuItem materialItem =
                new JMenuItem("8. Materials");

        JMenuItem purchaseItem =
                new JMenuItem("9. Purchases");

        JMenuItem userItem =
                new JMenuItem("10. Users");

        customerItem.addActionListener(
                event -> openCustomers()
        );

        employeeItem.addActionListener(
                event -> openEmployees()
        );

        productItem.addActionListener(
                event -> openProducts()
        );

        measurementItem.addActionListener(
                event -> openMeasurements()
        );

        orderItem.addActionListener(
                event -> openOrders()
        );

        paymentItem.addActionListener(
                event -> openPayments()
        );

        supplierItem.addActionListener(
                event -> openSuppliers()
        );

        materialItem.addActionListener(
                event -> openMaterials()
        );

        purchaseItem.addActionListener(
                event -> openPurchases()
        );

        userItem.addActionListener(
                event -> openUsers()
        );

        /*
         * Only an administrator should access user management.
         */
        userItem.setEnabled(
                SessionManager.isAdministrator()
        );

        dataMenu.add(customerItem);
        dataMenu.add(employeeItem);
        dataMenu.add(productItem);
        dataMenu.add(measurementItem);
        dataMenu.add(orderItem);
        dataMenu.add(paymentItem);
        dataMenu.add(supplierItem);
        dataMenu.add(materialItem);
        dataMenu.add(purchaseItem);
        dataMenu.addSeparator();
        dataMenu.add(userItem);
    }

    /**
     * Creates the five required Reports menu items.
     */
    private void createReportsMenu() {

        JMenuItem customerReport =
                new JMenuItem("1. Customer Report");

        JMenuItem orderReport =
                new JMenuItem("2. Order Report");

        JMenuItem paymentReport =
                new JMenuItem("3. Payment Report");

        JMenuItem stockReport =
                new JMenuItem("4. Material Stock Report");

        JMenuItem purchaseReport =
                new JMenuItem("5. Purchase Report");

        customerReport.addActionListener(
                event -> reportNotReady("Customer Report")
        );

        orderReport.addActionListener(
                event -> reportNotReady("Order Report")
        );

        paymentReport.addActionListener(
                event -> reportNotReady("Payment Report")
        );

        stockReport.addActionListener(
                event -> reportNotReady(
                        "Material Stock Report"
                )
        );

        purchaseReport.addActionListener(
                event -> reportNotReady("Purchase Report")
        );

        reportsMenu.add(customerReport);
        reportsMenu.add(orderReport);
        reportsMenu.add(paymentReport);
        reportsMenu.add(stockReport);
        reportsMenu.add(purchaseReport);
    }

    /**
     * Creates the visible dashboard.
     */
    private void createDashboard() {

        JPanel headerPanel = new JPanel(
                new BorderLayout()
        );

        headerPanel.setBackground(
                new Color(74, 45, 125)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        18, 25, 18, 25
                )
        );

        JLabel lblTitle = new JLabel(
                "SASA FASHIONS MANAGEMENT SYSTEM"
        );

        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        User currentUser =
                SessionManager.getCurrentUser();

        String roleName =
                getRoleName(currentUser.getRoleId());

        lblLoggedInUser.setText(
                "Logged in as: "
                        + currentUser.getUsername()
                        + " | Role: "
                        + roleName
        );

        lblLoggedInUser.setForeground(Color.WHITE);
        lblLoggedInUser.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(
                lblLoggedInUser,
                BorderLayout.EAST
        );

        JPanel centrePanel = new JPanel(
                new GridBagLayout()
        );

        centrePanel.setBackground(
                new Color(245, 245, 250)
        );

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(
                new BoxLayout(
                        welcomePanel,
                        BoxLayout.Y_AXIS
                )
        );

        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 210, 220)
                        ),
                        BorderFactory.createEmptyBorder(
                                45, 70, 45, 70
                        )
                )
        );

        JLabel lblWelcome = new JLabel(
                "Welcome to Sasa Fashions"
        );

        lblWelcome.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        lblWelcome.setFont(
                new Font("Arial", Font.BOLD, 30)
        );

        lblWelcome.setForeground(
                new Color(74, 45, 125)
        );

        JLabel lblDescription = new JLabel(
                "Tailoring Business Management System"
        );

        lblDescription.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        lblDescription.setFont(
                new Font("Arial", Font.PLAIN, 18)
        );

        JButton btnLogout =
                new JButton("Logout");

        btnLogout.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        btnLogout.addActionListener(
                event -> logout()
        );

        welcomePanel.add(lblWelcome);
        welcomePanel.add(Box.createVerticalStrut(15));
        welcomePanel.add(lblDescription);
        welcomePanel.add(Box.createVerticalStrut(30));
        welcomePanel.add(btnLogout);

        centrePanel.add(welcomePanel);

        JLabel footer = new JLabel(
                "Sasa Fashions © 2026",
                SwingConstants.CENTER
        );

        footer.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        add(headerPanel, BorderLayout.NORTH);
        add(centrePanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private String getRoleName(String roleId) {

        if ("ROL-0001".equals(roleId)) {
            return "Administrator";
        }

        if ("ROL-0002".equals(roleId)) {
            return "Manager";
        }

        if ("ROL-0003".equals(roleId)) {
            return "Staff";
        }

        return "Unknown";
    }

    private void openCustomers() {
        new CustomerForm().setVisible(true);
    }

    private void openEmployees() {
        new EmployeeForm().setVisible(true);
    }

    private void openProducts() {
        new ProductForm().setVisible(true);
    }

    private void openMeasurements() {
        new MeasurementForm().setVisible(true);
    }

    private void openOrders() {
        new OrderForm().setVisible(true);
    }

    private void openPayments() {
        new PaymentForm().setVisible(true);
    }

    private void openSuppliers() {
        new SupplierForm().setVisible(true);
    }

    private void openMaterials() {
        new MaterialForm().setVisible(true);
    }

    private void openPurchases() {
        new PurchaseForm().setVisible(true);
    }

    private void openUsers() {
        new UserForm().setVisible(true);
    }

    /**
     * Temporary response until PDF reports are implemented.
     */
    private void reportNotReady(String reportName) {

        JOptionPane.showMessageDialog(
                this,
                reportName
                        + " will be implemented in the reporting stage.",
                "Report",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Ends the current user session.
     *
     * <p>The LoginForm will be opened here after it is created.</p>
     */
    private void logout() {

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        SessionManager.logout();
        dispose();

        /*
         * LoginForm will replace this temporary application exit
         * in the next step.
         */
        System.exit(0);
    }

    /**
     * Confirms before closing the entire application.
     */
    private void registerWindowClosing() {

        addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent event
                    ) {

                        int answer =
                                JOptionPane.showConfirmDialog(
                                        MainMenuFrame.this,
                                        "Exit Sasa Fashions?",
                                        "Confirm Exit",
                                        JOptionPane.YES_NO_OPTION
                                );

                        if (answer
                                == JOptionPane.YES_OPTION) {

                            SessionManager.logout();
                            System.exit(0);
                        }
                    }
                }
        );
    }
}