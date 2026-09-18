package com.sasafashions.view;

import com.sasafashions.model.User;
import com.sasafashions.report.PdfReportGenerator;
import com.sasafashions.security.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main navigation window for the Sasa Fashions Management System.
 *
 * <p>The frame contains ten Data functions, five PDF reports and
 * account-management functions.</p>
 *
 * @author SASA Group
 */
public class MainMenuFrame extends JFrame {

    private static final Color PURPLE =
            new Color(74, 45, 125);

    private static final Color DARK_PURPLE =
            new Color(55, 31, 100);

    private static final Color LIGHT_PURPLE =
            new Color(237, 232, 247);

    /**
     * Blue used by Report and Account buttons.
     */
    private static final Color REPORT_BLUE =
            new Color(35, 100, 190);

    private static final Color BACKGROUND =
            new Color(245, 246, 250);

    private final PdfReportGenerator pdfReportGenerator =
            new PdfReportGenerator();

    private final JMenuBar menuBar =
            new JMenuBar();

    private final JMenu dataMenu =
            new JMenu("Data");

    private final JMenu reportsMenu =
            new JMenu("Reports");

    private final User currentUser;

    /**
     * Creates the main application dashboard.
     */
    public MainMenuFrame() {

        SessionManager.requireLogin();

        currentUser =
                SessionManager.getCurrentUser();

        setTitle(
                "Sasa Fashions Management System"
        );

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setSize(1200, 750);

        setMinimumSize(
                new Dimension(1000, 650)
        );

        setLocationRelativeTo(null);

        createMenuBar();
        createDashboard();
        registerWindowClosing();

        setExtendedState(
                JFrame.MAXIMIZED_BOTH
        );
    }

    /**
     * Creates the Data and Reports menus.
     */
    private void createMenuBar() {

        menuBar.setBorder(
                BorderFactory.createEmptyBorder(
                        3, 8, 3, 8
                )
        );

        dataMenu.setMnemonic('D');
        reportsMenu.setMnemonic('R');

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
                createMenuItem(
                        "1. Customers",
                        this::openCustomers
                );

        JMenuItem employeeItem =
                createMenuItem(
                        "2. Employees",
                        this::openEmployees
                );

        JMenuItem productItem =
                createMenuItem(
                        "3. Products",
                        this::openProducts
                );

        JMenuItem measurementItem =
                createMenuItem(
                        "4. Measurements",
                        this::openMeasurements
                );

        JMenuItem orderItem =
                createMenuItem(
                        "5. Orders",
                        this::openOrders
                );

        JMenuItem paymentItem =
                createMenuItem(
                        "6. Payments",
                        this::openPayments
                );

        JMenuItem supplierItem =
                createMenuItem(
                        "7. Suppliers",
                        this::openSuppliers
                );

        JMenuItem materialItem =
                createMenuItem(
                        "8. Materials",
                        this::openMaterials
                );

        JMenuItem purchaseItem =
                createMenuItem(
                        "9. Purchases",
                        this::openPurchases
                );

        JMenuItem userItem =
                createMenuItem(
                        "10. Users",
                        this::openUsers
                );

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

        reportsMenu.add(
                createMenuItem(
                        "1. Customer Report",
                        () ->
                                pdfReportGenerator
                                        .generateCustomerReport(
                                                this
                                        )
                )
        );

        reportsMenu.add(
                createMenuItem(
                        "2. Order Report",
                        () ->
                                pdfReportGenerator
                                        .generateOrderReport(
                                                this
                                        )
                )
        );

        reportsMenu.add(
                createMenuItem(
                        "3. Payment Report",
                        () ->
                                pdfReportGenerator
                                        .generatePaymentReport(
                                                this
                                        )
                )
        );

        reportsMenu.add(
                createMenuItem(
                        "4. Material Stock Report",
                        () ->
                                pdfReportGenerator
                                        .generateMaterialStockReport(
                                                this
                                        )
                )
        );

        reportsMenu.add(
                createMenuItem(
                        "5. Purchase Report",
                        () ->
                                pdfReportGenerator
                                        .generatePurchaseReport(
                                                this
                                        )
                )
        );
    }

    /**
     * Creates a menu item and connects its operation.
     */
    private JMenuItem createMenuItem(
            String text,
            Runnable action
    ) {

        JMenuItem item =
                new JMenuItem(text);

        item.addActionListener(
                event -> action.run()
        );

        return item;
    }

    /**
     * Creates the complete dashboard.
     */
    private void createDashboard() {

        setLayout(new BorderLayout());

        add(
                createHeaderPanel(),
                BorderLayout.NORTH
        );

        JPanel dashboardContent =
                new JPanel();

        dashboardContent.setLayout(
                new BoxLayout(
                        dashboardContent,
                        BoxLayout.Y_AXIS
                )
        );

        dashboardContent.setBackground(
                BACKGROUND
        );

        dashboardContent.setBorder(
                new EmptyBorder(
                        25,
                        35,
                        30,
                        35
                )
        );

        dashboardContent.add(
                createWelcomePanel()
        );

        dashboardContent.add(
                Box.createVerticalStrut(20)
        );

        dashboardContent.add(
                createDataSection()
        );

        dashboardContent.add(
                Box.createVerticalStrut(22)
        );

        dashboardContent.add(
                createReportsSection()
        );

        dashboardContent.add(
                Box.createVerticalStrut(22)
        );

        dashboardContent.add(
                createAccountSection()
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        dashboardContent
                );

        scrollPane.setBorder(null);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane.getViewport()
                .setBackground(BACKGROUND);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        add(
                createFooterPanel(),
                BorderLayout.SOUTH
        );
    }

    /**
     * Creates the purple application heading.
     */
    private JPanel createHeaderPanel() {

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(PURPLE);

        headerPanel.setBorder(
                new EmptyBorder(
                        20,
                        30,
                        20,
                        30
                )
        );

        JLabel lblTitle = new JLabel(
                "SASA FASHIONS MANAGEMENT SYSTEM"
        );

        lblTitle.setForeground(Color.WHITE);

        lblTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        25
                )
        );

        String roleName =
                getRoleName(
                        currentUser.getRoleId()
                );

        JLabel lblLoggedInUser =
                new JLabel(
                        "Logged in as: "
                                + currentUser.getUsername()
                                + "  |  Role: "
                                + roleName
                );

        lblLoggedInUser.setForeground(
                Color.WHITE
        );

        lblLoggedInUser.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        headerPanel.add(
                lblTitle,
                BorderLayout.WEST
        );

        headerPanel.add(
                lblLoggedInUser,
                BorderLayout.EAST
        );

        return headerPanel;
    }

    /**
     * Creates the welcome section.
     */
    private JPanel createWelcomePanel() {

        JPanel welcomePanel =
                new JPanel(
                        new BorderLayout()
                );

        welcomePanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        welcomePanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        95
                )
        );

        welcomePanel.setBackground(
                Color.WHITE
        );

        welcomePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        220,
                                        220,
                                        228
                                )
                        ),
                        new EmptyBorder(
                                18,
                                22,
                                18,
                                22
                        )
                )
        );

        JPanel textPanel =
                new JPanel();

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.setOpaque(false);

        JLabel lblWelcome =
                new JLabel(
                        "Welcome, "
                                + currentUser.getUsername()
                );

        lblWelcome.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        lblWelcome.setForeground(PURPLE);

        JLabel lblInstruction =
                new JLabel(
                        "Select a Data function or generate a customized PDF report."
                );

        lblInstruction.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        lblInstruction.setForeground(
                Color.DARK_GRAY
        );

        textPanel.add(lblWelcome);

        textPanel.add(
                Box.createVerticalStrut(7)
        );

        textPanel.add(lblInstruction);

        welcomePanel.add(
                textPanel,
                BorderLayout.WEST
        );

        return welcomePanel;
    }

    /**
     * Creates the ten organized Data buttons.
     */
    private JPanel createDataSection() {

        JPanel sectionPanel =
                createSectionPanel(
                        "DATA MANAGEMENT",
                        "Manage the main records used by Sasa Fashions.",
                        250
                );

        JPanel buttonsPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                5,
                                14,
                                14
                        )
                );

        buttonsPanel.setOpaque(false);

        JButton customerButton =
                createDataButton(
                        "Customers",
                        "Register and manage customers",
                        this::openCustomers
                );

        JButton employeeButton =
                createDataButton(
                        "Employees",
                        "Register and manage employees",
                        this::openEmployees
                );

        JButton productButton =
                createDataButton(
                        "Products",
                        "Manage tailoring products",
                        this::openProducts
                );

        JButton measurementButton =
                createDataButton(
                        "Measurements",
                        "Record customer measurements",
                        this::openMeasurements
                );

        JButton orderButton =
                createDataButton(
                        "Orders",
                        "Create and manage customer orders",
                        this::openOrders
                );

        JButton paymentButton =
                createDataButton(
                        "Payments",
                        "Record customer payments",
                        this::openPayments
                );

        JButton supplierButton =
                createDataButton(
                        "Suppliers",
                        "Register and manage suppliers",
                        this::openSuppliers
                );

        JButton materialButton =
                createDataButton(
                        "Materials",
                        "Manage tailoring materials",
                        this::openMaterials
                );

        JButton purchaseButton =
                createDataButton(
                        "Purchases",
                        "Record material purchases",
                        this::openPurchases
                );

        JButton userButton =
                createDataButton(
                        "Users",
                        "Manage system users",
                        this::openUsers
                );

        userButton.setEnabled(
                SessionManager.isAdministrator()
        );

        if (!SessionManager.isAdministrator()) {

            userButton.setToolTipText(
                    "Administrator permission is required."
            );
        }

        buttonsPanel.add(customerButton);
        buttonsPanel.add(employeeButton);
        buttonsPanel.add(productButton);
        buttonsPanel.add(measurementButton);
        buttonsPanel.add(orderButton);

        buttonsPanel.add(paymentButton);
        buttonsPanel.add(supplierButton);
        buttonsPanel.add(materialButton);
        buttonsPanel.add(purchaseButton);
        buttonsPanel.add(userButton);

        sectionPanel.add(
                buttonsPanel,
                BorderLayout.CENTER
        );

        return sectionPanel;
    }

    /**
     * Creates the five blue Report buttons.
     */
    private JPanel createReportsSection() {

        JPanel sectionPanel =
                createSectionPanel(
                        "PDF REPORTS",
                        "Generate and save customized business reports.",
                        185
                );

        JPanel buttonsPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                5,
                                14,
                                14
                        )
                );

        buttonsPanel.setOpaque(false);

        buttonsPanel.add(
                createBlueButton(
                        "Customer Report",
                        () ->
                                pdfReportGenerator
                                        .generateCustomerReport(
                                                this
                                        )
                )
        );

        buttonsPanel.add(
                createBlueButton(
                        "Order Report",
                        () ->
                                pdfReportGenerator
                                        .generateOrderReport(
                                                this
                                        )
                )
        );

        buttonsPanel.add(
                createBlueButton(
                        "Payment Report",
                        () ->
                                pdfReportGenerator
                                        .generatePaymentReport(
                                                this
                                        )
                )
        );

        buttonsPanel.add(
                createBlueButton(
                        "Material Stock",
                        () ->
                                pdfReportGenerator
                                        .generateMaterialStockReport(
                                                this
                                        )
                )
        );

        buttonsPanel.add(
                createBlueButton(
                        "Purchase Report",
                        () ->
                                pdfReportGenerator
                                        .generatePurchaseReport(
                                                this
                                        )
                )
        );

        sectionPanel.add(
                buttonsPanel,
                BorderLayout.CENTER
        );

        return sectionPanel;
    }

    /**
     * Creates the blue Account buttons.
     */
    private JPanel createAccountSection() {

        JPanel sectionPanel =
                createSectionPanel(
                        "ACCOUNT",
                        "Manage your password or end the current session.",
                        155
                );

        JPanel buttonsPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                5
                        )
                );

        buttonsPanel.setOpaque(false);

        buttonsPanel.add(
                createBlueButton(
                        "Change Password",
                        this::changePassword
                )
        );

        buttonsPanel.add(
                createBlueButton(
                        "Logout",
                        this::logout
                )
        );

        buttonsPanel.add(
                createBlueButton(
                        "Exit Application",
                        this::exitApplication
                )
        );

        sectionPanel.add(
                buttonsPanel,
                BorderLayout.CENTER
        );

        return sectionPanel;
    }

    /**
     * Creates a reusable dashboard section.
     */
    private JPanel createSectionPanel(
            String title,
            String description,
            int maximumHeight
    ) {

        JPanel sectionPanel =
                new JPanel(
                        new BorderLayout()
                );

        sectionPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        sectionPanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        maximumHeight
                )
        );

        sectionPanel.setBackground(
                Color.WHITE
        );

        sectionPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        220,
                                        220,
                                        228
                                )
                        ),
                        new EmptyBorder(
                                16,
                                18,
                                18,
                                18
                        )
                )
        );

        JPanel headingPanel =
                new JPanel();

        headingPanel.setLayout(
                new BoxLayout(
                        headingPanel,
                        BoxLayout.Y_AXIS
                )
        );

        headingPanel.setOpaque(false);

        headingPanel.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        12,
                        0
                )
        );

        JLabel lblTitle =
                new JLabel(title);

        lblTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        lblTitle.setForeground(PURPLE);

        JLabel lblDescription =
                new JLabel(description);

        lblDescription.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        lblDescription.setForeground(
                Color.GRAY
        );

        headingPanel.add(lblTitle);

        headingPanel.add(
                Box.createVerticalStrut(4)
        );

        headingPanel.add(lblDescription);

        sectionPanel.add(
                headingPanel,
                BorderLayout.NORTH
        );

        return sectionPanel;
    }

    /**
     * Creates a light-purple Data button.
     */
    private JButton createDataButton(
            String text,
            String toolTip,
            Runnable action
    ) {

        JButton button =
                new JButton(text);

        button.setPreferredSize(
                new Dimension(170, 72)
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        button.setForeground(PURPLE);
        button.setBackground(LIGHT_PURPLE);

        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setToolTipText(toolTip);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        190,
                                        175,
                                        220
                                )
                        ),
                        new EmptyBorder(
                                12,
                                12,
                                12,
                                12
                        )
                )
        );

        button.addActionListener(
                event -> action.run()
        );

        return button;
    }

    /**
     * Creates a blue button with white text.
     *
     * <p>This method is used by both Report and Account buttons.</p>
     */
    private JButton createBlueButton(
            String text,
            Runnable action
    ) {

        JButton button =
                new JButton(text);

        button.setPreferredSize(
                new Dimension(180, 58)
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(Color.WHITE);
        button.setBackground(REPORT_BLUE);

        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                new EmptyBorder(
                        12,
                        18,
                        12,
                        18
                )
        );

        button.addActionListener(
                event -> action.run()
        );

        return button;
    }

    /**
     * Creates the application footer.
     */
    private JPanel createFooterPanel() {

        JPanel footerPanel =
                new JPanel(
                        new BorderLayout()
                );

        footerPanel.setBackground(
                DARK_PURPLE
        );

        footerPanel.setBorder(
                new EmptyBorder(
                        9,
                        25,
                        9,
                        25
                )
        );

        JLabel lblBusiness =
                new JLabel(
                        "Sasa Fashions © 2026"
                );

        lblBusiness.setForeground(
                Color.WHITE
        );

        JLabel lblSystem =
                new JLabel(
                        "Tailoring Business Management System"
                );

        lblSystem.setForeground(
                new Color(
                        220,
                        215,
                        235
                )
        );

        footerPanel.add(
                lblBusiness,
                BorderLayout.WEST
        );

        footerPanel.add(
                lblSystem,
                BorderLayout.EAST
        );

        return footerPanel;
    }

    /**
     * Returns a readable role name.
     */
    private String getRoleName(
            String roleId
    ) {

        if ("ROLE-001".equals(roleId)) {
            return "Administrator";
        }

        if ("ROLE-002".equals(roleId)) {
            return "Manager";
        }

        if ("ROLE-003".equals(roleId)) {
            return "Cashier";
        }

        if ("ROLE-004".equals(roleId)) {
            return "Tailor";
        }

        if ("ROLE-005".equals(roleId)) {
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

        if (!SessionManager.isAdministrator()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Administrator permission is required.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        new UserForm().setVisible(true);
    }

    /**
     * Opens the password-change screen.
     */
    private void changePassword() {

        new ChangePasswordForm(
                SessionManager.getCurrentUser()
        ).setVisible(true);
    }

    /**
     * Logs out and returns to the login screen.
     */
    private void logout() {

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Do you want to log out?",
                        "Confirm Logout",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (answer
                != JOptionPane.YES_OPTION) {

            return;
        }

        SessionManager.logout();
        dispose();

        SwingUtilities.invokeLater(
                () ->
                        new LoginForm()
                                .setVisible(true)
        );
    }

    /**
     * Confirms and closes the application.
     */
    private void exitApplication() {

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Do you want to exit Sasa Fashions?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (answer
                == JOptionPane.YES_OPTION) {

            SessionManager.logout();
            System.exit(0);
        }
    }

    /**
     * Handles the window close button.
     */
    private void registerWindowClosing() {

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent event
                    ) {

                        exitApplication();
                    }
                }
        );
    }
}