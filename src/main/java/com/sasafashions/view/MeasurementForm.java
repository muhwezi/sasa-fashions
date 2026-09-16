package com.sasafashions.view;

import com.sasafashions.dao.CustomerDAO;
import com.sasafashions.dao.MeasurementDAO;
import com.sasafashions.model.Customer;
import com.sasafashions.model.Measurement;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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
 * Provides the interface for recording customer measurements.
 *
 * @author HP
 * @version 1.0
 */
public class MeasurementForm extends JFrame {

    private final MeasurementDAO measurementDAO =
            new MeasurementDAO();

    private final CustomerDAO customerDAO =
            new CustomerDAO();

    private ArrayList<Measurement> measurementList =
            new ArrayList<>();

    private final JTextField txtMeasurementId =
            new JTextField(18);

    private final JComboBox<Customer> cmbCustomer =
            new JComboBox<>();

    private final JTextField txtChest =
            new JTextField(18);

    private final JTextField txtWaist =
            new JTextField(18);

    private final JTextField txtHip =
            new JTextField(18);

    private final JTextField txtShoulder =
            new JTextField(18);

    private final JTextField txtSleeveLength =
            new JTextField(18);

    private final JTextField txtTrouserLength =
            new JTextField(18);

    private final JTextField txtDateTaken =
            new JTextField(18);

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
                        "Measurement ID",
                        "Customer",
                        "Chest",
                        "Waist",
                        "Hip",
                        "Shoulder",
                        "Sleeve",
                        "Trouser",
                        "Date Taken"
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

    private final JTable tblMeasurements =
            new JTable(tableModel);

    /**
     * Creates and prepares the Measurement Form.
     */
    public MeasurementForm() {

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

        loadCustomers();
        prepareNewMeasurement();
        loadMeasurementTable();
    }

    /**
     * Creates and arranges the form components.
     */
    private void initialiseForm() {

        setTitle(
                "Sasa Fashions - Customer Measurements"
        );

        setSize(1250, 700);

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
                new JLabel("CUSTOMER MEASUREMENTS");

        lblSubheading.setFont(
                new Font("Arial", Font.BOLD, 16)
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
                new Insets(5, 5, 5, 5);

        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        addFormField(
                formPanel,
                constraints,
                0,
                "Measurement ID",
                txtMeasurementId
        );

        addFormField(
                formPanel,
                constraints,
                1,
                "Customer",
                cmbCustomer
        );

        addFormField(
                formPanel,
                constraints,
                2,
                "Chest",
                txtChest
        );

        addFormField(
                formPanel,
                constraints,
                3,
                "Waist",
                txtWaist
        );

        addFormField(
                formPanel,
                constraints,
                4,
                "Hip",
                txtHip
        );

        addFormField(
                formPanel,
                constraints,
                5,
                "Shoulder",
                txtShoulder
        );

        addFormField(
                formPanel,
                constraints,
                6,
                "Sleeve Length",
                txtSleeveLength
        );

        addFormField(
                formPanel,
                constraints,
                7,
                "Trouser Length",
                txtTrouserLength
        );

        addFormField(
                formPanel,
                constraints,
                8,
                "Date Taken",
                txtDateTaken
        );

        txtMeasurementId.setEditable(false);

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
        navigationButtons.add(btnClose);

        JPanel buttonPanel =
                new JPanel(new BorderLayout());

        buttonPanel.add(
                actionButtons,
                BorderLayout.NORTH
        );

        buttonPanel.add(
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
                buttonPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                formSection,
                BorderLayout.WEST
        );

        tblMeasurements.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tblMeasurements.setRowHeight(24);

        mainPanel.add(
                new JScrollPane(tblMeasurements),
                BorderLayout.CENTER
        );

        setContentPane(mainPanel);
    }

    /**
     * Adds a labelled component to the form.
     */
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
     * Attaches actions to buttons and the JTable.
     */
    private void attachEvents() {

        btnNew.addActionListener(
                event -> prepareNewMeasurement()
        );

        btnSave.addActionListener(
                event -> saveMeasurement()
        );

        btnFind.addActionListener(
                event -> findMeasurement()
        );

        btnEdit.addActionListener(
                event -> editMeasurement()
        );

        btnDelete.addActionListener(
                event -> deleteMeasurement()
        );

        btnFirst.addActionListener(
                event -> showFirstMeasurement()
        );

        btnPrevious.addActionListener(
                event -> showPreviousMeasurement()
        );

        btnNext.addActionListener(
                event -> showNextMeasurement()
        );

        btnLast.addActionListener(
                event -> showLastMeasurement()
        );

        btnClose.addActionListener(
                event -> closeForm()
        );

        tblMeasurements
                .getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        displaySelectedMeasurement();
                    }
                });
    }

    /**
     * Loads registered customers into the dropdown.
     */
    private void loadCustomers() {

        try {

            ArrayList<Customer> customers =
                    customerDAO.findAllCustomers();

            cmbCustomer.removeAllItems();

            for (Customer customer : customers) {
                cmbCustomer.addItem(customer);
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to load customers.",
                    error
            );
        }
    }

    /**
     * Clears the form and generates a measurement ID.
     */
    private void prepareNewMeasurement() {

        txtChest.setText("");
        txtWaist.setText("");
        txtHip.setText("");
        txtShoulder.setText("");
        txtSleeveLength.setText("");
        txtTrouserLength.setText("");

        txtDateTaken.setText(
                LocalDate.now().toString()
        );

        if (cmbCustomer.getItemCount() > 0) {
            cmbCustomer.setSelectedIndex(0);
        }

        try {

            txtMeasurementId.setText(
                    measurementDAO
                            .generateNextMeasurementId()
            );

        } catch (SQLException error) {

            txtMeasurementId.setText("");

            showDatabaseError(
                    "Unable to generate measurement ID.",
                    error
            );
        }

        tblMeasurements.clearSelection();
        txtChest.requestFocus();
    }

    /**
     * Validates the form and creates a Measurement.
     */
    private Measurement readMeasurementFromForm() {

        String measurementId =
                txtMeasurementId.getText().trim();

        Customer customer =
                (Customer) cmbCustomer
                        .getSelectedItem();

        if (measurementId.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "A measurement ID has not been generated.",
                    "Missing Measurement ID"
            );

            return null;
        }

        if (customer == null) {

            MessageDialog.showWarning(
                    this,
                    "Register a customer before "
                            + "recording measurements.",
                    "No Customer"
            );

            return null;
        }

        try {

            BigDecimal chest =
                    readMeasurementValue(
                            txtChest,
                            "Chest"
                    );

            BigDecimal waist =
                    readMeasurementValue(
                            txtWaist,
                            "Waist"
                    );

            BigDecimal hip =
                    readMeasurementValue(
                            txtHip,
                            "Hip"
                    );

            BigDecimal shoulder =
                    readMeasurementValue(
                            txtShoulder,
                            "Shoulder"
                    );

            BigDecimal sleeveLength =
                    readMeasurementValue(
                            txtSleeveLength,
                            "Sleeve length"
                    );

            BigDecimal trouserLength =
                    readMeasurementValue(
                            txtTrouserLength,
                            "Trouser length"
                    );

            if (chest == null
                    && waist == null
                    && hip == null
                    && shoulder == null
                    && sleeveLength == null
                    && trouserLength == null) {

                MessageDialog.showWarning(
                        this,
                        "Enter at least one measurement.",
                        "Missing Measurements"
                );

                return null;
            }

            LocalDate dateTaken;

            try {

                dateTaken = LocalDate.parse(
                        txtDateTaken
                                .getText()
                                .trim()
                );

            } catch (DateTimeParseException error) {

                MessageDialog.showWarning(
                        this,
                        "Enter the date in YYYY-MM-DD format.",
                        "Invalid Date"
                );

                txtDateTaken.requestFocus();
                return null;
            }

            return new Measurement(
                    measurementId,
                    customer.getCustomerId(),
                    chest,
                    waist,
                    hip,
                    shoulder,
                    sleeveLength,
                    trouserLength,
                    dateTaken
            );

        } catch (IllegalArgumentException error) {

            MessageDialog.showWarning(
                    this,
                    error.getMessage(),
                    "Invalid Measurement"
            );

            return null;
        }
    }

    /**
     * Reads one optional decimal measurement.
     */
    private BigDecimal readMeasurementValue(
            JTextField field,
            String fieldName
    ) {

        String text =
                field.getText().trim();

        if (text.isEmpty()) {
            return null;
        }

        try {

            BigDecimal value =
                    new BigDecimal(text)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            if (value.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        fieldName
                                + " must be greater than zero."
                );
            }

            return value;

        } catch (NumberFormatException error) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid number."
            );
        }
    }

    /**
     * Saves a new measurement record.
     */
    private void saveMeasurement() {

        Measurement measurement =
                readMeasurementFromForm();

        if (measurement == null) {
            return;
        }

        try {

            boolean saved =
                    measurementDAO.saveMeasurement(
                            measurement
                    );

            if (saved) {

                MessageDialog.showInformation(
                        this,
                        "Measurements saved successfully.",
                        "Success"
                );

                loadMeasurementTable();
                prepareNewMeasurement();
            }

        } catch (SQLException error) {

            if (error.getErrorCode() == 1062) {

                MessageDialog.showWarning(
                        this,
                        "The selected customer already has "
                                + "a measurement record.\n"
                                + "Find the record and use EDIT.",
                        "Measurements Already Exist"
                );

            } else {

                showDatabaseError(
                        "Unable to save measurements.",
                        error
                );
            }
        }
    }

    /**
     * Finds a measurement using its ID.
     */
    private void findMeasurement() {

        String measurementId =
                JOptionPane.showInputDialog(
                        this,
                        "Enter the measurement ID:",
                        "Find Measurements",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (measurementId == null) {
            return;
        }

        measurementId =
                measurementId.trim().toUpperCase();

        if (measurementId.isEmpty()) {
            return;
        }

        try {

            Measurement measurement =
                    measurementDAO.findMeasurement(
                            measurementId
                    );

            if (measurement != null) {

                displayMeasurement(measurement);

            } else {

                MessageDialog.showInformation(
                        this,
                        "No measurements were found for ID: "
                                + measurementId,
                        "Not Found"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to find measurements.",
                    error
            );
        }
    }

    /**
     * Updates the displayed measurement record.
     */
    private void editMeasurement() {

        Measurement measurement =
                readMeasurementFromForm();

        if (measurement == null) {
            return;
        }

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Save changes to "
                                + measurement
                                        .getMeasurementId()
                                + "?",
                        "Confirm Edit"
                );

        if (!confirmed) {
            return;
        }

        try {

            boolean updated =
                    measurementDAO.updateMeasurement(
                            measurement
                    );

            if (updated) {

                MessageDialog.showInformation(
                        this,
                        "Measurements updated successfully.",
                        "Success"
                );

                loadMeasurementTable();

            } else {

                MessageDialog.showWarning(
                        this,
                        "The measurement record was not found.",
                        "Update Failed"
                );
            }

        } catch (SQLException error) {

            if (error.getErrorCode() == 1062) {

                MessageDialog.showWarning(
                        this,
                        "The selected customer already has "
                                + "another measurement record.",
                        "Duplicate Customer"
                );

            } else {

                showDatabaseError(
                        "Unable to update measurements.",
                        error
                );
            }
        }
    }

    /**
     * Deletes the displayed measurement record.
     */
    private void deleteMeasurement() {

        String measurementId =
                txtMeasurementId.getText().trim();

        if (measurementId.isEmpty()) {

            MessageDialog.showWarning(
                    this,
                    "Select a measurement before deleting.",
                    "No Measurement Selected"
            );

            return;
        }

        boolean confirmed =
                MessageDialog.confirm(
                        this,
                        "Delete measurement "
                                + measurementId
                                + "?",
                        "Confirm Deletion"
                );

        if (!confirmed) {
            return;
        }

        try {

            boolean deleted =
                    measurementDAO.deleteMeasurement(
                            measurementId
                    );

            if (deleted) {

                MessageDialog.showInformation(
                        this,
                        "Measurement deleted successfully.",
                        "Success"
                );

                loadMeasurementTable();
                prepareNewMeasurement();

            } else {

                MessageDialog.showWarning(
                        this,
                        "The measurement was not found.",
                        "Delete Failed"
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to delete measurements.",
                    error
            );
        }
    }

    /**
     * Loads measurement records into the JTable.
     */
    private void loadMeasurementTable() {

        try {

            measurementList =
                    measurementDAO
                            .findAllMeasurements();

            tableModel.setRowCount(0);

            for (Measurement measurement
                    : measurementList) {

                tableModel.addRow(
                        new Object[]{
                            measurement
                                    .getMeasurementId(),
                            measurement.getCustomerId(),
                            measurement.getChest(),
                            measurement.getWaist(),
                            measurement.getHip(),
                            measurement.getShoulder(),
                            measurement.getSleeveLength(),
                            measurement.getTrouserLength(),
                            measurement.getDateTaken()
                        }
                );
            }

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to load measurements.",
                    error
            );
        }
    }

    /**
     * Displays the selected JTable measurement.
     */
    private void displaySelectedMeasurement() {

        int selectedRow =
                tblMeasurements.getSelectedRow();

        if (selectedRow < 0
                || selectedRow
                >= measurementList.size()) {
            return;
        }

        displayMeasurement(
                measurementList.get(selectedRow)
        );
    }

    /**
     * Displays a Measurement object on the form.
     */
    private void displayMeasurement(
            Measurement measurement
    ) {

        txtMeasurementId.setText(
                measurement.getMeasurementId()
        );

        selectCustomer(
                measurement.getCustomerId()
        );

        setDecimalText(
                txtChest,
                measurement.getChest()
        );

        setDecimalText(
                txtWaist,
                measurement.getWaist()
        );

        setDecimalText(
                txtHip,
                measurement.getHip()
        );

        setDecimalText(
                txtShoulder,
                measurement.getShoulder()
        );

        setDecimalText(
                txtSleeveLength,
                measurement.getSleeveLength()
        );

        setDecimalText(
                txtTrouserLength,
                measurement.getTrouserLength()
        );

        txtDateTaken.setText(
                measurement.getDateTaken().toString()
        );
    }

    /**
     * Selects a customer in the dropdown using an ID.
     */
    private void selectCustomer(String customerId) {

        for (int index = 0;
                index < cmbCustomer.getItemCount();
                index++) {

            Customer customer =
                    cmbCustomer.getItemAt(index);

            if (customer
                    .getCustomerId()
                    .equals(customerId)) {

                cmbCustomer.setSelectedIndex(index);
                return;
            }
        }
    }

    /**
     * Displays a decimal value or an empty field.
     */
    private void setDecimalText(
            JTextField field,
            BigDecimal value
    ) {

        field.setText(
                value == null
                        ? ""
                        : value.toPlainString()
        );
    }

    private void showFirstMeasurement() {

        try {

            displayNavigationResult(
                    measurementDAO
                            .getFirstMeasurement(),
                    "There are no measurements to display."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate measurements.",
                    error
            );
        }
    }

    private void showLastMeasurement() {

        try {

            displayNavigationResult(
                    measurementDAO
                            .getLastMeasurement(),
                    "There are no measurements to display."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate measurements.",
                    error
            );
        }
    }

    private void showNextMeasurement() {

        String currentId =
                txtMeasurementId.getText().trim();

        try {

            displayNavigationResult(
                    measurementDAO.getNextMeasurement(
                            currentId
                    ),
                    "You have reached the last record."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate measurements.",
                    error
            );
        }
    }

    private void showPreviousMeasurement() {

        String currentId =
                txtMeasurementId.getText().trim();

        try {

            displayNavigationResult(
                    measurementDAO
                            .getPreviousMeasurement(
                                    currentId
                            ),
                    "You have reached the first record."
            );

        } catch (SQLException error) {

            showDatabaseError(
                    "Unable to navigate measurements.",
                    error
            );
        }
    }

    private void displayNavigationResult(
            Measurement measurement,
            String message
    ) {

        if (measurement != null) {

            displayMeasurement(measurement);

        } else {

            MessageDialog.showInformation(
                    this,
                    message,
                    "Measurement Navigation"
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
                        + "Customer Measurements?",
                "Confirm Close"
        )) {
            dispose();
        }
    }

    /**
     * Starts the Measurement Form.
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(
                () -> new MeasurementForm()
                        .setVisible(true)
        );
    }
}