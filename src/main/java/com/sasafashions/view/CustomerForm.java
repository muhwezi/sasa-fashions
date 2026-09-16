/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.sasafashions.view;



import com.sasafashions.view.components.FormStyler;
import com.sasafashions.view.components.MessageDialog;
import com.sasafashions.dao.CustomerDAO;
import com.sasafashions.model.Customer;
import com.sasafashions.model.shared.ContactDetails;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.JOptionPane;
/**
 *
 * @author HP
 */
public class CustomerForm extends javax.swing.JFrame {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CustomerForm.class.getName());

    /**
     * Creates new form CustomerForm
     */
public CustomerForm() {

    initComponents();

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

    prepareNewCustomer();
}
    
    
    
/**
 * Requests confirmation before closing the form.
 */
private void closeForm() {

    boolean confirmed = MessageDialog.confirm(
            this,
            "Do you want to close the "
                    + "Customer Registration form?",
            "Confirm Close"
    );

    if (confirmed) {
        dispose();
    }
}
    
    private void showPreviousCustomer() {

    String currentCustomerId =
            txtCustomerId.getText().trim();

    if (currentCustomerId.isEmpty()) {

        showLastCustomer();
        return;
    }

    try {

        Customer customer =
                customerDAO.getPreviousCustomer(
                        currentCustomerId
                );

        if (customer != null) {

            displayCustomer(customer);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "You have reached the first customer.",
                    "Beginning of Records",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (SQLException error) {

        showNavigationError(error);
    }
}
    
    
    
    
/**
 * Displays an error encountered while navigating records.
 *
 * @param error database error
 */
private void showNavigationError(SQLException error) {

    MessageDialog.showError(
            this,
            "Unable to navigate customer records.\n"
                    + error.getMessage(),
            "Database Error"
    );
}
    
    
    
    
    
    
    
    
    private void showNextCustomer() {

    String currentCustomerId =
            txtCustomerId.getText().trim();

    if (currentCustomerId.isEmpty()) {

        showFirstCustomer();
        return;
    }

    try {

        Customer customer =
                customerDAO.getNextCustomer(
                        currentCustomerId
                );

        if (customer != null) {

            displayCustomer(customer);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "You have reached the last customer.",
                    "End of Records",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (SQLException error) {

        showNavigationError(error);
    }
}
    
    
    private void showLastCustomer() {

    try {

        Customer customer =
                customerDAO.getLastCustomer();

        if (customer != null) {

            displayCustomer(customer);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "There are no customers to display.",
                    "No Customers",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (SQLException error) {

        showNavigationError(error);
    }
}
    
    
    
    private void showFirstCustomer() {

    try {

        Customer customer =
                customerDAO.getFirstCustomer();

        if (customer != null) {

            displayCustomer(customer);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "There are no customers to display.",
                    "No Customers",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (SQLException error) {

        showNavigationError(error);
    }
}
    
    
    private void deleteCustomer() {

    String customerId =
            txtCustomerId.getText().trim();

    String customerName =
            txtCustomerName.getText().trim();

    if (customerId.isEmpty()
            || customerName.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Find a customer before deleting.",
                "No Customer Selected",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Do you want to delete this customer?\n\n"
                    + "Customer ID: " + customerId + "\n"
                    + "Customer Name: " + customerName,
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (confirmation != JOptionPane.YES_OPTION) {
        return;
    }

    try {

        boolean deleted =
                customerDAO.deleteCustomer(customerId);

        if (deleted) {

            JOptionPane.showMessageDialog(
                    this,
                    "Customer deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            prepareNewCustomer();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "The customer was not found.",
                    "Delete Failed",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    } catch (SQLException error) {

        // Error 1451 means another table is using this customer
        if (error.getErrorCode() == 1451) {

            JOptionPane.showMessageDialog(
                    this,
                    "This customer cannot be deleted because "
                            + "they have related records.",
                    "Customer In Use",
                    JOptionPane.WARNING_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete the customer.\n"
                            + error.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
    
    
    
    
    private void findCustomer() {

    String customerId = JOptionPane.showInputDialog(
            this,
            "Enter the customer ID:",
            "Find Customer",
            JOptionPane.QUESTION_MESSAGE
    );

    // The user pressed Cancel
    if (customerId == null) {
        return;
    }

    customerId = customerId.trim().toUpperCase();

    if (customerId.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter a customer ID.",
                "Missing Customer ID",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    try {

        Customer customer =
                customerDAO.findCustomer(customerId);

        if (customer != null) {

            displayCustomer(customer);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No customer was found with ID: "
                            + customerId,
                    "Customer Not Found",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (SQLException error) {

        JOptionPane.showMessageDialog(
                this,
                "Unable to find the customer.\n"
                        + error.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void displayCustomer(Customer customer) {

    txtCustomerId.setText(
            customer.getCustomerId()
    );

    txtCustomerName.setText(
            customer.getCustomerName()
    );

    txtTelephone.setText(
            customer.getTelephone()
    );

    cmbGender.setSelectedItem(
            customer.getGender()
    );

    txtAddress.setText(
            customer.getAddress()
    );

    txtRegistrationDate.setText(
            customer.getRegistrationDate().toString()
    );

    cmbStatus.setSelectedItem(
            customer.getCustomerStatus()
    );
}
    
    
    private void saveCustomer() {

    String customerId =
            txtCustomerId.getText().trim();

    String customerName =
            txtCustomerName.getText().trim();

    String telephone =
            txtTelephone.getText().trim();

    String gender =
            cmbGender.getSelectedItem().toString();

    String address =
            txtAddress.getText().trim();

    String customerStatus =
            cmbStatus.getSelectedItem().toString();

    // Validate customer name
    if (customerName.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter the customer name.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );

        txtCustomerName.requestFocus();
        return;
    }

    // Validate telephone
    if (telephone.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter the telephone number.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );

        txtTelephone.requestFocus();
        return;
    }

    // Accept 07XXXXXXXX or +2567XXXXXXXX
if (!ContactDetails.isValidUgandanTelephone(telephone)) {

        JOptionPane.showMessageDialog(
                this,
                "Enter a valid telephone number.\n"
                        + "Example: 0772000000 or +256772000000",
                "Invalid Telephone",
                JOptionPane.WARNING_MESSAGE
        );

        txtTelephone.requestFocus();
        return;
    }

telephone = ContactDetails.cleanTelephone(telephone);

    LocalDate registrationDate =
            LocalDate.parse(
                    txtRegistrationDate.getText()
            );

    Customer customer = new Customer(
            customerId,
            customerName,
            telephone,
            gender,
            address,
            registrationDate,
            customerStatus
    );

    try {

        boolean saved =
                customerDAO.saveCustomer(customer);

        if (saved) {

            JOptionPane.showMessageDialog(
                    this,
                    "Customer saved successfully.\n"
                            + "Customer ID: " + customerId,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            prepareNewCustomer();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "The customer was not saved.",
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (SQLException error) {

        showSaveError(error);
    }
}
    
    private void showSaveError(SQLException error) {

    // MySQL error 1062 means duplicate information
    if (error.getErrorCode() == 1062) {

        JOptionPane.showMessageDialog(
                this,
                "That customer ID or telephone number "
                        + "is already registered.",
                "Duplicate Customer",
                JOptionPane.WARNING_MESSAGE
        );

    } else {

        JOptionPane.showMessageDialog(
                this,
                "Unable to save the customer.\n"
                        + error.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    
private void prepareNewCustomer() {

    txtCustomerName.setText("");
    txtTelephone.setText("");
    txtAddress.setText("");

    cmbGender.setSelectedIndex(0);
    cmbStatus.setSelectedItem("Active");

    txtRegistrationDate.setText(
            LocalDate.now().toString()
    );

    try {

        String nextCustomerId =
                customerDAO.generateNextCustomerId();

        txtCustomerId.setText(nextCustomerId);

    } catch (SQLException error) {

        JOptionPane.showMessageDialog(
                this,
                "Unable to generate customer ID.\n"
                        + error.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    txtCustomerName.requestFocus();
}




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCustomerId = new javax.swing.JTextField();
        txtCustomerName = new javax.swing.JTextField();
        txtTelephone = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtRegistrationDate = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox<>();
        cmbGender = new javax.swing.JComboBox<>();
        btnNew = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitle.setText("SASA FASHIONS");

        lblSubtitle.setText("CUSTOMER REGISTRATION");

        jLabel1.setText("Customer ID");

        jLabel2.setText("Customer Name");

        jLabel3.setText("Telephone");

        jLabel4.setText("Gender");

        jLabel5.setText("Address");

        jLabel6.setText("Registration Date");

        jLabel7.setText("Customer Status");

        txtCustomerId.setEditable(false);
        txtCustomerId.addActionListener(this::txtCustomerIdActionPerformed);

        txtCustomerName.addActionListener(this::txtCustomerNameActionPerformed);

        txtTelephone.addActionListener(this::txtTelephoneActionPerformed);

        txtAddress.addActionListener(this::txtAddressActionPerformed);

        txtRegistrationDate.setEditable(false);
        txtRegistrationDate.addActionListener(this::txtRegistrationDateActionPerformed);

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));

        cmbGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        cmbGender.addActionListener(this::cmbGenderActionPerformed);

        btnNew.setText("NEW");
        btnNew.addActionListener(this::btnNewActionPerformed);

        btnFirst.setText("FIRST");
        btnFirst.addActionListener(this::btnFirstActionPerformed);

        btnSave.setText("SAVE");
        btnSave.addActionListener(this::btnSaveActionPerformed);

        btnFind.setText("FIND");
        btnFind.addActionListener(this::btnFindActionPerformed);

        btnEdit.setText("EDIT");
        btnEdit.addActionListener(this::btnEditActionPerformed);

        btnDelete.setText("DELETE");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);

        btnPrevious.setText("PREVIOUS");
        btnPrevious.addActionListener(this::btnPreviousActionPerformed);

        btnNext.setText("NEXT");
        btnNext.addActionListener(this::btnNextActionPerformed);

        btnLast.setText("LAST");
        btnLast.addActionListener(this::btnLastActionPerformed);

        btnClose.setText("CLOSE");
        btnClose.addActionListener(this::btnCloseActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(94, 94, 94)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnFirst)
                                    .addComponent(btnNew))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtCustomerId)
                                .addComponent(txtCustomerName)
                                .addComponent(txtTelephone)
                                .addComponent(txtAddress)
                                .addComponent(txtRegistrationDate, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                                .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSave)
                                .addGap(18, 18, 18)
                                .addComponent(btnFind)
                                .addGap(18, 18, 18)
                                .addComponent(btnEdit)
                                .addGap(18, 18, 18)
                                .addComponent(btnDelete))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnPrevious)
                                .addGap(18, 18, 18)
                                .addComponent(btnNext)
                                .addGap(18, 18, 18)
                                .addComponent(btnLast)
                                .addGap(18, 18, 18)
                                .addComponent(btnClose))))
                    .addComponent(lblSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(171, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addComponent(lblSubtitle)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtCustomerId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTelephone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtRegistrationDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNew)
                    .addComponent(btnSave)
                    .addComponent(btnFind)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFirst)
                    .addComponent(btnPrevious)
                    .addComponent(btnNext)
                    .addComponent(btnLast)
                    .addComponent(btnClose))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressActionPerformed

    private void txtRegistrationDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegistrationDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegistrationDateActionPerformed

    private void txtCustomerIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerIdActionPerformed

    private void txtTelephoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelephoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelephoneActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        prepareNewCustomer();
        
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveCustomer();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
        showFirstCustomer();
    }//GEN-LAST:event_btnFirstActionPerformed

    
    
    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        findCustomer();

// TODO add your handling code here:
    }//GEN-LAST:event_btnFindActionPerformed

    private void cmbGenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbGenderActionPerformed

    private void txtCustomerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerNameActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        editCustomer();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteCustomer();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        
        showLastCustomer();
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        
        
        showNextCustomer();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        // TODO add your handling code here:
        
        showPreviousCustomer();
        
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        closeForm();
        
    }//GEN-LAST:event_btnCloseActionPerformed

    
    
    private void editCustomer() {

    String customerId =
            txtCustomerId.getText().trim();

    String customerName =
            txtCustomerName.getText().trim();

    String telephone =
            txtTelephone.getText().trim();

    String gender =
            cmbGender.getSelectedItem().toString();

    String address =
            txtAddress.getText().trim();

    String customerStatus =
            cmbStatus.getSelectedItem().toString();

    if (customerId.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Find a customer before editing.",
                "No Customer Selected",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    if (customerName.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter the customer name.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );

        txtCustomerName.requestFocus();
        return;
    }

    if (telephone.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter the telephone number.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );

        txtTelephone.requestFocus();
        return;
    }

if (!ContactDetails.isValidUgandanTelephone(telephone)) {

        JOptionPane.showMessageDialog(
                this,
                "Enter a valid telephone number.\n"
                        + "Example: 0772000000 or +256772000000",
                "Invalid Telephone",
                JOptionPane.WARNING_MESSAGE
        );

        txtTelephone.requestFocus();
        return;
    }
telephone = ContactDetails.cleanTelephone(telephone);

    int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Save changes to customer "
                    + customerId + "?",
            "Confirm Edit",
            JOptionPane.YES_NO_OPTION
    );

    if (confirmation != JOptionPane.YES_OPTION) {
        return;
    }

    LocalDate registrationDate =
            LocalDate.parse(
                    txtRegistrationDate.getText()
            );

    Customer customer = new Customer(
            customerId,
            customerName,
            telephone,
            gender,
            address,
            registrationDate,
            customerStatus
    );

    try {

        boolean updated =
                customerDAO.updateCustomer(customer);

        if (updated) {

            JOptionPane.showMessageDialog(
                    this,
                    "Customer updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "The customer was not found.",
                    "Update Failed",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    } catch (SQLException error) {

        if (error.getErrorCode() == 1062) {

            JOptionPane.showMessageDialog(
                    this,
                    "That telephone number is already "
                            + "registered to another customer.",
                    "Duplicate Telephone",
                    JOptionPane.WARNING_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update the customer.\n"
                            + error.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}






    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new CustomerForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbGender;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCustomerId;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtRegistrationDate;
    private javax.swing.JTextField txtTelephone;
    // End of variables declaration//GEN-END:variables
}
