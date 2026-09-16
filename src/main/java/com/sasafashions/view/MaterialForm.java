package com.sasafashions.view;

import com.sasafashions.dao.MaterialDAO;
import com.sasafashions.model.Material;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Provides the graphical interface for managing tailoring materials.
 *
 * <p>The form supports saving, finding, updating, deleting and
 * navigating material records. It also displays material stock
 * values and identifies materials that require restocking.</p>
 *
 * @author Joshua Muhwezi
 * @version 1.0
 */
public class MaterialForm extends JFrame {

    private final MaterialDAO materialDAO;

    private JTextField materialIdField;
    private JTextField materialNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> unitOfMeasureComboBox;
    private JTextField quantityField;
    private JTextField reorderLevelField;
    private JTextField unitCostField;
    private JComboBox<String> materialStatusComboBox;

    private JLabel currentStockValueLabel;
    private JLabel totalStockValueLabel;
    private JLabel tableStatusLabel;

    private JTable materialTable;
    private DefaultTableModel tableModel;

    /**
     * Creates and prepares the material-management form.
     */
    public MaterialForm() {

        materialDAO = new MaterialDAO();

        initializeFrame();
        initializeComponents();
        registerEvents();

        try {
            refreshMaterialTable();
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Configures the main material frame.
     */
    private void initializeFrame() {

        setTitle(
                "Sasa Fashions - Material Management"
        );

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Creates and adds the form components.
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
     * Creates the form heading.
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
                        "MATERIAL AND STOCK MANAGEMENT"
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
     * Creates the material-entry panel.
     *
     * @return material-entry panel
     */
    private JPanel createFormPanel() {

        JPanel container =
                new JPanel(new BorderLayout(15, 10));

        container.setBorder(
                BorderFactory.createTitledBorder(
                        "Material Information"
                )
        );

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        materialIdField = new JTextField(18);
        materialIdField.setEditable(false);

        materialNameField = new JTextField(18);

        descriptionArea = new JTextArea(3, 18);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        unitOfMeasureComboBox =
                new JComboBox<>(
                        new String[]{
                                "Metres",
                                "Yards",
                                "Pieces",
                                "Rolls",
                                "Kilograms",
                                "Sets"
                        }
                );

        /*
         * Allows the user to type another measurement unit
         * that is not in the default list.
         */
        unitOfMeasureComboBox.setEditable(true);

        quantityField = new JTextField(18);
        reorderLevelField = new JTextField(18);
        unitCostField = new JTextField(18);

        materialStatusComboBox =
                new JComboBox<>(
                        new String[]{
                                "Available",
                                "Unavailable",
                                "Discontinued"
                        }
                );

        addFormField(
                formPanel,
                "Material ID",
                materialIdField,
                0,
                0
        );

        addFormField(
                formPanel,
                "Material Name",
                materialNameField,
                0,
                2
        );

        addFormField(
                formPanel,
                "Unit of Measure",
                unitOfMeasureComboBox,
                1,
                0
        );

        addFormField(
                formPanel,
                "Quantity in Stock",
                quantityField,
                1,
                2
        );

        addFormField(
                formPanel,
                "Reorder Level",
                reorderLevelField,
                2,
                0
        );

        addFormField(
                formPanel,
                "Unit Cost",
                unitCostField,
                2,
                2
        );

        addFormField(
                formPanel,
                "Status",
                materialStatusComboBox,
                3,
                0
        );

        JScrollPane descriptionScrollPane =
                new JScrollPane(descriptionArea);

        addFormField(
                formPanel,
                "Description",
                descriptionScrollPane,
                3,
                2
        );

        container.add(
                formPanel,
                BorderLayout.CENTER
        );

        container.add(
                createStockSummaryPanel(),
                BorderLayout.EAST
        );

        return container;
    }

    /**
     * Places a labelled input component on the form.
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
            Component component,
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
                new Insets(7, 8, 7, 8);

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
                new Insets(7, 8, 7, 20);

        panel.add(
                component,
                fieldConstraints
        );
    }

    /**
     * Creates the stock-value summary panel.
     *
     * @return stock summary panel
     */
    private JPanel createStockSummaryPanel() {

        JPanel panel =
                new JPanel(new GridBagLayout());

        panel.setBackground(
                new Color(245, 243, 252)
        );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Stock Summary"
                )
        );

        currentStockValueLabel =
                createValueLabel();

        totalStockValueLabel =
                createValueLabel();

        tableStatusLabel =
                new JLabel("Showing all materials");

        tableStatusLabel.setForeground(
                new Color(72, 45, 145)
        );

        tableStatusLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        addSummaryRow(
                panel,
                "Current Material:",
                currentStockValueLabel,
                0
        );

        addSummaryRow(
                panel,
                "Total Stock Value:",
                totalStockValueLabel,
                1
        );

        GridBagConstraints statusConstraints =
                new GridBagConstraints();

        statusConstraints.gridx = 0;
        statusConstraints.gridy = 2;
        statusConstraints.gridwidth = 2;
        statusConstraints.insets =
                new Insets(12, 10, 8, 10);

        statusConstraints.anchor =
                GridBagConstraints.WEST;

        panel.add(
                tableStatusLabel,
                statusConstraints
        );

        return panel;
    }

    /**
     * Creates a label used for displaying monetary values.
     *
     * @return formatted monetary label
     */
    private JLabel createValueLabel() {

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

    /**
     * Adds one item to the stock-summary panel.
     *
     * @param panel summary panel
     * @param labelText description
     * @param valueLabel value label
     * @param row grid row
     */
    private void addSummaryRow(
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

    /**
     * Creates the material-results table.
     *
     * <p>Low-stock rows are displayed with a light-red
     * background.</p>
     *
     * @return scroll pane containing the material table
     */
    private JScrollPane createTablePanel() {

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Material ID",
                        "Material Name",
                        "Description",
                        "Unit",
                        "Quantity",
                        "Reorder Level",
                        "Unit Cost",
                        "Stock Value",
                        "Status"
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

        materialTable = new JTable(tableModel) {

            @Override
            public Component prepareRenderer(
                    TableCellRenderer renderer,
                    int row,
                    int column
            ) {

                Component component =
                        super.prepareRenderer(
                                renderer,
                                row,
                                column
                        );

                if (isRowSelected(row)) {
                    return component;
                }

                int modelRow =
                        convertRowIndexToModel(row);

                Object quantityValue =
                        getModel().getValueAt(
                                modelRow,
                                4
                        );

                Object reorderValue =
                        getModel().getValueAt(
                                modelRow,
                                5
                        );

                Object statusValue =
                        getModel().getValueAt(
                                modelRow,
                                8
                        );

                boolean lowStock = false;

                if (quantityValue instanceof BigDecimal quantity
                        && reorderValue
                        instanceof BigDecimal reorderLevel) {

                    lowStock =
                            quantity.compareTo(
                                    reorderLevel
                            ) <= 0;
                }

                boolean discontinued =
                        statusValue != null
                        && "Discontinued".equalsIgnoreCase(
                                statusValue.toString()
                        );

                if (lowStock && !discontinued) {

                    component.setBackground(
                            new Color(255, 225, 225)
                    );

                } else {

                    component.setBackground(Color.WHITE);
                }

                return component;
            }
        };

        materialTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        materialTable.setRowHeight(25);
        materialTable.setAutoCreateRowSorter(true);

        materialTable.getTableHeader().setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(materialTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Material Stock"
                )
        );

        return scrollPane;
    }

    /**
     * Creates the form-action buttons.
     *
     * @return panel containing action buttons
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

        JButton lowStockButton =
                createButton("Low Stock");

        JButton refreshButton =
                createButton("Show All");

        JButton closeButton =
                createButton("Close");

        newButton.addActionListener(
                event -> clearFormSafely()
        );

        saveButton.addActionListener(
                event -> saveMaterial()
        );

        findButton.addActionListener(
                event -> findMaterial()
        );

        editButton.addActionListener(
                event -> updateMaterial()
        );

        deleteButton.addActionListener(
                event -> deleteMaterial()
        );

        firstButton.addActionListener(
                event -> navigateMaterial("FIRST")
        );

        previousButton.addActionListener(
                event -> navigateMaterial("PREVIOUS")
        );

        nextButton.addActionListener(
                event -> navigateMaterial("NEXT")
        );

        lastButton.addActionListener(
                event -> navigateMaterial("LAST")
        );

        lowStockButton.addActionListener(
                event -> showLowStockMaterials()
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
        panel.add(lowStockButton);
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
     * Registers table-selection and stock-value events.
     */
    private void registerEvents() {

        materialTable.getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        loadSelectedMaterial();
                    }
                });

        DocumentListener stockValueListener =
                new DocumentListener() {

                    @Override
                    public void insertUpdate(
                            DocumentEvent event
                    ) {
                        updateCurrentStockValue();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent event
                    ) {
                        updateCurrentStockValue();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent event
                    ) {
                        updateCurrentStockValue();
                    }
                };

        quantityField.getDocument()
                .addDocumentListener(
                        stockValueListener
                );

        unitCostField.getDocument()
                .addDocumentListener(
                        stockValueListener
                );
    }

    /**
     * Saves a new material.
     */
    private void saveMaterial() {

        try {
            Material material =
                    collectMaterialFromForm();

            materialDAO.saveMaterial(material);

            showInformation(
                    "Material saved successfully.\n"
                    + "Material ID: "
                    + material.getMaterialId()
            );

            refreshMaterialTable();
            clearForm();

        } catch (NumberFormatException exception) {

            showError(
                    "Quantity, reorder level and unit cost "
                    + "must contain valid numbers."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Finds a material using an entered material ID.
     */
    private void findMaterial() {

        String materialId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the Material ID:",
                        "Find Material",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (materialId == null
                || materialId.trim().isEmpty()) {
            return;
        }

        try {
            Material material =
                    materialDAO.findMaterial(
                            materialId.trim()
                    );

            if (material == null) {

                showError(
                        "Material not found."
                );

                return;
            }

            displayMaterial(material);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Updates the currently displayed material.
     */
    private void updateMaterial() {

        String materialId =
                materialIdField.getText().trim();

        if (materialId.isEmpty()) {

            showError(
                    "Find or select the material "
                    + "you want to edit."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Update material " + materialId + "?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Material material =
                    collectMaterialFromForm();

            boolean updated =
                    materialDAO.updateMaterial(
                            material
                    );

            if (updated) {

                showInformation(
                        "Material updated successfully."
                );

                refreshMaterialTable();
                displayMaterial(material);

            } else {

                showError(
                        "Material was not found."
                );
            }

        } catch (NumberFormatException exception) {

            showError(
                    "Quantity, reorder level and unit cost "
                    + "must contain valid numbers."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            showDatabaseError(exception);
        }
    }

    /**
     * Deletes the currently displayed material.
     */
    private void deleteMaterial() {

        String materialId =
                materialIdField.getText().trim();

        if (materialId.isEmpty()) {

            showError(
                    "Find or select the material "
                    + "you want to delete."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete material " + materialId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted =
                    materialDAO.deleteMaterial(
                            materialId
                    );

            if (deleted) {

                showInformation(
                        "Material deleted successfully."
                );

                refreshMaterialTable();
                clearForm();

            } else {

                showError(
                        "Material was not found."
                );
            }

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1451) {

                showError(
                        "This material cannot be deleted "
                        + "because purchase records are "
                        + "attached to it.\n"
                        + "Mark it as Discontinued instead."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    /**
     * Navigates through material records.
     *
     * @param direction requested navigation direction
     */
    private void navigateMaterial(
            String direction
    ) {

        try {
            Material material;

            String currentId =
                    materialIdField
                            .getText()
                            .trim();

            switch (direction) {

                case "FIRST" ->
                    material =
                        materialDAO.findFirstMaterial();

                case "LAST" ->
                    material =
                        materialDAO.findLastMaterial();

                case "NEXT" -> {

                    if (currentId.isEmpty()) {

                        material =
                            materialDAO.findFirstMaterial();

                    } else {

                        material =
                            materialDAO.findNextMaterial(
                                    currentId
                            );
                    }
                }

                case "PREVIOUS" -> {

                    if (currentId.isEmpty()) {

                        material =
                            materialDAO.findLastMaterial();

                    } else {

                        material =
                            materialDAO.findPreviousMaterial(
                                    currentId
                            );
                    }
                }

                default -> material = null;
            }

            if (material == null) {

                showInformation(
                        "There are no more materials "
                        + "in that direction."
                );

                return;
            }

            displayMaterial(material);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Displays only materials that have reached
     * their reorder levels.
     */
    private void showLowStockMaterials() {

        try {
            ArrayList<Material> materials =
                    materialDAO.findLowStockMaterials();

            displayMaterialsInTable(materials);

            tableStatusLabel.setText(
                    "Low-stock materials: "
                    + materials.size()
            );

            if (materials.isEmpty()) {

                showInformation(
                        "No materials currently require "
                        + "restocking."
                );
            }

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Creates a Material object from form values.
     *
     * @return material containing entered information
     */
    private Material collectMaterialFromForm() {

        String unitOfMeasure = "";

        Object selectedUnit =
                unitOfMeasureComboBox
                        .getSelectedItem();

        if (selectedUnit != null) {
            unitOfMeasure =
                    selectedUnit.toString().trim();
        }

        return new Material(
                materialIdField.getText().trim(),
                materialNameField.getText().trim(),
                descriptionArea.getText().trim(),
                unitOfMeasure,
                parseDecimal(quantityField),
                parseDecimal(reorderLevelField),
                parseDecimal(unitCostField),
                (String) materialStatusComboBox
                        .getSelectedItem()
        );
    }

    /**
     * Converts the contents of a text field into a decimal.
     *
     * <p>Blank fields are treated as zero, and commas are
     * removed before conversion.</p>
     *
     * @param field text field containing a number
     * @return converted decimal value
     */
    private BigDecimal parseDecimal(
            JTextField field
    ) {

        String value =
                field.getText()
                        .trim()
                        .replace(",", "");

        if (value.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value);
    }

    /**
     * Loads the material selected in the table.
     */
    private void loadSelectedMaterial() {

        int selectedRow =
                materialTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow =
                materialTable.convertRowIndexToModel(
                        selectedRow
                );

        String materialId =
                tableModel.getValueAt(
                        modelRow,
                        0
                ).toString();

        try {
            Material material =
                    materialDAO.findMaterial(
                            materialId
                    );

            if (material != null) {
                displayMaterial(material);
            }

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Displays a material on the form.
     *
     * @param material material to display
     */
    private void displayMaterial(
            Material material
    ) {

        materialIdField.setText(
                material.getMaterialId()
        );

        materialNameField.setText(
                material.getMaterialName()
        );

        descriptionArea.setText(
                valueOrEmpty(
                        material.getDescription()
                )
        );

        unitOfMeasureComboBox.setSelectedItem(
                material.getUnitOfMeasure()
        );

        quantityField.setText(
                decimalText(
                        material.getQuantityInStock()
                )
        );

        reorderLevelField.setText(
                decimalText(
                        material.getReorderLevel()
                )
        );

        unitCostField.setText(
                decimalText(
                        material.getUnitCost()
                )
        );

        materialStatusComboBox.setSelectedItem(
                material.getMaterialStatus()
        );

        currentStockValueLabel.setText(
                formatMoney(
                        material.calculateStockValue()
                )
        );
    }

    /**
     * Loads all materials into the table.
     *
     * @throws SQLException when the database operation fails
     */
    private void refreshMaterialTable()
            throws SQLException {

        ArrayList<Material> materials =
                materialDAO.findAllMaterials();

        displayMaterialsInTable(materials);

        totalStockValueLabel.setText(
                formatMoney(
                        materialDAO
                                .calculateTotalStockValue()
                )
        );

        tableStatusLabel.setText(
                "Showing all materials: "
                + materials.size()
        );
    }

    /**
     * Displays the supplied material list in the table.
     *
     * @param materials materials to display
     */
    private void displayMaterialsInTable(
            ArrayList<Material> materials
    ) {

        tableModel.setRowCount(0);

        for (Material material : materials) {

            tableModel.addRow(
                    new Object[]{
                            material.getMaterialId(),
                            material.getMaterialName(),
                            material.getDescription(),
                            material.getUnitOfMeasure(),
                            material.getQuantityInStock(),
                            material.getReorderLevel(),
                            material.getUnitCost(),
                            material.calculateStockValue(),
                            material.getMaterialStatus()
                    }
            );
        }
    }

    /**
     * Recalculates the current material's stock value
     * as the user types.
     */
    private void updateCurrentStockValue() {

        try {
            BigDecimal quantity =
                    parseDecimal(quantityField);

            BigDecimal unitCost =
                    parseDecimal(unitCostField);

            currentStockValueLabel.setText(
                    formatMoney(
                            quantity.multiply(unitCost)
                    )
            );

        } catch (NumberFormatException exception) {

            currentStockValueLabel.setText(
                    "UGX 0.00"
            );
        }
    }

    /**
     * Reloads all material information.
     */
    private void refreshEverything() {

        try {
            refreshMaterialTable();
            clearForm();

            showInformation(
                    "Material information refreshed."
            );

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Clears the form and generates the next material ID.
     *
     * @throws SQLException when the ID cannot be generated
     */
    private void clearForm()
            throws SQLException {

        materialIdField.setText(
                materialDAO.generateNextMaterialId()
        );

        materialNameField.setText("");
        descriptionArea.setText("");

        unitOfMeasureComboBox.setSelectedItem(
                "Metres"
        );

        quantityField.setText("0.00");
        reorderLevelField.setText("0.00");
        unitCostField.setText("0.00");

        materialStatusComboBox.setSelectedItem(
                "Available"
        );

        materialTable.clearSelection();

        updateCurrentStockValue();
        materialNameField.requestFocus();
    }

    /**
     * Clears the form while safely handling database errors.
     */
    private void clearFormSafely() {

        try {
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Converts a possible null decimal into display text.
     *
     * @param value decimal value
     * @return decimal text or zero
     */
    private String decimalText(
            BigDecimal value
    ) {

        return value == null
                ? "0.00"
                : value.toPlainString();
    }

    /**
     * Formats a decimal value as Uganda shillings.
     *
     * @param amount amount to format
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
     * Converts a null string into an empty string.
     *
     * @param value possible null value
     * @return original string or empty string
     */
    private String valueOrEmpty(
            String value
    ) {

        return value == null ? "" : value;
    }

    /**
     * Displays an informational message.
     *
     * @param message information to display
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
     * Opens the material form for independent testing.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MaterialForm form =
                    new MaterialForm();

            form.setVisible(true);
        });
    }
}