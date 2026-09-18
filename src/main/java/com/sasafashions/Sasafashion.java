package com.sasafashions;

import com.sasafashions.view.LoginForm;

import javax.swing.*;

/**
 * Main entry point for the Sasa Fashions Management System.
 *
 * @author SASA Group
 */
public class Sasafashion {

    /**
     * Starts the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (
                    ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException
                    | UnsupportedLookAndFeelException exception
            ) {
                System.err.println(
                        "Unable to load system appearance: "
                                + exception.getMessage()
                );
            }

            new LoginForm().setVisible(true);
        });
    }
}