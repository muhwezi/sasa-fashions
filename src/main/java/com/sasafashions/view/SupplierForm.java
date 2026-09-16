package com.sasafashions.view;

import com.sasafashions.dao.SupplierDAO;
import com.sasafashions.model.Supplier;

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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Provides the graphical interface for managing suppliers.
 *
 * <p>The form supports creating, finding, editing, deleting
 * and navigating supplier records.</p>
 *
 * @author Joshua Muhwezi
 * @version 1.0
 */
public class SupplierForm extends JFrame {

    private final SupplierDAO supplierDAO;

    private JTextField supplierIdField;
    private JTextField supplierNameField;
    private JTextField telephoneField;
    private JTextField emailField;
    private JTextArea addressArea;
    private JTextField registrationDateField;
    private JComboBox<String> statusComboBox;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    /**
     * Creates and prepares the supplier-management form.
     */
    public SupplierForm() {

        supplierDAO = new SupplierDAO();

        initializeFrame();
        initializeComponents();
        registerTableEvents();

        try {
            refreshSupplierTable();
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Configures the main frame.
     */
    private void initializeFrame() {

        setTitle(
                "Sasa Fashions - Supplier Management"
        );

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setSize(1050, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Creates and adds the screen components.
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
                new JLabel("SUPPLIER MANAGEMENT");

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
     * Creates the supplier-entry section.
     *
     * @return supplier form panel
     */
    private JPanel createFormPanel() {

        JPanel panel =
                new JPanel(new GridBagLayout());

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Supplier Information"
                )
        );

        supplierIdField = new JTextField(20);
        supplierIdField.setEditable(false);

        supplierNameField = new JTextField(20);
        telephoneField = new JTextField(20);
        emailField = new JTextField(20);

        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        registrationDateField =
                new JTextField(20);

        statusComboBox =
                new JComboBox<>(
                        new String[]{
                                "Active",
                                "Inactive"
                        }
                );

        addFormField(
                panel,
                "Supplier ID",
                supplierIdField,
                0,
                0
        );

        addFormField(
                panel,
                "Supplier Name",
                supplierNameField,
                0,
                2
        );

        addFormField(
                panel,
                "Telephone",
                telephoneField,
                1,
                0
        );

        addFormField(
                panel,
                "Email",
                emailField,
                1,
                2
        );

        addFormField(
                panel,
                "Registration Date",
                registrationDateField,
                2,
                0
        );

        addFormField(
                panel,
                "Status",
                statusComboBox,
                2,
                2
        );

        JScrollPane addressScrollPane =
                new JScrollPane(addressArea);

        addFormField(
                panel,
                "Address",
                addressScrollPane,
                3,
                0
        );

        JLabel dateHelpLabel =
                new JLabel(
                        "Use the format YYYY-MM-DD"
                );

        dateHelpLabel.setForeground(Color.GRAY);

        GridBagConstraints helpConstraints =
                new GridBagConstraints();

        helpConstraints.gridx = 2;
        helpConstraints.gridy = 3;
        helpConstraints.gridwidth = 2;
        helpConstraints.anchor =
                GridBagConstraints.WEST;

        helpConstraints.insets =
                new Insets(6, 8, 6, 8);

        panel.add(
                dateHelpLabel,
                helpConstraints
        );

        return panel;
    }

    /**
     * Places a labelled field on the form.
     *
     * @param panel form panel
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
     * Creates the supplier-results table.
     *
     * @return scroll pane containing the supplier table
     */
    private JScrollPane createTablePanel() {

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Supplier ID",
                        "Supplier Name",
                        "Telephone",
                        "Email",
                        "Address",
                        "Registration Date",
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

        supplierTable = new JTable(tableModel);

        supplierTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        supplierTable.setRowHeight(25);
        supplierTable.setAutoCreateRowSorter(true);

        supplierTable.getTableHeader().setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(supplierTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Registered Suppliers"
                )
        );

        return scrollPane;
    }

    /**
     * Creates the form action buttons.
     *
     * @return panel containing action buttons
     */
    private JPanel createButtonPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                8,
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
                event -> saveSupplier()
        );

        findButton.addActionListener(
                event -> findSupplier()
        );

        editButton.addActionListener(
                event -> updateSupplier()
        );

        deleteButton.addActionListener(
                event -> deleteSupplier()
        );

        firstButton.addActionListener(
                event -> navigateSupplier("FIRST")
        );

        previousButton.addActionListener(
                event -> navigateSupplier("PREVIOUS")
        );

        nextButton.addActionListener(
                event -> navigateSupplier("NEXT")
        );

        lastButton.addActionListener(
                event -> navigateSupplier("LAST")
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
     * Creates a consistently formatted button.
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
     * Connects table selection to supplier loading.
     */
    private void registerTableEvents() {

        supplierTable.getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        loadSelectedSupplier();
                    }
                });
    }

    /**
     * Saves information entered on the form.
     */
    private void saveSupplier() {

        try {
            Supplier supplier =
                    collectSupplierFromForm();

            supplierDAO.saveSupplier(supplier);

            showInformation(
                    "Supplier saved successfully.\n"
                    + "Supplier ID: "
                    + supplier.getSupplierId()
            );

            refreshSupplierTable();
            clearForm();

        } catch (DateTimeParseException exception) {

            showError(
                    "Enter the registration date "
                    + "in YYYY-MM-DD format."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1062) {

                showError(
                        "That email address is already "
                        + "registered to another supplier."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    /**
     * Finds a supplier after requesting its identifier.
     */
    private void findSupplier() {

        String supplierId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the Supplier ID:",
                        "Find Supplier",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (supplierId == null
                || supplierId.trim().isEmpty()) {
            return;
        }

        try {
            Supplier supplier =
                    supplierDAO.findSupplier(
                            supplierId.trim()
                    );

            if (supplier == null) {

                showError(
                        "Supplier not found."
                );

                return;
            }

            displaySupplier(supplier);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Updates the currently displayed supplier.
     */
    private void updateSupplier() {

        String supplierId =
                supplierIdField.getText().trim();

        if (supplierId.isEmpty()) {

            showError(
                    "Find or select the supplier "
                    + "you want to edit."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Update supplier " + supplierId + "?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Supplier supplier =
                    collectSupplierFromForm();

            boolean updated =
                    supplierDAO.updateSupplier(
                            supplier
                    );

            if (updated) {

                showInformation(
                        "Supplier updated successfully."
                );

                refreshSupplierTable();
                displaySupplier(supplier);

            } else {

                showError(
                        "Supplier was not found."
                );
            }

        } catch (DateTimeParseException exception) {

            showError(
                    "Enter the registration date "
                    + "in YYYY-MM-DD format."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1062) {

                showError(
                        "That email address is already "
                        + "registered to another supplier."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    /**
     * Deletes the currently displayed supplier.
     */
    private void deleteSupplier() {

        String supplierId =
                supplierIdField.getText().trim();

        if (supplierId.isEmpty()) {

            showError(
                    "Find or select the supplier "
                    + "you want to delete."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete supplier " + supplierId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted =
                    supplierDAO.deleteSupplier(
                            supplierId
                    );

            if (deleted) {

                showInformation(
                        "Supplier deleted successfully."
                );

                refreshSupplierTable();
                clearForm();

            } else {

                showError(
                        "Supplier was not found."
                );
            }

        } catch (SQLException exception) {

            if (exception.getErrorCode() == 1451) {

                showError(
                        "This supplier cannot be deleted "
                        + "because purchase records are "
                        + "attached to it.\n"
                        + "Change its status to Inactive instead."
                );

            } else {
                showDatabaseError(exception);
            }
        }
    }

    /**
     * Navigates through supplier records.
     *
     * @param direction navigation direction
     */
    private void navigateSupplier(
            String direction
    ) {

        try {
            Supplier supplier;

            String currentId =
                    supplierIdField
                            .getText()
                            .trim();

            switch (direction) {

                case "FIRST" ->
                    supplier =
                        supplierDAO.findFirstSupplier();

                case "LAST" ->
                    supplier =
                        supplierDAO.findLastSupplier();

                case "NEXT" -> {

                    if (currentId.isEmpty()) {

                        supplier =
                            supplierDAO.findFirstSupplier();

                    } else {

                        supplier =
                            supplierDAO.findNextSupplier(
                                    currentId
                            );
                    }
                }

                case "PREVIOUS" -> {

                    if (currentId.isEmpty()) {

                        supplier =
                            supplierDAO.findLastSupplier();

                    } else {

                        supplier =
                            supplierDAO.findPreviousSupplier(
                                    currentId
                            );
                    }
                }

                default -> supplier = null;
            }

            if (supplier == null) {

                showInformation(
                        "There are no more suppliers "
                        + "in that direction."
                );

                return;
            }

            displaySupplier(supplier);

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Creates a Supplier object from the entered form values.
     *
     * @return supplier containing the entered information
     */
    private Supplier collectSupplierFromForm() {

        String dateText =
                registrationDateField
                        .getText()
                        .trim();

        if (dateText.isEmpty()) {

            throw new IllegalArgumentException(
                    "Registration date is required."
            );
        }

        LocalDate registrationDate =
                LocalDate.parse(dateText);

        return new Supplier(
                supplierIdField.getText().trim(),
                supplierNameField.getText().trim(),
                telephoneField.getText().trim(),
                emailField.getText().trim(),
                addressArea.getText().trim(),
                registrationDate,
                (String) statusComboBox
                        .getSelectedItem()
        );
    }

    /**
     * Loads the supplier selected in the results table.
     */
    private void loadSelectedSupplier() {

        int selectedRow =
                supplierTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow =
                supplierTable.convertRowIndexToModel(
                        selectedRow
                );

        String supplierId =
                tableModel.getValueAt(
                        modelRow,
                        0
                ).toString();

        try {
            Supplier supplier =
                    supplierDAO.findSupplier(
                            supplierId
                    );

            if (supplier != null) {
                displaySupplier(supplier);
            }

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Displays a supplier on the form.
     *
     * @param supplier supplier to display
     */
    private void displaySupplier(
            Supplier supplier
    ) {

        supplierIdField.setText(
                supplier.getSupplierId()
        );

        supplierNameField.setText(
                supplier.getSupplierName()
        );

        telephoneField.setText(
                supplier.getTelephone()
        );

        emailField.setText(
                valueOrEmpty(
                        supplier.getEmail()
                )
        );

        addressArea.setText(
                valueOrEmpty(
                        supplier.getAddress()
                )
        );

        registrationDateField.setText(
                supplier.getRegistrationDate() == null
                        ? ""
                        : supplier.getRegistrationDate()
                                .toString()
        );

        statusComboBox.setSelectedItem(
                supplier.getSupplierStatus()
        );
    }

    /**
     * Reloads all suppliers into the table.
     *
     * @throws SQLException when the database operation fails
     */
    private void refreshSupplierTable()
            throws SQLException {

        tableModel.setRowCount(0);

        for (Supplier supplier
                : supplierDAO.findAllSuppliers()) {

            tableModel.addRow(
                    new Object[]{
                            supplier.getSupplierId(),
                            supplier.getSupplierName(),
                            supplier.getTelephone(),
                            supplier.getEmail(),
                            supplier.getAddress(),
                            supplier.getRegistrationDate(),
                            supplier.getSupplierStatus()
                    }
            );
        }
    }

    /**
     * Reloads the table and prepares a new record.
     */
    private void refreshEverything() {

        try {
            refreshSupplierTable();
            clearForm();

            showInformation(
                    "Supplier information refreshed."
            );

        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    /**
     * Clears the form and generates the next supplier ID.
     *
     * @throws SQLException when the ID cannot be generated
     */
    private void clearForm()
            throws SQLException {

        supplierIdField.setText(
                supplierDAO.generateNextSupplierId()
        );

        supplierNameField.setText("");
        telephoneField.setText("");
        emailField.setText("");
        addressArea.setText("");

        registrationDateField.setText(
                LocalDate.now().toString()
        );

        statusComboBox.setSelectedItem(
                "Active"
        );

        supplierTable.clearSelection();
        supplierNameField.requestFocus();
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
     * Converts a possible null string into an empty string.
     *
     * @param value possible null value
     * @return original value or an empty string
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
     * Opens the supplier form for independent testing.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            SupplierForm form =
                    new SupplierForm();

            form.setVisible(true);
        });
    }
}