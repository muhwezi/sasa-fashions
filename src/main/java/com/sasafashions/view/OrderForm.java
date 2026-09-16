package com.sasafashions.view;

import com.sasafashions.dao.CustomerDAO;
import com.sasafashions.dao.EmployeeDAO;
import com.sasafashions.dao.OrderDAO;
import com.sasafashions.dao.ProductDAO;
import com.sasafashions.model.Customer;
import com.sasafashions.model.Employee;
import com.sasafashions.model.Order;
import com.sasafashions.model.OrderDetail;
import com.sasafashions.model.Product;
import com.sasafashions.model.shared.OrderQueue;
import com.sasafashions.view.components.FormStyler;
import com.sasafashions.view.components.MessageDialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
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
 * Provides the interface for creating and managing
 * tailoring orders.
 *
 * @author HP
 * @version 1.0
 */
public class OrderForm extends JFrame {

    private final OrderDAO orderDAO =
            new OrderDAO();

    private final CustomerDAO customerDAO =
            new CustomerDAO();

    private final EmployeeDAO employeeDAO =
            new EmployeeDAO();

    private final ProductDAO productDAO =
            new ProductDAO();

    /*
     * The third required data structure.
     */
    private final OrderQueue orderQueue =
            new OrderQueue();

    /*
     * Temporarily stores product lines before the
     * complete order is saved.
     */
    private ArrayList<OrderDetail> orderDetails =
            new ArrayList<>();

    private final JTextField txtOrderId =
            new JTextField(15);

    private final JComboBox<Customer> cmbCustomer =
            new JComboBox<>();

    private final JComboBox<Employee> cmbEmployee =
            new JComboBox<>();

    private final JTextField txtOrderDate =
            new JTextField(12);

    private final JTextField txtDueDate =
            new JTextField(12);

    private final JComboBox<String> cmbStatus =
            new JComboBox<>(
                    new String[]{
                        "Pending",
                        "In Progress",
                        "Ready",
                        "Collected",
                        "Cancelled"
                    }
            );

    private final JComboBox<Product> cmbProduct =
            new JComboBox<>();

    private final JTextField txtQuantity =
            new JTextField("1", 8);

    private final JTextField txtUnitPrice =
            new JTextField(12);

    private final JLabel lblTotal =
            new JLabel("UGX 0.00");

    private final JLabel lblQueue =
            new JLabel("Active order queue: 0");

    private final JButton btnAddItem =
            new JButton("ADD ITEM");

    private final JButton btnRemoveItem =
            new JButton("REMOVE ITEM");

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

    private final JButton btnViewNextJob =
            new JButton("VIEW NEXT JOB");

    private final JButton btnClose =
            new JButton("CLOSE");

    private final DefaultTableModel detailTableModel =
            new DefaultTableModel(
                    new Object[]{
                        "Line",
                        "Product ID",
                        "Product",
                        "Quantity",
                        "Unit Price",
                        "Subtotal"
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

    private final JTable tblOrderDetails =
            new JTable(detailTableModel);

    /**
     * Creates and prepares the Order Form.
     */
    public OrderForm() {

        initialiseForm();
        attachEvents();

        FormStyler.prepareFrame(this);

        FormStyler.styleButtons(
                btnAddItem,
                btnRemoveItem,
                btnNew,
                btnSave,
                btnFind,
                btnEdit,
                btnDelete,
                btnFirst,
                btnPrevious,
                btnNext,
                btnLast,
                btnViewNextJob,
                btnClose
        );

        loadReferenceData();
        prepareNewOrder();
        refreshOrderQueue();
    }

    /**
     * Creates and arranges the interface.
     */
    private void initialiseForm() {

        setTitle(
                "Sasa Fashions - Order Management"
        );

        setSize(1250, 750);

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                new EmptyBorder(15, 20, 15, 20)
        );

        JLabel lblHeading =
                new JLabel("SASA FASHIONS");

        lblHeading.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        JLabel lblSubheading =
                new JLabel("ORDER MANAGEMENT");

        lblSubheading.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        JPanel titlePanel =
                new JPanel(new BorderLayout());

        titlePanel.add(
                lblHeading,
                BorderLayout.NORTH
        );

        titlePanel.add(
                lblSubheading,
                BorderLayout.CENTER
        );

        titlePanel.add(
                lblQueue,
                BorderLayout.EAST
        );

        mainPanel.add(
                titlePanel,
                BorderLayout.NORTH
        );

        JPanel orderPanel =
                new JPanel(new GridBagLayout());

        orderPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Order Information"
                )
        );

        GridBagConstraints constraints =
                createConstraints();

        addFormField(
                orderPanel,
                constraints,
                0,
                "Order ID",
                txtOrderId
        );

        addFormField(
                orderPanel,
                constraints,
                1,
                "Customer",
                cmbCustomer
        );

        addFormField(
                orderPanel,
                constraints,
                2,
                "Employee",
                cmbEmployee
        );

        addFormField(
                orderPanel,
                constraints,
                3,
                "Order Date",
                txtOrderDate
        );

        addFormField(
                orderPanel,
                constraints,
                4,
                "Due Date",
                txtDueDate
        );

        addFormField(
                orderPanel,
                constraints,
                5,
                "Status",
                cmbStatus
        );

        txtOrderId.setEditable(false);

        JPanel itemEntryPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT
                ));

        itemEntryPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Add Product to Order"
                )
        );

        itemEntryPanel.add(
                new JLabel("Product")
        );

        itemEntryPanel.add(cmbProduct);

        itemEntryPanel.add(
                new JLabel("Quantity")
        );

        itemEntryPanel.add(txtQuantity);

        itemEntryPanel.add(
                new JLabel("Unit Price")
        );

        txtUnitPrice.setEditable(false);
        itemEntryPanel.add(txtUnitPrice);

        itemEntryPanel.add(btnAddItem);
        itemEntryPanel.add(btnRemoveItem);

        JPanel leftPanel =
                new JPanel(new BorderLayout());

        leftPanel.add(
                orderPanel,
                BorderLayout.CENTER
        );

        leftPanel.add(
                itemEntryPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                leftPanel,
                BorderLayout.WEST
        );

        tblOrderDetails.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tblOrderDetails.setRowHeight(24);

        JPanel tablePanel =
                new JPanel(new BorderLayout());

        tablePanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Order Items"
                )
        );

        tablePanel.add(
                new JScrollPane(tblOrderDetails),
                BorderLayout.CENTER
        );

        JPanel totalPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT
                ));

        JLabel lblTotalTitle =
                new JLabel("ORDER TOTAL:");

        lblTotalTitle.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        totalPanel.add(lblTotalTitle);
        totalPanel.add(lblTotal);

        tablePanel.add(
                totalPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                tablePanel,
                BorderLayout.CENTER
        );

        JPanel actionButtons =
                new JPanel(new FlowLayout());

        actionButtons.add(btnNew);
        actionButtons.add(btnSave);
        actionButtons.add(btnFind);
        actionButtons.add(btnEdit);
        actionButtons.add(btnDelete);

        JPanel navigationButtons =
                new JPanel(new FlowLayout());

        navigationButtons.add(btnFirst);
        navigationButtons.add(btnPrevious);
        navigationButtons.add(btnNext);
        navigationButtons.add(btnLast);
        navigationButtons.add(btnViewNextJob);
        navigationButtons.add(btnClose);

        JPanel bottomPanel =
                new JPanel(new BorderLayout());

        bottomPanel.add(
                actionButtons,
                BorderLayout.NORTH
        );

        bottomPanel.add(
                navigationButtons,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        setContentPane(mainPanel);
    }

    private GridBagConstraints createConstraints() {

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(5, 5, 5, 5);

        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        return constraints;
    }

    private void addFormField(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String label,
            java.awt.Component component
    ) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;

        panel.add(
                new JLabel(label),
                constraints
        );

        constraints.gridx = 1;
        constraints.weightx = 1;

        panel.add(component, constraints);
    }

    /**
     * Connects components to their actions.
     */
    private void attachEvents() {

        cmbProduct.addActionListener(
                event -> displaySelectedProductPrice()
        );

        btnAddItem.addActionListener(
                event -> addOrderItem()
        );

        btnRemoveItem.addActionListener(
                event -> removeOrderItem()
        );

        btnNew.addActionListener(
                event -> prepareNewOrder()
        );

        btnSave.addActionListener(
                event -> saveOrder()
        );

        btnFind.addActionListener(
                event -> findOrder()
        );

        btnEdit.addActionListener(
                event -> editOrder()
        );

        btnDelete.addActionListener(
                event -> deleteOrder()
        );

        btnFirst.addActionListener(
                event -> showFirstOrder()
        );

        btnPrevious.addActionListener(
                event -> showPreviousOrder()
        );

        btnNext.addActionListener(
                event -> showNextOrder()
        );

        btnLast.addActionListener(
                event -> showLastOrder()
        );

        btnViewNextJob.addActionListener(
                event -> viewNextQueuedOrder()
        );

        btnClose.addActionListener(
                event -> closeForm()
        );
    }

    /**
     * Loads customers, employees and products.
     */
    private void loadReferenceData() {

        try {

            cmbCustomer.removeAllItems();

            for (Customer customer
                    : customerDAO.findAllCustomers()) {

                cmbCustomer.addItem(customer);
            }

            cmbEmployee.removeAllItems();

            for (Employee employee
                    : employeeDAO.findAllEmployees()) {

                cmbEmployee.addItem(employee);
            }

            cmbProduct.removeAllItems();

            for (Product product
                    : productDAO.findAllProducts()) {

                cmbProduct.addItem(product);
            }

            displaySelectedProductPrice();

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to load order selections.",
                    error
            );
        }
    }

    /**
     * Clears the form and prepares a new order.
     */
    private void prepareNewOrder() {

        orderDetails = new ArrayList<>();

        detailTableModel.setRowCount(0);

        txtOrderDate.setText(
                LocalDate.now().toString()
        );

        txtDueDate.setText(
                LocalDate.now()
                        .plusDays(7)
                        .toString()
        );

        cmbStatus.setSelectedItem("Pending");
        txtQuantity.setText("1");

        try {

            txtOrderId.setText(
                    orderDAO.generateNextOrderId()
            );

        } catch (SQLException error) {

            txtOrderId.setText("");

            showDatabaseError(
                    "Unable to generate order ID.",
                    error
            );
        }

        if (cmbCustomer.getItemCount() > 0) {
            cmbCustomer.setSelectedIndex(0);
        }

        if (cmbEmployee.getItemCount() > 0) {
            cmbEmployee.setSelectedIndex(0);
        }

        if (cmbProduct.getItemCount() > 0) {
            cmbProduct.setSelectedIndex(0);
        }

        displaySelectedProductPrice();
        updateTotalDisplay();
    }

    /**
     * Displays the selected product's current price.
     */
    private void displaySelectedProductPrice() {

        Product product =
                (Product) cmbProduct.getSelectedItem();

        if (product == null) {

            txtUnitPrice.setText("");
            return;
        }

        txtUnitPrice.setText(
                product.getPrice().toPlainString()
        );
    }

    /**
     * Adds the selected product to the temporary order.
     */
    private void addOrderItem() {

        Product product =
                (Product) cmbProduct.getSelectedItem();

        if (product == null) {

            MessageDialog.showWarning(
                    this,
                    "Create a product before adding items.",
                    "No Product"
            );

            return;
        }

        int quantity;

        try {

            quantity = Integer.parseInt(
                    txtQuantity.getText().trim()
            );

            if (quantity <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException error) {

            MessageDialog.showWarning(
                    this,
                    "Quantity must be a whole number "
                            + "greater than zero.",
                    "Invalid Quantity"
            );

            txtQuantity.requestFocus();
            return;
        }

        OrderDetail detail =
                new OrderDetail(
                        null,
                        txtOrderId.getText().trim(),
                        product.getProductId(),
                        quantity,
                        product.getPrice()
                );

        orderDetails.add(detail);

        loadDetailTable();
        txtQuantity.setText("1");
    }

    /**
     * Removes the selected order item.
     */
    private void removeOrderItem() {

        int selectedRow =
                tblOrderDetails.getSelectedRow();

        if (selectedRow < 0) {

            MessageDialog.showWarning(
                    this,
                    "Select an item to remove.",
                    "No Item Selected"
            );

            return;
        }

        orderDetails.remove(selectedRow);
        loadDetailTable();
    }

    /**
     * Displays temporary order details in the JTable.
     */
    private void loadDetailTable() {

        detailTableModel.setRowCount(0);

        int lineNumber = 1;

        for (OrderDetail detail : orderDetails) {

            detailTableModel.addRow(
                    new Object[]{
                        lineNumber,
                        detail.getProductId(),
                        findProductName(
                                detail.getProductId()
                        ),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.calculateSubtotal()
                    }
            );

            lineNumber++;
        }

        updateTotalDisplay();
    }

    private String findProductName(String productId) {

        for (int index = 0;
                index < cmbProduct.getItemCount();
                index++) {

            Product product =
                    cmbProduct.getItemAt(index);

            if (product
                    .getProductId()
                    .equals(productId)) {

                return product.getProductName();
            }
        }

        return productId;
    }

    private BigDecimal calculateOrderTotal() {

        BigDecimal total =
                BigDecimal.ZERO;

        for (OrderDetail detail : orderDetails) {

            total = total.add(
                    detail.calculateSubtotal()
            );
        }

        return total;
    }

    private void updateTotalDisplay() {

        lblTotal.setText(
                "UGX "
                        + calculateOrderTotal()
                                .toPlainString()
        );
    }

    /**
     * Reads and validates the main order fields.
     */
    private Order readOrderFromForm() {

        String orderId =
                txtOrderId.getText().trim();

        Customer customer =
                (Customer) cmbCustomer
                        .getSelectedItem();

        Employee employee =
                (Employee) cmbEmployee
                        .getSelectedItem();

        if (orderId.isEmpty()
                || customer == null
                || employee == null) {

            MessageDialog.showWarning(
                    this,
                    "Select a customer and employee.",
                    "Missing Information"
            );

            return null;
        }

        LocalDate orderDate;
        LocalDate dueDate;

        try {

            orderDate = LocalDate.parse(
                    txtOrderDate.getText().trim()
            );

            dueDate = LocalDate.parse(
                    txtDueDate.getText().trim()
            );

        } catch (DateTimeParseException error) {

            MessageDialog.showWarning(
                    this,
                    "Enter dates in YYYY-MM-DD format.",
                    "Invalid Date"
            );

            return null;
        }

        if (dueDate.isBefore(orderDate)) {

            MessageDialog.showWarning(
                    this,
                    "Due date cannot be before order date.",
                    "Invalid Due Date"
            );

            return null;
        }

        return new Order(
                orderId,
                customer.getCustomerId(),
                employee.getEmployeeId(),
                orderDate,
                dueDate,
                cmbStatus
                        .getSelectedItem()
                        .toString(),
                calculateOrderTotal()
        );
    }

    /**
     * Saves the complete order.
     */
    private void saveOrder() {

        Order order =
                readOrderFromForm();

        if (order == null) {
            return;
        }

        if (orderDetails.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Add at least one product "
                            + "to the order.",
                    "Empty Order"
            );

            return;
        }

        try {

            orderDAO.saveOrderWithDetails(
                    order,
                    orderDetails
            );

            MessageDialog.showInformation(
                    this,
                    "Order saved successfully.",
                    "Success"
            );

            refreshOrderQueue();
            prepareNewOrder();

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to save the order.",
                    error
            );

        } catch (IllegalArgumentException error) {

            MessageDialog.showWarning(
                    this,
                    error.getMessage(),
                    "Invalid Order"
            );
        }
    }

    /**
     * Finds and displays an order.
     */
    private void findOrder() {

        String orderId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the order ID:",
                        "Find Order",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (orderId == null) {
            return;
        }

        orderId = orderId.trim().toUpperCase();

        try {

            Order order =
                    orderDAO.findOrder(orderId);

            if (order != null) {

                displayOrder(order);

            } else {

                MessageDialog.showInformation(
                        this,
                        "No order was found with ID: "
                                + orderId,
                        "Order Not Found"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to find the order.",
                    error
            );
        }
    }

    /**
     * Updates the order and replaces its details.
     */
    private void editOrder() {

        Order order =
                readOrderFromForm();

        if (order == null
                || orderDetails.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "The order must contain at least "
                            + "one product.",
                    "Invalid Order"
            );

            return;
        }

        if (!MessageDialog.confirm(
                this,
                "Save changes to "
                        + order.getOrderId()
                        + "?",
                "Confirm Edit"
        )) {
            return;
        }

        try {

            boolean updated =
                    orderDAO.updateOrderWithDetails(
                            order,
                            orderDetails
                    );

            if (updated) {

                MessageDialog.showInformation(
                        this,
                        "Order updated successfully.",
                        "Success"
                );

                refreshOrderQueue();

            } else {

                MessageDialog.showWarning(
                        this,
                        "The order was not found.",
                        "Update Failed"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to update the order.",
                    error
            );
        }
    }

    /**
     * Deletes the displayed order.
     */
    private void deleteOrder() {

        String orderId =
                txtOrderId.getText().trim();

        if (orderId.isEmpty()) {
            return;
        }

        if (!MessageDialog.confirm(
                this,
                "Delete order " + orderId + "?\n"
                        + "Its order items will also be deleted.",
                "Confirm Deletion"
        )) {
            return;
        }

        try {

            boolean deleted =
                    orderDAO.deleteOrder(orderId);

            if (deleted) {

                MessageDialog.showInformation(
                        this,
                        "Order deleted successfully.",
                        "Success"
                );

                refreshOrderQueue();
                prepareNewOrder();

            } else {

                MessageDialog.showWarning(
                        this,
                        "The order was not found.",
                        "Delete Failed"
                );
            }

        } catch (SQLException error) {

            if (error.getErrorCode() == 1451) {

                MessageDialog.showWarning(
                        this,
                        "This order cannot be deleted "
                                + "because payments exist.",
                        "Order In Use"
                );

            } else {

                showDatabaseError(
                        "Unable to delete the order.",
                        error
                );
            }
        }
    }

    /**
     * Displays an order and loads its details.
     */
    private void displayOrder(Order order) {

        txtOrderId.setText(order.getOrderId());

        selectCustomer(order.getCustomerId());
        selectEmployee(order.getEmployeeId());

        txtOrderDate.setText(
                order.getOrderDate().toString()
        );

        txtDueDate.setText(
                order.getDueDate().toString()
        );

        cmbStatus.setSelectedItem(
                order.getOrderStatus()
        );

        try {

            orderDetails =
                    orderDAO.findOrderDetails(
                            order.getOrderId()
                    );

            loadDetailTable();

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to load order items.",
                    error
            );
        }
    }

    private void selectCustomer(String customerId) {

        for (int index = 0;
                index < cmbCustomer.getItemCount();
                index++) {

            Customer customer =
                    cmbCustomer.getItemAt(index);

            if (customer.getCustomerId()
                    .equals(customerId)) {

                cmbCustomer.setSelectedIndex(index);
                return;
            }
        }
    }

    private void selectEmployee(String employeeId) {

        for (int index = 0;
                index < cmbEmployee.getItemCount();
                index++) {

            Employee employee =
                    cmbEmployee.getItemAt(index);

            if (employee.getEmployeeId()
                    .equals(employeeId)) {

                cmbEmployee.setSelectedIndex(index);
                return;
            }
        }
    }

    /**
     * Loads active orders into Queue<Order>.
     */
    private void refreshOrderQueue() {

        try {

            orderQueue.loadOrders(
                    orderDAO.findActiveOrders()
            );

            lblQueue.setText(
                    "Active order queue: "
                            + orderQueue.size()
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to load the order queue.",
                    error
            );
        }
    }

    /**
     * Displays the first active order in the queue.
     */
    private void viewNextQueuedOrder() {

        Order nextOrder =
                orderQueue.viewNextOrder();

        if (nextOrder == null) {

            MessageDialog.showInformation(
                    this,
                    "There are no active orders.",
                    "Order Queue"
            );

            return;
        }

        displayOrder(nextOrder);
    }

    private void showFirstOrder() {

        try {

            displayNavigationResult(
                    orderDAO.getFirstOrder(),
                    "There are no orders."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate orders.",
                    error
            );
        }
    }

    private void showLastOrder() {

        try {

            displayNavigationResult(
                    orderDAO.getLastOrder(),
                    "There are no orders."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate orders.",
                    error
            );
        }
    }

    private void showNextOrder() {

        try {

            displayNavigationResult(
                    orderDAO.getNextOrder(
                            txtOrderId
                                    .getText()
                                    .trim()
                    ),
                    "You have reached the last order."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate orders.",
                    error
            );
        }
    }

    private void showPreviousOrder() {

        try {

            displayNavigationResult(
                    orderDAO.getPreviousOrder(
                            txtOrderId
                                    .getText()
                                    .trim()
                    ),
                    "You have reached the first order."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate orders.",
                    error
            );
        }
    }

    private void displayNavigationResult(
            Order order,
            String message
    ) {

        if (order != null) {

            displayOrder(order);

        } else {

            MessageDialog.showInformation(
                    this,
                    message,
                    "Order Navigation"
            );
        }
    }

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

    private void closeForm() {

        if (MessageDialog.confirm(
                this,
                "Do you want to close "
                        + "Order Management?",
                "Confirm Close"
        )) {
            dispose();
        }
    }

    /**
     * Starts the Order Form.
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(
                () -> new OrderForm()
                        .setVisible(true)
        );
    }
}