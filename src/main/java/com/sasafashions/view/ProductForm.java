package com.sasafashions.view;

import com.sasafashions.dao.ProductDAO;
import com.sasafashions.model.Product;
import com.sasafashions.view.components.FormStyler;
import com.sasafashions.view.components.MessageDialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Provides the user interface for managing products.
 *
 * The form demonstrates HashMap by storing products
 * against their product IDs for quick retrieval.
 *
 * @author SASA Group
 * @version 1.0
 */
public class ProductForm extends JFrame {

    private final ProductDAO productDAO =
            new ProductDAO();

    /**
     * HashMap is the second major data structure.
     *
     * Key: product ID
     * Value: complete Product object
     */
    private final HashMap<String, Product> productMap =
            new HashMap<>();

    private final JTextField txtProductId =
            new JTextField(20);

    private final JTextField txtProductName =
            new JTextField(20);

    private final JTextArea txtDescription =
            new JTextArea(4, 20);

    private final JTextField txtPrice =
            new JTextField(20);

    private final JTextField txtQuantity =
            new JTextField(20);

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

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                        "Product ID",
                        "Product Name",
                        "Price",
                        "Quantity",
                        "Stock Value"
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

    private final JTable tblProducts =
            new JTable(tableModel);

    /**
     * Creates and prepares the Product Form.
     */
    public ProductForm() {

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

        prepareNewProduct();
        loadProductTable();
    }

    /**
     * Creates and arranges the Swing components.
     */
    private void initialiseForm() {

        setTitle(
                "Sasa Fashions - Product Management"
        );

        setSize(1050, 650);

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
                new JLabel("PRODUCT MANAGEMENT");

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
                "Product ID",
                txtProductId
        );

        addFormField(
                formPanel,
                constraints,
                1,
                "Product Name",
                txtProductName
        );

        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JScrollPane descriptionScrollPane =
                new JScrollPane(txtDescription);

        addFormField(
                formPanel,
                constraints,
                2,
                "Description",
                descriptionScrollPane
        );

        addFormField(
                formPanel,
                constraints,
                3,
                "Price",
                txtPrice
        );

        addFormField(
                formPanel,
                constraints,
                4,
                "Quantity",
                txtQuantity
        );

        txtProductId.setEditable(false);

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

        JPanel formSection =
                new JPanel(new BorderLayout());

        formSection.add(
                formPanel,
                BorderLayout.CENTER
        );

        formSection.add(
                buttonSection,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                formSection,
                BorderLayout.WEST
        );

        tblProducts.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tblProducts.setRowHeight(24);

        JScrollPane tableScrollPane =
                new JScrollPane(tblProducts);

        mainPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        setContentPane(mainPanel);
    }

    /**
     * Adds a labelled field to the form.
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
                event -> prepareNewProduct()
        );

        btnSave.addActionListener(
                event -> saveProduct()
        );

        btnFind.addActionListener(
                event -> findProduct()
        );

        btnEdit.addActionListener(
                event -> editProduct()
        );

        btnDelete.addActionListener(
                event -> deleteProduct()
        );

        btnFirst.addActionListener(
                event -> showFirstProduct()
        );

        btnPrevious.addActionListener(
                event -> showPreviousProduct()
        );

        btnNext.addActionListener(
                event -> showNextProduct()
        );

        btnLast.addActionListener(
                event -> showLastProduct()
        );

        btnClose.addActionListener(
                event -> closeForm()
        );

        tblProducts
                .getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        displaySelectedProduct();
                    }
                });
    }

    /**
     * Clears the form and generates a new product ID.
     */
    private void prepareNewProduct() {

        txtProductName.setText("");
        txtDescription.setText("");
        txtPrice.setText("0.00");
        txtQuantity.setText("0");

        try {

            String productId =
                    productDAO.generateNextProductId();

            txtProductId.setText(productId);

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to generate product ID.",
                    error
            );

            txtProductId.setText("");
        }

        tblProducts.clearSelection();
        txtProductName.requestFocus();
    }

    /**
     * Validates the form and creates a Product object.
     *
     * @return Product, or null when validation fails
     */
    private Product readProductFromForm() {

        String productId =
                txtProductId.getText().trim();

        String productName =
                txtProductName.getText().trim();

        String description =
                txtDescription.getText().trim();

        if (productId.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "A product ID has not been generated.",
                    "Missing Product ID"
            );

            return null;
        }

        if (productName.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Please enter the product name.",
                    "Missing Product Name"
            );

            txtProductName.requestFocus();
            return null;
        }

        BigDecimal price;

        try {

            String enteredPrice =
                    txtPrice
                            .getText()
                            .trim()
                            .replace(",", "");

            price = new BigDecimal(enteredPrice)
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            if (price.compareTo(BigDecimal.ZERO) < 0) {

                MessageDialog.showWarning(
                        this,
                        "Price cannot be negative.",
                        "Invalid Price"
                );

                txtPrice.requestFocus();
                return null;
            }

        } catch (NumberFormatException error) {

            MessageDialog.showWarning(
                    this,
                    "Enter a valid product price.",
                    "Invalid Price"
            );

            txtPrice.requestFocus();
            return null;
        }

        int quantity;

        try {

            quantity = Integer.parseInt(
                    txtQuantity.getText().trim()
            );

            if (quantity < 0) {

                MessageDialog.showWarning(
                        this,
                        "Quantity cannot be negative.",
                        "Invalid Quantity"
                );

                txtQuantity.requestFocus();
                return null;
            }

        } catch (NumberFormatException error) {

            MessageDialog.showWarning(
                    this,
                    "Enter a valid whole-number quantity.",
                    "Invalid Quantity"
            );

            txtQuantity.requestFocus();
            return null;
        }

        return new Product(
                productId,
                productName,
                description,
                price,
                quantity
        );
    }

    /**
     * Saves a new product.
     */
    private void saveProduct() {

        Product product =
                readProductFromForm();

        if (product == null) {
            return;
        }

        try {

            boolean saved =
                    productDAO.saveProduct(product);

            if (saved) {

                MessageDialog.showInformation(
                        this,
                        "Product saved successfully.",
                        "Success"
                );

                loadProductTable();
                prepareNewProduct();
            }

        } catch (SQLException error) {

            if (error.getErrorCode() == 1062) {

                MessageDialog.showWarning(
                        this,
                        "The product ID already exists.",
                        "Duplicate Product"
                );

            } else {

                showDatabaseError(
                        "Unable to save the product.",
                        error
                );
            }
        }
    }

    /**
     * Finds a product using its product ID.
     */
    private void findProduct() {

        String productId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the product ID:",
                        "Find Product",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (productId == null) {
            return;
        }

        productId =
                productId.trim().toUpperCase();

        if (productId.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Please enter a product ID.",
                    "Missing Product ID"
            );

            return;
        }

        /*
         * First check the HashMap. This is faster than
         * querying the database again.
         */
        Product product =
                productMap.get(productId);

        if (product != null) {

            displayProduct(product);
            return;
        }

        try {

            product =
                    productDAO.findProduct(productId);

            if (product != null) {

                productMap.put(
                        product.getProductId(),
                        product
                );

                displayProduct(product);

            } else {

                MessageDialog.showInformation(
                        this,
                        "No product was found with ID: "
                                + productId,
                        "Product Not Found"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to find the product.",
                    error
            );
        }
    }

    /**
     * Updates the displayed product.
     */
    private void editProduct() {

        Product product =
                readProductFromForm();

        if (product == null) {
            return;
        }

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Save changes to product "
                                + product.getProductId()
                                + "?",
                        "Confirm Edit"
                );

        if (!confirmed) {
            return;
        }

        try {

            boolean updated =
                    productDAO.updateProduct(product);

            if (updated) {

                MessageDialog.showInformation(
                        this,
                        "Product updated successfully.",
                        "Success"
                );

                loadProductTable();
                displayProduct(product);

            } else {

                MessageDialog.showWarning(
                        this,
                        "The product was not found.",
                        "Update Failed"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to update the product.",
                    error
            );
        }
    }

    /**
     * Deletes the displayed product.
     */
    private void deleteProduct() {

        String productId =
                txtProductId.getText().trim();

        String productName =
                txtProductName.getText().trim();

        if (productId.isEmpty()
                || productName.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Find or select a product "
                            + "before deleting.",
                    "No Product Selected"
            );

            return;
        }

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Delete this product?\n\n"
                                + productId
                                + " - "
                                + productName,
                        "Confirm Deletion"
                );

        if (!confirmed) {
            return;
        }

        try {

            boolean deleted =
                    productDAO.deleteProduct(productId);

            if (deleted) {

                MessageDialog.showInformation(
                        this,
                        "Product deleted successfully.",
                        "Success"
                );

                loadProductTable();
                prepareNewProduct();

            } else {

                MessageDialog.showWarning(
                        this,
                        "The product was not found.",
                        "Delete Failed"
                );
            }

        } catch (SQLException error) {

            if (error.getErrorCode() == 1451) {

                MessageDialog.showWarning(
                        this,
                        "This product cannot be deleted "
                                + "because it is used by an order.",
                        "Product In Use"
                );

            } else {

                showDatabaseError(
                        "Unable to delete the product.",
                        error
                );
            }
        }
    }

    /**
     * Loads all products into the HashMap and JTable.
     */
    private void loadProductTable() {

        try {

            ArrayList<Product> products =
                    productDAO.findAllProducts();

            productMap.clear();
            tableModel.setRowCount(0);

            for (Product product : products) {

                productMap.put(
                        product.getProductId(),
                        product
                );

                tableModel.addRow(
                        new Object[]{
                            product.getProductId(),
                            product.getProductName(),
                            product.getPrice(),
                            product.getQuantity(),
                            product.calculateStockValue()
                        }
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to load product records.",
                    error
            );
        }
    }

    /**
     * Retrieves the selected product from the HashMap.
     */
    private void displaySelectedProduct() {

        int selectedRow =
                tblProducts.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        String productId =
                tableModel
                        .getValueAt(selectedRow, 0)
                        .toString();

        Product product =
                productMap.get(productId);

        if (product != null) {
            displayProduct(product);
        }
    }

    /**
     * Displays a Product object on the form.
     *
     * @param product product to display
     */
    private void displayProduct(Product product) {

        txtProductId.setText(
                product.getProductId()
        );

        txtProductName.setText(
                product.getProductName()
        );

        txtDescription.setText(
                product.getDescription() == null
                        ? ""
                        : product.getDescription()
        );

        txtPrice.setText(
                product.getPrice().toPlainString()
        );

        txtQuantity.setText(
                String.valueOf(
                        product.getQuantity()
                )
        );
    }

    private void showFirstProduct() {

        try {

            displayNavigationResult(
                    productDAO.getFirstProduct(),
                    "There are no products to display."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate products.",
                    error
            );
        }
    }

    private void showLastProduct() {

        try {

            displayNavigationResult(
                    productDAO.getLastProduct(),
                    "There are no products to display."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate products.",
                    error
            );
        }
    }

    private void showNextProduct() {

        String currentId =
                txtProductId.getText().trim();

        if (currentId.isEmpty()) {
            showFirstProduct();
            return;
        }

        try {

            displayNavigationResult(
                    productDAO.getNextProduct(currentId),
                    "You have reached the last product."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate products.",
                    error
            );
        }
    }

    private void showPreviousProduct() {

        String currentId =
                txtProductId.getText().trim();

        if (currentId.isEmpty()) {
            showLastProduct();
            return;
        }

        try {

            displayNavigationResult(
                    productDAO.getPreviousProduct(
                            currentId
                    ),
                    "You have reached the first product."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate products.",
                    error
            );
        }
    }

    /**
     * Displays a navigation result.
     *
     * @param product returned product
     * @param emptyMessage message when none is returned
     */
    private void displayNavigationResult(
            Product product,
            String emptyMessage
    ) {

        if (product != null) {

            displayProduct(product);

        } else {

            MessageDialog.showInformation(
                    this,
                    emptyMessage,
                    "Product Navigation"
            );
        }
    }

    /**
     * Displays a standard database error.
     *
     * @param message user-friendly message
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
     * Requests confirmation before closing.
     */
    private void closeForm() {

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Do you want to close "
                                + "Product Management?",
                        "Confirm Close"
                );

        if (confirmed) {
            dispose();
        }
    }

    /**
     * Starts the Product Form.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(
                () -> new ProductForm()
                        .setVisible(true)
        );
    }
}