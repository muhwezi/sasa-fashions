/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sasafashions;

import com.sasafashions.view.CustomerForm;
import javax.swing.SwingUtilities;

public class Sasafashion {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            CustomerForm customerForm = new CustomerForm();
            customerForm.setLocationRelativeTo(null);
            customerForm.setVisible(true);
        });
    }
}