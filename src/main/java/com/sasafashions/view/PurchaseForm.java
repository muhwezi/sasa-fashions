package com.sasafashions.view;

import com.sasafashions.dao.EmployeeDAO;
import com.sasafashions.dao.MaterialDAO;
import com.sasafashions.dao.PurchaseDAO;
import com.sasafashions.dao.SupplierDAO;
import com.sasafashions.model.Employee;
import com.sasafashions.model.Material;
import com.sasafashions.model.Purchase;
import com.sasafashions.model.PurchaseDetail;
import com.sasafashions.model.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides the graphical interface for recording purchases.
 *
 * <p>The form allows a user to select a supplier, employee
 * and multiple materials. Saving a completed purchase
 * automatically updates material stock.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class PurchaseForm extends JFrame {

    private final PurchaseDAO purchaseDAO;
    private final SupplierDAO supplierDAO;
    private final EmployeeDAO employeeDAO;
    private final MaterialDAO materialDAO;

    private final Map<String, Supplier> supplierMap;
    private final Map<String, Employee> employeeMap;
    private final Map<String, Material> materialMap;

    private final ArrayList<PurchaseDetail> purchaseDetails;

    private JTextField purchaseIdField;
    private JComboBox<String> supplierComboBox;
    private JComboBox<String> employeeComboBox;
    private JTextField purchaseDateField;
    private JTextField invoiceNumberField;
    private JComboBox<String> purchaseStatusComboBox;
    private JTextArea notesArea;

    private JComboBox<String> materialComboBox;
    private JTextField quantityField;
    private JTextField unitCostField;

    private JTable detailTable;
    private DefaultTableModel detailTableModel;

    private JTable purchaseTable;
    private DefaultTableModel purchaseTableModel;

    private JLabel purchaseTotalLabel;
    private JLabel completedPurchasesLabel;

    /**
     * Creates and prepares the purchase-management form.
     */
    public PurchaseForm() {

        purchaseDAO = new PurchaseDAO();
        supplierDAO = new SupplierDAO();
        employeeDAO = new EmployeeDAO();
        materialDAO = new MaterialDAO();

        supplierMap = new LinkedHashMap<>();
        employeeMap = new LinkedHashMap<>();
        materialMap = new LinkedHashMap<>();

        purchaseDetails = new ArrayList<>();

        initializeFrame();
        initializeComponents();
        registerEvents();

        try {
            loadReferenceData();
            refreshPurchaseTable();
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Configures the purchase frame.
     */
    private void initializeFrame() {

        setTitle(
                "Sasa Fashions - Purchase Management"
        );

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setSize(1300, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Creates the components displayed on the screen.
     */
    private void initializeComponents() {

        add(
                createHeadingPanel(),
                BorderLayout.NORTH
        );

        JPanel centrePanel =
                new JPanel(new BorderLayout(10, 10));

        centrePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        12,
                        0,
                        12
                )
        );

        JPanel entryPanel =
                new JPanel(new BorderLayout(8, 8));

        entryPanel.add(
                createPurchaseInformationPanel(),
                BorderLayout.NORTH
        );

        entryPanel.add(
                createMaterialEntryPanel(),
                BorderLayout.CENTER
        );

        centrePanel.add(
                entryPanel,
                BorderLayout.NORTH
        );

        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        createDetailTablePanel(),
                        createPurchaseTablePanel()
                );

        splitPane.setResizeWeight(0.48);
        splitPane.setDividerLocation(230);

        centrePanel.add(
                splitPane,
                BorderLayout.CENTER
        );

        add(
                centrePanel,
                BorderLayout.CENTER
        );

        add(
                createButtonPanel(),
                BorderLayout.SOUTH
        );
    }

    /**
     * Creates the screen heading.
     *
     * @return heading panel
     */
    private JPanel createHeadingPanel() {

        JPanel panel =
                new JPanel(new BorderLayout());

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

        JLabel businessLabel =
                new JLabel("SASA FASHIONS");

        businessLabel.setForeground(Color.WHITE);

        businessLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        JLabel formLabel =
                new JLabel(
                        "MATERIAL PURCHASE MANAGEMENT"
                );

        formLabel.setForeground(
                new Color(230, 225, 255)
        );

        formLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        panel.add(
                businessLabel,
                BorderLayout.WEST
        );

        panel.add(
                formLabel,
                BorderLayout.EAST
        );

        return panel;
    }

    /**
     * Creates the general purchase-information fields.
     *
     * @return purchase-information panel
     */
    private JPanel createPurchaseInformationPanel() {

        JPanel panel =
                new JPanel(new GridBagLayout());

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Purchase Information"
                )
        );

        purchaseIdField = new JTextField(16);
        purchaseIdField.setEditable(false);

        supplierComboBox = new JComboBox<>();
        employeeComboBox = new JComboBox<>();

        purchaseDateField = new JTextField(16);
        invoiceNumberField = new JTextField(16);

        purchaseStatusComboBox =
                new JComboBox<>(
                        new String[]{
                                "Completed",
                                "Cancelled"
                        }
                );

        notesArea = new JTextArea(2, 16);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        addFormField(
                panel,
                "Purchase ID",
                purchaseIdField,
                0,
                0
        );

        addFormField(
                panel,
                "Supplier",
                supplierComboBox,
                0,
                2
        );

        addFormField(
                panel,
                "Employee",
                employeeComboBox,
                0,
                4
        );

        addFormField(
                panel,
                "Purchase Date",
                purchaseDateField,
                1,
                0
        );

        addFormField(
                panel,
                "Invoice Number",
                invoiceNumberField,
                1,
                2
        );

        addFormField(
                panel,
                "Status",
                purchaseStatusComboBox,
                1,
                4
        );

        JScrollPane notesScrollPane =
                new JScrollPane(notesArea);

        addFormField(
                panel,
                "Notes",
                notesScrollPane,
                2,
                0
        );

        return panel;
    }

    /**
     * Creates the section used to add materials.
     *
     * @return material-entry panel
     */
    private JPanel createMaterialEntryPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                8
                        )
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Add Material to Purchase"
                )
        );

        materialComboBox = new JComboBox<>();
        materialComboBox.setPrototypeDisplayValue(
                "MAT-0000 - Long material name"
        );

        quantityField = new JTextField(10);
        unitCostField = new JTextField(12);

        JButton addButton =
                createButton("Add Material");

        JButton removeButton =
                createButton("Remove Selected");

        purchaseTotalLabel =
                new JLabel("Total: UGX 0.00");

        purchaseTotalLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        purchaseTotalLabel.setForeground(
                new Color(72, 45, 145)
        );

        panel.add(new JLabel("Material"));
        panel.add(materialComboBox);

        panel.add(new JLabel("Quantity"));
        panel.add(quantityField);

        panel.add(new JLabel("Unit Cost"));
        panel.add(unitCostField);

        panel.add(addButton);
        panel.add(removeButton);
        panel.add(purchaseTotalLabel);

        addButton.addActionListener(
                event -> addMaterial()
        );

        removeButton.addActionListener(
                event -> removeSelectedMaterial()
        );

        return panel;
    }

    /**
     * Adds a labelled component to a grid panel.
     *
     * @param panel destination panel
     * @param labelText field label
     * @param component input component
     * @param row grid row
     * @param column grid column
     */
    private void addFormField(
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
                new Insets(5, 6, 5, 6);

        JLabel label = new JLabel(labelText);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
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
                new Insets(5, 6, 5, 12);

        panel.add(
                component,
                fieldConstraints
        );
    }

    /**
     * Creates the table containing the current purchase lines.
     *
     * @return purchase-detail table
     */
    private JScrollPane createDetailTablePanel() {

        detailTableModel = new DefaultTableModel(
                new Object[]{
                        "Material ID",
                        "Material Name",
                        "Unit",
                        "Quantity",
                        "Unit Cost",
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

        detailTable = new JTable(detailTableModel);
        detailTable.setRowHeight(24);

        detailTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(detailTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Materials in Current Purchase"
                )
        );

        return scrollPane;
    }

    /**
     * Creates the purchase-history table.
     *
     * @return purchase-history panel
     */
    private JPanel createPurchaseTablePanel() {

        JPanel panel =
                new JPanel(new BorderLayout());

        purchaseTableModel = new DefaultTableModel(
                new Object[]{
                        "Purchase ID",
                        "Supplier ID",
                        "Employee ID",
                        "Purchase Date",
                        "Invoice Number",
                        "Status",
                        "Total Amount",
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

        purchaseTable =
                new JTable(purchaseTableModel);

        purchaseTable.setRowHeight(24);
        purchaseTable.setAutoCreateRowSorter(true);

        purchaseTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        completedPurchasesLabel =
                new JLabel(
                        "Completed purchases: UGX 0.00"
                );

        completedPurchasesLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        completedPurchasesLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        8,
                        5,
                        8
                )
        );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Purchase History"
                )
        );

        panel.add(
                new JScrollPane(purchaseTable),
                BorderLayout.CENTER
        );

        panel.add(
                completedPurchasesLabel,
                BorderLayout.SOUTH
        );

        return panel;
    }

    /**
     * Creates the main action buttons.
     *
     * @return button panel
     */
    private JPanel createButtonPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                7,
                                12
                        )
                );

        JButton newButton = createButton("New");
        JButton saveButton = createButton("Save");
        JButton findButton = createButton("Find");
        JButton editButton = createButton("Edit");
        JButton deleteButton = createButton("Delete");
        JButton firstButton = createButton("First");
        JButton previousButton = createButton("Previous");
        JButton nextButton = createButton("Next");
        JButton lastButton = createButton("Last");
        JButton refreshButton = createButton("Refresh");
        JButton closeButton = createButton("Close");

        newButton.addActionListener(
                event -> clearFormSafely()
        );

        saveButton.addActionListener(
                event -> savePurchase()
        );

        findButton.addActionListener(
                event -> findPurchase()
        );

        editButton.addActionListener(
                event -> updatePurchase()
        );

        deleteButton.addActionListener(
                event -> deletePurchase()
        );

        firstButton.addActionListener(
                event -> navigatePurchase("FIRST")
        );

        previousButton.addActionListener(
                event -> navigatePurchase("PREVIOUS")
        );

        nextButton.addActionListener(
                event -> navigatePurchase("NEXT")
        );

        lastButton.addActionListener(
                event -> navigatePurchase("LAST")
        );

        refreshButton.addActionListener(
                event -> refreshEverything()
        );

        closeButton.addActionListener(
                event -> dispose()
        );

        panel.add(newButton);
        panel.add(saveButton);
        panel.add(findButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(firstButton);
        panel.add(previousButton);
        panel.add(nextButton);
        panel.add(lastButton);
        panel.add(refreshButton);
        panel.add(closeButton);

        return panel;
    }

    /**
     * Creates a consistently styled button.
     *
     * @param text button caption
     * @return configured button
     */
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

    /**
     * Registers combo-box and table events.
     */
    private void registerEvents() {

        materialComboBox.addActionListener(
                event -> loadSelectedMaterialCost()
        );

        purchaseTable.getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        loadSelectedPurchase();
                    }
                });
    }

    /**
     * Loads suppliers, employees and materials.
     *
     * @throws SQLException when database access fails
     */
    private void loadReferenceData()
            throws SQLException {

        loadSuppliers();
        loadEmployees();
        loadMaterials();
    }

    /**
     * Loads suppliers into the supplier combo box.
     *
     * @throws SQLException when database access fails
     */
    private void loadSuppliers()
            throws SQLException {

        supplierMap.clear();
        supplierComboBox.removeAllItems();

        for (Supplier supplier
                : supplierDAO.findAllSuppliers()) {

            String displayText =
                    supplier.getSupplierId()
                    + " - "
                    + supplier.getSupplierName();

            supplierMap.put(
                    displayText,
                    supplier
            );

            supplierComboBox.addItem(
                    displayText
            );
        }
    }

    /**
     * Loads employees into the employee combo box.
     *
     * @throws SQLException when database access fails
     */
    private void loadEmployees()
            throws SQLException {

        employeeMap.clear();
        employeeComboBox.removeAllItems();

        for (Employee employee
                : employeeDAO.findAllEmployees()) {

            String displayText =
                    employee.getEmployeeId()
                    + " - "
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

    /**
     * Loads materials into the material combo box.
     *
     * @throws SQLException when database access fails
     */
    private void loadMaterials()
            throws SQLException {

        materialMap.clear();
        materialComboBox.removeAllItems();

        for (Material material
                : materialDAO.findAllMaterials()) {

            String displayText =
                    material.getMaterialId()
                    + " - "
                    + material.getMaterialName();

            materialMap.put(
                    displayText,
                    material
            );

            materialComboBox.addItem(
                    displayText
            );
        }
    }

    /**
     * Loads the selected material's current cost.
     */
    private void loadSelectedMaterialCost() {

        Material material =
                getSelectedMaterial();

        if (material == null) {
            return;
        }

        BigDecimal cost =
                material.getUnitCost();

        unitCostField.setText(
                cost == null
                        ? "0.00"
                        : cost.toPlainString()
        );

        quantityField.requestFocus();
    }

    /**
     * Adds the selected material to the current purchase.
     */
    private void addMaterial() {

        try {
            Material material =
                    getSelectedMaterial();

            if (material == null) {

                showError(
                        "Please select a material."
                );

                return;
            }

            BigDecimal quantity =
                    parseDecimal(
                            quantityField.getText()
                    );

            BigDecimal unitCost =
                    parseDecimal(
                            unitCostField.getText()
                    );

            if (quantity.compareTo(
                    BigDecimal.ZERO) <= 0) {

                showError(
                        "Quantity must be greater than zero."
                );

                return;
            }

            if (unitCost.compareTo(
                    BigDecimal.ZERO) < 0) {

                showError(
                        "Unit cost cannot be negative."
                );

                return;
            }

            for (PurchaseDetail detail
                    : purchaseDetails) {

                if (detail.getMaterialId().equals(
                        material.getMaterialId()
                )) {

                    showError(
                            "That material is already "
                            + "in the purchase."
                    );

                    return;
                }
            }

            purchaseDetails.add(
                    new PurchaseDetail(
                            material.getMaterialId(),
                            quantity,
                            unitCost
                    )
            );

            refreshDetailTable();

            quantityField.setText("");
            quantityField.requestFocus();

        } catch (NumberFormatException exception) {

            showError(
                    "Enter valid quantity and unit-cost values."
            );
        }
    }

    /**
     * Removes the selected detail line.
     */
    private void removeSelectedMaterial() {

        int selectedRow =
                detailTable.getSelectedRow();

        if (selectedRow < 0) {

            showError(
                    "Select a material line to remove."
            );

            return;
        }

        purchaseDetails.remove(selectedRow);
        refreshDetailTable();
    }

    /**
     * Saves the current purchase.
     */
    private void savePurchase() {

        try {
            Purchase purchase =
                    collectPurchaseFromForm();

            purchaseDAO.savePurchaseWithDetails(
                    purchase
            );

            showInformation(
                    "Purchase saved successfully.\n"
                    + "Purchase ID: "
                    + purchase.getPurchaseId()
                    + "\nMaterial stock has been updated."
            );

            loadMaterials();
            refreshPurchaseTable();
            clearForm();

        } catch (DateTimeParseException exception) {

            showError(
                    "Enter the purchase date "
                    + "in YYYY-MM-DD format."
            );

        } catch (NumberFormatException exception) {

            showError(
                    "Enter valid numeric values."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1062) {

                showError(
                        "That invoice number or purchase ID "
                        + "has already been used."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    /**
     * Finds a purchase using its identifier.
     */
    private void findPurchase() {

        String purchaseId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the Purchase ID:",
                        "Find Purchase",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (purchaseId == null
                || purchaseId.trim().isEmpty()) {
            return;
        }

        try {
            Purchase purchase =
                    purchaseDAO.findPurchase(
                            purchaseId.trim()
                    );

            if (purchase == null) {

                showError(
                        "Purchase not found."
                );

                return;
            }

            displayPurchase(purchase);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Updates the current purchase.
     */
    private void updatePurchase() {

        String purchaseId =
                purchaseIdField.getText().trim();

        if (purchaseId.isEmpty()) {

            showError(
                    "Find or select the purchase "
                    + "you want to edit."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Update purchase " + purchaseId + "?\n"
                + "Material stock will be recalculated.",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Purchase purchase =
                    collectPurchaseFromForm();

            purchaseDAO.updatePurchaseWithDetails(
                    purchase
            );

            showInformation(
                    "Purchase updated successfully.\n"
                    + "Material stock has been recalculated."
            );

            loadMaterials();
            refreshPurchaseTable();
            displayPurchase(
                    purchaseDAO.findPurchase(
                            purchaseId
                    )
            );

        } catch (DateTimeParseException exception) {

            showError(
                    "Enter the purchase date "
                    + "in YYYY-MM-DD format."
            );

        } catch (NumberFormatException exception) {

            showError(
                    "Enter valid numeric values."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1062) {

                showError(
                        "That invoice number is already used."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    /**
     * Deletes the current purchase.
     */
    private void deletePurchase() {

        String purchaseId =
                purchaseIdField.getText().trim();

        if (purchaseId.isEmpty()) {

            showError(
                    "Find or select the purchase "
                    + "you want to delete."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete purchase " + purchaseId + "?\n"
                + "Its material quantities will be "
                + "removed from stock.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted =
                    purchaseDAO.deletePurchase(
                            purchaseId
                    );

            if (deleted) {

                showInformation(
                        "Purchase deleted successfully.\n"
                        + "Material stock was reversed."
                );

                loadMaterials();
                refreshPurchaseTable();
                clearForm();

            } else {

                showError(
                        "Purchase was not found."
                );
            }

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Navigates through purchase records.
     *
     * @param direction navigation direction
     */
    private void navigatePurchase(
            String direction
    ) {

        try {
            Purchase purchase;

            String currentId =
                    purchaseIdField
                            .getText()
                            .trim();

            switch (direction) {

                case "FIRST" ->
                    purchase =
                        purchaseDAO.findFirstPurchase();

                case "LAST" ->
                    purchase =
                        purchaseDAO.findLastPurchase();

                case "NEXT" -> {

                    if (currentId.isEmpty()) {

                        purchase =
                            purchaseDAO.findFirstPurchase();

                    } else {

                        purchase =
                            purchaseDAO.findNextPurchase(
                                    currentId
                            );
                    }
                }

                case "PREVIOUS" -> {

                    if (currentId.isEmpty()) {

                        purchase =
                            purchaseDAO.findLastPurchase();

                    } else {

                        purchase =
                            purchaseDAO.findPreviousPurchase(
                                    currentId
                            );
                    }
                }

                default -> purchase = null;
            }

            if (purchase == null) {

                showInformation(
                        "There are no more purchases "
                        + "in that direction."
                );

                return;
            }

            displayPurchase(purchase);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Creates a Purchase from the current form values.
     *
     * @return completed purchase object
     */
    private Purchase collectPurchaseFromForm() {

        Supplier supplier = getSelectedSupplier();
        Employee employee = getSelectedEmployee();

        if (supplier == null) {

            throw new IllegalArgumentException(
                    "Please select a supplier."
            );
        }

        if (employee == null) {

            throw new IllegalArgumentException(
                    "Please select an employee."
            );
        }

        LocalDate purchaseDate =
                LocalDate.parse(
                        purchaseDateField
                                .getText()
                                .trim()
                );

        Purchase purchase = new Purchase();

        purchase.setPurchaseId(
                purchaseIdField.getText().trim()
        );

        purchase.setSupplierId(
                supplier.getSupplierId()
        );

        purchase.setEmployeeId(
                employee.getEmployeeId()
        );

        purchase.setPurchaseDate(purchaseDate);

        purchase.setInvoiceNumber(
                invoiceNumberField
                        .getText()
                        .trim()
        );

        purchase.setPurchaseStatus(
                (String) purchaseStatusComboBox
                        .getSelectedItem()
        );

        purchase.setNotes(
                notesArea.getText().trim()
        );

        purchase.setDetails(purchaseDetails);

        purchase.setTotalAmount(
                purchase.calculateTotalAmount()
        );

        return purchase;
    }

    /**
     * Displays a purchase and its detail lines.
     *
     * @param purchase purchase to display
     */
    private void displayPurchase(
            Purchase purchase
    ) {

        if (purchase == null) {
            return;
        }

        purchaseIdField.setText(
                purchase.getPurchaseId()
        );

        selectSupplier(
                purchase.getSupplierId()
        );

        selectEmployee(
                purchase.getEmployeeId()
        );

        purchaseDateField.setText(
                purchase.getPurchaseDate() == null
                        ? ""
                        : purchase.getPurchaseDate()
                                .toString()
        );

        invoiceNumberField.setText(
                valueOrEmpty(
                        purchase.getInvoiceNumber()
                )
        );

        purchaseStatusComboBox.setSelectedItem(
                purchase.getPurchaseStatus()
        );

        notesArea.setText(
                valueOrEmpty(
                        purchase.getNotes()
                )
        );

        purchaseDetails.clear();

        if (purchase.getDetails() != null) {
            purchaseDetails.addAll(
                    purchase.getDetails()
            );
        }

        refreshDetailTable();
    }

    /**
     * Loads the purchase selected in the history table.
     */
    private void loadSelectedPurchase() {

        int selectedRow =
                purchaseTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow =
                purchaseTable.convertRowIndexToModel(
                        selectedRow
                );

        String purchaseId =
                purchaseTableModel.getValueAt(
                        modelRow,
                        0
                ).toString();

        try {
            Purchase purchase =
                    purchaseDAO.findPurchase(
                            purchaseId
                    );

            displayPurchase(purchase);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Refreshes the current purchase-detail table.
     */
    private void refreshDetailTable() {

        detailTableModel.setRowCount(0);

        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseDetail detail
                : purchaseDetails) {

            Material material =
                    findMaterialById(
                            detail.getMaterialId()
                    );

            String materialName =
                    material == null
                            ? ""
                            : material.getMaterialName();

            String unit =
                    material == null
                            ? ""
                            : material.getUnitOfMeasure();

            BigDecimal subtotal =
                    detail.calculateSubtotal();

            total = total.add(subtotal);

            detailTableModel.addRow(
                    new Object[]{
                            detail.getMaterialId(),
                            materialName,
                            unit,
                            detail.getQuantity(),
                            detail.getUnitCost(),
                            subtotal
                    }
            );
        }

        purchaseTotalLabel.setText(
                "Total: " + formatMoney(total)
        );
    }

    /**
     * Reloads the purchase-history table.
     *
     * @throws SQLException when database access fails
     */
    private void refreshPurchaseTable()
            throws SQLException {

        purchaseTableModel.setRowCount(0);

        for (Purchase purchase
                : purchaseDAO.findAllPurchases()) {

            purchaseTableModel.addRow(
                    new Object[]{
                            purchase.getPurchaseId(),
                            purchase.getSupplierId(),
                            purchase.getEmployeeId(),
                            purchase.getPurchaseDate(),
                            purchase.getInvoiceNumber(),
                            purchase.getPurchaseStatus(),
                            purchase.getTotalAmount(),
                            purchase.getNotes()
                    }
            );
        }

        completedPurchasesLabel.setText(
                "Completed purchases: "
                + formatMoney(
                        purchaseDAO
                                .calculateCompletedPurchaseTotal()
                )
        );
    }

    /**
     * Reloads all purchase and reference information.
     */
    private void refreshEverything() {

        try {
            loadReferenceData();
            refreshPurchaseTable();
            clearForm();

            showInformation(
                    "Purchase information refreshed."
            );

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Clears the form and prepares a new purchase.
     *
     * @throws SQLException when ID generation fails
     */
    private void clearForm()
            throws SQLException {

        purchaseIdField.setText(
                purchaseDAO.generateNextPurchaseId()
        );

        purchaseDateField.setText(
                LocalDate.now().toString()
        );

        invoiceNumberField.setText("");
        notesArea.setText("");

        purchaseStatusComboBox.setSelectedItem(
                "Completed"
        );

        if (supplierComboBox.getItemCount() > 0) {
            supplierComboBox.setSelectedIndex(0);
        }

        if (employeeComboBox.getItemCount() > 0) {
            employeeComboBox.setSelectedIndex(0);
        }

        if (materialComboBox.getItemCount() > 0) {
            materialComboBox.setSelectedIndex(0);
        }

        quantityField.setText("");
        purchaseDetails.clear();

        purchaseTable.clearSelection();
        detailTable.clearSelection();

        refreshDetailTable();
        loadSelectedMaterialCost();
    }

    /**
     * Clears the form while handling database errors.
     */
    private void clearFormSafely() {

        try {
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Returns the selected supplier.
     *
     * @return selected supplier, or null
     */
    private Supplier getSelectedSupplier() {

        String selected =
                (String) supplierComboBox
                        .getSelectedItem();

        return supplierMap.get(selected);
    }

    /**
     * Returns the selected employee.
     *
     * @return selected employee, or null
     */
    private Employee getSelectedEmployee() {

        String selected =
                (String) employeeComboBox
                        .getSelectedItem();

        return employeeMap.get(selected);
    }

    /**
     * Returns the selected material.
     *
     * @return selected material, or null
     */
    private Material getSelectedMaterial() {

        String selected =
                (String) materialComboBox
                        .getSelectedItem();

        return materialMap.get(selected);
    }

    /**
     * Finds a loaded material using its identifier.
     *
     * @param materialId material identifier
     * @return matching material, or null
     */
    private Material findMaterialById(
            String materialId
    ) {

        for (Material material
                : materialMap.values()) {

            if (material.getMaterialId().equals(
                    materialId
            )) {
                return material;
            }
        }

        return null;
    }

    /**
     * Selects a supplier using its identifier.
     *
     * @param supplierId supplier identifier
     */
    private void selectSupplier(
            String supplierId
    ) {

        for (int index = 0;
             index < supplierComboBox.getItemCount();
             index++) {

            String display =
                    supplierComboBox.getItemAt(index);

            Supplier supplier =
                    supplierMap.get(display);

            if (supplier != null
                    && supplier.getSupplierId()
                            .equals(supplierId)) {

                supplierComboBox.setSelectedIndex(
                        index
                );

                return;
            }
        }
    }

    /**
     * Selects an employee using its identifier.
     *
     * @param employeeId employee identifier
     */
    private void selectEmployee(
            String employeeId
    ) {

        for (int index = 0;
             index < employeeComboBox.getItemCount();
             index++) {

            String display =
                    employeeComboBox.getItemAt(index);

            Employee employee =
                    employeeMap.get(display);

            if (employee != null
                    && employee.getEmployeeId()
                            .equals(employeeId)) {

                employeeComboBox.setSelectedIndex(
                        index
                );

                return;
            }
        }
    }

    /**
     * Converts text into a decimal number.
     *
     * @param value numeric text
     * @return converted decimal
     */
    private BigDecimal parseDecimal(
            String value
    ) {

        String cleaned =
                value.trim().replace(",", "");

        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(cleaned);
    }

    /**
     * Formats an amount as Uganda shillings.
     *
     * @param amount monetary amount
     * @return formatted amount
     */
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

    /**
     * Converts null into an empty string.
     *
     * @param value possible null string
     * @return original or empty string
     */
    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * Displays an informational message.
     *
     * @param message message to display
     */
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

    /**
     * Displays an error message.
     *
     * @param message error message
     */
    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays a database error.
     *
     * @param exception database exception
     */
    private void showDatabaseError(
            SQLException exception
    ) {

        showError(
                "A database error occurred.\n"
                + exception.getMessage()
        );
    }

    /**
     * Opens the purchase form for independent testing.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PurchaseForm form =
                    new PurchaseForm();

            form.setVisible(true);
        });
    }
}