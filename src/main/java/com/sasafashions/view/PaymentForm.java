package com.sasafashions.view;

import com.sasafashions.dao.EmployeeDAO;
import com.sasafashions.dao.OrderDAO;
import com.sasafashions.dao.PaymentDAO;
import com.sasafashions.model.Employee;
import com.sasafashions.model.Order;
import com.sasafashions.model.Payment;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentForm extends JFrame {

    private final PaymentDAO paymentDAO;
    private final OrderDAO orderDAO;
    private final EmployeeDAO employeeDAO;

    /*
     * Maps are used to connect the text displayed in
     * the combo boxes to the actual Java objects.
     */
    private final Map<String, Order> orderMap;
    private final Map<String, Employee> employeeMap;

    private JTextField paymentIdField;
    private JComboBox<String> orderComboBox;
    private JComboBox<String> employeeComboBox;
    private JTextField paymentDateField;
    private JTextField amountField;
    private JComboBox<String> paymentMethodComboBox;
    private JTextField referenceNumberField;
    private JTextArea notesArea;

    private JLabel orderTotalValueLabel;
    private JLabel totalPaidValueLabel;
    private JLabel outstandingValueLabel;

    private JTable paymentTable;
    private DefaultTableModel tableModel;

    private LocalDateTime selectedPaymentDate;

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );

    public PaymentForm() {

        paymentDAO = new PaymentDAO();
        orderDAO = new OrderDAO();
        employeeDAO = new EmployeeDAO();

        orderMap = new LinkedHashMap<>();
        employeeMap = new LinkedHashMap<>();

        initializeFrame();
        initializeComponents();
        registerEvents();

        try {
            loadReferenceData();
            refreshPaymentTable();
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void initializeFrame() {

        setTitle("Sasa Fashions - Payment Management");

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setSize(1250, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void initializeComponents() {

        add(createHeadingPanel(), BorderLayout.NORTH);

        JPanel centrePanel =
                new JPanel(new BorderLayout(10, 10));

        centrePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        0,
                        15
                )
        );

        centrePanel.add(
                createFormPanel(),
                BorderLayout.NORTH
        );

        centrePanel.add(
                createTablePanel(),
                BorderLayout.CENTER
        );

        add(centrePanel, BorderLayout.CENTER);

        add(
                createButtonPanel(),
                BorderLayout.SOUTH
        );
    }

    private JPanel createHeadingPanel() {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(
                new Color(72, 45, 145)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );

        JLabel headingLabel =
                new JLabel("SASA FASHIONS");

        headingLabel.setForeground(Color.WHITE);

        headingLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        JLabel subHeadingLabel =
                new JLabel("PAYMENT MANAGEMENT");

        subHeadingLabel.setForeground(
                new Color(230, 225, 255)
        );

        subHeadingLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        panel.add(
                headingLabel,
                BorderLayout.WEST
        );

        panel.add(
                subHeadingLabel,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel createFormPanel() {

        JPanel container =
                new JPanel(new BorderLayout(15, 10));

        container.setBorder(
                BorderFactory.createTitledBorder(
                        "Payment Information"
                )
        );

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        paymentIdField = new JTextField(18);
        paymentIdField.setEditable(false);

        orderComboBox = new JComboBox<>();
        employeeComboBox = new JComboBox<>();

        paymentDateField = new JTextField(18);
        paymentDateField.setEditable(false);

        amountField = new JTextField(18);

        paymentMethodComboBox =
                new JComboBox<>(
                        new String[]{
                                "Cash",
                                "Mobile Money",
                                "Bank Transfer",
                                "Card",
                                "Cheque"
                        }
                );

        referenceNumberField =
                new JTextField(18);

        notesArea = new JTextArea(3, 18);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        addFormComponent(
                formPanel,
                "Payment ID",
                paymentIdField,
                0,
                0
        );

        addFormComponent(
                formPanel,
                "Order",
                orderComboBox,
                0,
                2
        );

        addFormComponent(
                formPanel,
                "Received By",
                employeeComboBox,
                1,
                0
        );

        addFormComponent(
                formPanel,
                "Payment Date",
                paymentDateField,
                1,
                2
        );

        addFormComponent(
                formPanel,
                "Amount",
                amountField,
                2,
                0
        );

        addFormComponent(
                formPanel,
                "Payment Method",
                paymentMethodComboBox,
                2,
                2
        );

        addFormComponent(
                formPanel,
                "Reference Number",
                referenceNumberField,
                3,
                0
        );

        JScrollPane notesScrollPane =
                new JScrollPane(notesArea);

        addFormComponent(
                formPanel,
                "Notes",
                notesScrollPane,
                3,
                2
        );

        container.add(
                formPanel,
                BorderLayout.CENTER
        );

        container.add(
                createBalancePanel(),
                BorderLayout.EAST
        );

        return container;
    }

    private void addFormComponent(
            JPanel panel,
            String labelText,
            java.awt.Component component,
            int row,
            int column
    ) {

        GridBagConstraints labelConstraints =
                new GridBagConstraints();

        labelConstraints.gridx = column;
        labelConstraints.gridy = row;
        labelConstraints.anchor =
                GridBagConstraints.WEST;

        labelConstraints.insets =
                new Insets(6, 8, 6, 8);

        JLabel label = new JLabel(labelText);
        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints =
                new GridBagConstraints();

        fieldConstraints.gridx = column + 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill =
                GridBagConstraints.HORIZONTAL;

        fieldConstraints.insets =
                new Insets(6, 8, 6, 20);

        panel.add(
                component,
                fieldConstraints
        );
    }

    private JPanel createBalancePanel() {

        JPanel balancePanel =
                new JPanel(new GridBagLayout());

        balancePanel.setBackground(
                new Color(245, 243, 252)
        );

        balancePanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Order Balance"
                )
        );

        orderTotalValueLabel =
                createMoneyLabel();

        totalPaidValueLabel =
                createMoneyLabel();

        outstandingValueLabel =
                createMoneyLabel();

        outstandingValueLabel.setForeground(
                new Color(180, 35, 35)
        );

        addBalanceRow(
                balancePanel,
                "Order Total:",
                orderTotalValueLabel,
                0
        );

        addBalanceRow(
                balancePanel,
                "Total Paid:",
                totalPaidValueLabel,
                1
        );

        addBalanceRow(
                balancePanel,
                "Outstanding:",
                outstandingValueLabel,
                2
        );

        return balancePanel;
    }

    private JLabel createMoneyLabel() {

        JLabel label = new JLabel("UGX 0.00");

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        return label;
    }

    private void addBalanceRow(
            JPanel panel,
            String labelText,
            JLabel valueLabel,
            int row
    ) {

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.anchor =
                GridBagConstraints.WEST;

        constraints.insets =
                new Insets(8, 10, 8, 10);

        panel.add(
                new JLabel(labelText),
                constraints
        );

        constraints.gridx = 1;

        panel.add(
                valueLabel,
                constraints
        );
    }

    private JScrollPane createTablePanel() {

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Payment ID",
                        "Order ID",
                        "Received By",
                        "Payment Date",
                        "Amount",
                        "Method",
                        "Reference",
                        "Notes"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);

        paymentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        paymentTable.setRowHeight(25);
        paymentTable.setAutoCreateRowSorter(true);

        paymentTable.getTableHeader().setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(paymentTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Recorded Payments"
                )
        );

        return scrollPane;
    }

    private JPanel createButtonPanel() {

        JPanel buttonPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.CENTER,
                        8,
                        12
                ));

        JButton newButton =
                createButton("New");

        JButton saveButton =
                createButton("Save");

        JButton findButton =
                createButton("Find");

        JButton editButton =
                createButton("Edit");

        JButton deleteButton =
                createButton("Delete");

        JButton firstButton =
                createButton("First");

        JButton previousButton =
                createButton("Previous");

        JButton nextButton =
                createButton("Next");

        JButton lastButton =
                createButton("Last");

        JButton refreshButton =
                createButton("Refresh");

        JButton closeButton =
                createButton("Close");

        newButton.addActionListener(
                event -> clearFormSafely()
        );

        saveButton.addActionListener(
                event -> savePayment()
        );

        findButton.addActionListener(
                event -> findPayment()
        );

        editButton.addActionListener(
                event -> updatePayment()
        );

        deleteButton.addActionListener(
                event -> deletePayment()
        );

        firstButton.addActionListener(
                event -> navigatePayment("FIRST")
        );

        previousButton.addActionListener(
                event -> navigatePayment("PREVIOUS")
        );

        nextButton.addActionListener(
                event -> navigatePayment("NEXT")
        );

        lastButton.addActionListener(
                event -> navigatePayment("LAST")
        );

        refreshButton.addActionListener(
                event -> refreshEverything()
        );

        closeButton.addActionListener(
                event -> dispose()
        );

        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(findButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(firstButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(lastButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFocusPainted(false);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        return button;
    }

    private void registerEvents() {

        orderComboBox.addActionListener(
                event -> refreshBalanceSafely()
        );

        paymentTable.getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        loadSelectedTablePayment();
                    }
                });
    }

    private void loadReferenceData()
            throws SQLException {

        loadOrders();
        loadEmployees();
    }

    private void loadOrders()
            throws SQLException {

        orderMap.clear();
        orderComboBox.removeAllItems();

        for (Order order
                : orderDAO.findAllOrders()) {

            String displayText =
                    order.getOrderId()
                    + " | Customer: "
                    + order.getCustomerId()
                    + " | Total: UGX "
                    + order.getTotalAmount();

            orderMap.put(displayText, order);
            orderComboBox.addItem(displayText);
        }
    }

    private void loadEmployees()
            throws SQLException {

        employeeMap.clear();
        employeeComboBox.removeAllItems();

        String emptyOption = "Not specified";

        employeeComboBox.addItem(emptyOption);
        employeeMap.put(emptyOption, null);

        for (Employee employee
                : employeeDAO.findAllEmployees()) {

            String displayText =
                    employee.getEmployeeId()
                    + " | "
                    + employee.getEmployeeName();

            employeeMap.put(
                    displayText,
                    employee
            );

            employeeComboBox.addItem(
                    displayText
            );
        }
    }

    private void refreshPaymentTable()
            throws SQLException {

        tableModel.setRowCount(0);

        for (Payment payment
                : paymentDAO.findAllPayments()) {

            tableModel.addRow(
                    new Object[]{
                            payment.getPaymentId(),
                            payment.getOrderId(),
                            payment.getReceivedBy(),
                            formatDate(
                                    payment.getPaymentDate()
                            ),
                            payment.getAmount(),
                            payment.getPaymentMethod(),
                            payment.getReferenceNumber(),
                            payment.getNotes()
                    }
            );
        }
    }

    private void refreshEverything() {

        try {
            loadReferenceData();
            refreshPaymentTable();
            clearForm();

            showInformation(
                    "Payment information refreshed."
            );

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void savePayment() {

        try {
            Payment payment =
                    collectPaymentFromForm();

            paymentDAO.savePayment(payment);

            showInformation(
                    "Payment saved successfully.\n"
                    + "Payment ID: "
                    + payment.getPaymentId()
            );

            refreshPaymentTable();
            clearForm();

        } catch (NumberFormatException exception) {

            showError(
                    "Enter a valid payment amount.\n"
                    + "Example: 50000"
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1062) {

                showError(
                        "The payment reference number "
                        + "has already been used."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    private void updatePayment() {

        String paymentId =
                paymentIdField.getText().trim();

        if (paymentId.isEmpty()) {
            showError(
                    "Find or select the payment "
                    + "you want to edit."
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Update payment " + paymentId + "?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Payment payment =
                    collectPaymentFromForm();

            paymentDAO.updatePayment(payment);

            showInformation(
                    "Payment updated successfully."
            );

            refreshPaymentTable();
            loadPayment(payment.getPaymentId());

        } catch (NumberFormatException exception) {

            showError(
                    "Enter a valid payment amount."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1062) {

                showError(
                        "The payment reference number "
                        + "has already been used."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    private void findPayment() {

        String paymentId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the Payment ID:",
                        "Find Payment",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (paymentId == null
                || paymentId.trim().isEmpty()) {
            return;
        }

        try {
            Payment payment =
                    paymentDAO.findPayment(
                            paymentId.trim()
                    );

            if (payment == null) {

                showError(
                        "Payment not found."
                );

                return;
            }

            displayPayment(payment);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void deletePayment() {

        String paymentId =
                paymentIdField.getText().trim();

        if (paymentId.isEmpty()) {

            showError(
                    "Find or select the payment "
                    + "you want to delete."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete payment " + paymentId + "?\n"
                + "This will increase the order's "
                + "outstanding balance.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted =
                    paymentDAO.deletePayment(
                            paymentId
                    );

            if (deleted) {

                showInformation(
                        "Payment deleted successfully."
                );

                refreshPaymentTable();
                clearForm();

            } else {

                showError(
                        "Payment was not found."
                );
            }

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void navigatePayment(
            String direction
    ) {

        try {
            Payment payment;

            String currentPaymentId =
                    paymentIdField.getText().trim();

            switch (direction) {

                case "FIRST" ->
                    payment =
                        paymentDAO.findFirstPayment();

                case "LAST" ->
                    payment =
                        paymentDAO.findLastPayment();

                case "NEXT" -> {

                    if (currentPaymentId.isEmpty()) {

                        payment =
                            paymentDAO.findFirstPayment();

                    } else {

                        payment =
                            paymentDAO.findNextPayment(
                                    currentPaymentId
                            );
                    }
                }

                case "PREVIOUS" -> {

                    if (currentPaymentId.isEmpty()) {

                        payment =
                            paymentDAO.findLastPayment();

                    } else {

                        payment =
                            paymentDAO.findPreviousPayment(
                                    currentPaymentId
                            );
                    }
                }

                default -> payment = null;
            }

            if (payment == null) {

                showInformation(
                        "There are no more payments "
                        + "in that direction."
                );

                return;
            }

            displayPayment(payment);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private Payment collectPaymentFromForm() {

        String selectedOrderText =
                (String) orderComboBox
                        .getSelectedItem();

        if (selectedOrderText == null) {

            throw new IllegalArgumentException(
                    "Please select an order."
            );
        }

        Order selectedOrder =
                orderMap.get(selectedOrderText);

        if (selectedOrder == null) {

            throw new IllegalArgumentException(
                    "The selected order is invalid."
            );
        }

        String selectedEmployeeText =
                (String) employeeComboBox
                        .getSelectedItem();

        Employee selectedEmployee =
                employeeMap.get(
                        selectedEmployeeText
                );

        String receivedBy = null;

        if (selectedEmployee != null) {
            receivedBy =
                    selectedEmployee.getEmployeeId();
        }

        String amountText =
                amountField.getText()
                        .trim()
                        .replace(",", "");

        if (amountText.isEmpty()) {

            throw new IllegalArgumentException(
                    "Enter the payment amount."
            );
        }

        BigDecimal amount =
                new BigDecimal(amountText);

        String paymentMethod =
                (String) paymentMethodComboBox
                        .getSelectedItem();

        Payment payment = new Payment();

        payment.setPaymentId(
                paymentIdField.getText().trim()
        );

        payment.setOrderId(
                selectedOrder.getOrderId()
        );

        payment.setReceivedBy(receivedBy);

        payment.setPaymentDate(
                selectedPaymentDate
        );

        payment.setAmount(amount);

        payment.setPaymentMethod(
                paymentMethod
        );

        payment.setReferenceNumber(
                referenceNumberField
                        .getText()
                        .trim()
        );

        payment.setNotes(
                notesArea.getText().trim()
        );

        return payment;
    }

    private void loadSelectedTablePayment() {

        int selectedRow =
                paymentTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow =
                paymentTable.convertRowIndexToModel(
                        selectedRow
                );

        String paymentId =
                tableModel.getValueAt(
                        modelRow,
                        0
                ).toString();

        try {
            loadPayment(paymentId);
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void loadPayment(
            String paymentId
    ) throws SQLException {

        Payment payment =
                paymentDAO.findPayment(paymentId);

        if (payment == null) {

            showError(
                    "Payment was not found."
            );

            return;
        }

        displayPayment(payment);
    }

    private void displayPayment(
            Payment payment
    ) {

        paymentIdField.setText(
                payment.getPaymentId()
        );

        selectOrder(
                payment.getOrderId()
        );

        selectEmployee(
                payment.getReceivedBy()
        );

        selectedPaymentDate =
                payment.getPaymentDate();

        paymentDateField.setText(
                formatDate(selectedPaymentDate)
        );

        amountField.setText(
                payment.getAmount().toPlainString()
        );

        paymentMethodComboBox.setSelectedItem(
                payment.getPaymentMethod()
        );

        referenceNumberField.setText(
                valueOrEmpty(
                        payment.getReferenceNumber()
                )
        );

        notesArea.setText(
                valueOrEmpty(
                        payment.getNotes()
                )
        );

        refreshBalanceSafely();
    }

    private void selectOrder(
            String orderId
    ) {

        for (int index = 0;
             index < orderComboBox.getItemCount();
             index++) {

            String displayText =
                    orderComboBox.getItemAt(index);

            Order order =
                    orderMap.get(displayText);

            if (order != null
                    && order.getOrderId()
                            .equals(orderId)) {

                orderComboBox.setSelectedIndex(
                        index
                );

                return;
            }
        }
    }

    private void selectEmployee(
            String employeeId
    ) {

        if (employeeId == null
                || employeeId.isBlank()) {

            employeeComboBox.setSelectedIndex(0);
            return;
        }

        for (int index = 0;
             index < employeeComboBox.getItemCount();
             index++) {

            String displayText =
                    employeeComboBox.getItemAt(index);

            Employee employee =
                    employeeMap.get(displayText);

            if (employee != null
                    && employee.getEmployeeId()
                            .equals(employeeId)) {

                employeeComboBox.setSelectedIndex(
                        index
                );

                return;
            }
        }

        employeeComboBox.setSelectedIndex(0);
    }

    private void refreshBalanceSafely() {

        try {
            refreshBalance();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void refreshBalance()
            throws SQLException {

        String selectedOrderText =
                (String) orderComboBox
                        .getSelectedItem();

        if (selectedOrderText == null) {
            resetBalanceLabels();
            return;
        }

        Order selectedOrder =
                orderMap.get(selectedOrderText);

        if (selectedOrder == null) {
            resetBalanceLabels();
            return;
        }

        String orderId =
                selectedOrder.getOrderId();

        BigDecimal orderTotal =
                paymentDAO.getOrderTotal(orderId);

        BigDecimal totalPaid =
                paymentDAO.getTotalPaid(orderId);

        BigDecimal outstanding =
                paymentDAO.getOutstandingBalance(
                        orderId
                );

        orderTotalValueLabel.setText(
                formatMoney(orderTotal)
        );

        totalPaidValueLabel.setText(
                formatMoney(totalPaid)
        );

        outstandingValueLabel.setText(
                formatMoney(outstanding)
        );

        if (outstanding.compareTo(
                BigDecimal.ZERO) <= 0) {

            outstandingValueLabel.setForeground(
                    new Color(20, 130, 70)
            );

        } else {

            outstandingValueLabel.setForeground(
                    new Color(180, 35, 35)
            );
        }
    }

    private void clearForm()
            throws SQLException {

        paymentIdField.setText(
                paymentDAO.generateNextPaymentId()
        );

        selectedPaymentDate =
                LocalDateTime.now();

        paymentDateField.setText(
                formatDate(selectedPaymentDate)
        );

        amountField.setText("");
        referenceNumberField.setText("");
        notesArea.setText("");

        paymentMethodComboBox.setSelectedIndex(0);

        if (orderComboBox.getItemCount() > 0) {
            orderComboBox.setSelectedIndex(0);
        }

        if (employeeComboBox.getItemCount() > 0) {
            employeeComboBox.setSelectedIndex(0);
        }

        paymentTable.clearSelection();

        refreshBalance();
        amountField.requestFocus();
    }

    private void clearFormSafely() {

        try {
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void resetBalanceLabels() {

        orderTotalValueLabel.setText("UGX 0.00");
        totalPaidValueLabel.setText("UGX 0.00");
        outstandingValueLabel.setText("UGX 0.00");
    }

    private String formatMoney(
            BigDecimal amount
    ) {

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        return String.format(
                "UGX %,.2f",
                amount
        );
    }

    private String formatDate(
            LocalDateTime dateTime
    ) {

        if (dateTime == null) {
            return "";
        }

        return dateTime.format(
                dateTimeFormatter
        );
    }

    private String valueOrEmpty(
            String value
    ) {

        return value == null ? "" : value;
    }

    private void showInformation(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Sasa Fashions",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showDatabaseError(
            SQLException exception
    ) {

        showError(
                "A database error occurred.\n"
                + exception.getMessage()
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PaymentForm form =
                    new PaymentForm();

            form.setVisible(true);
        });
    }
}