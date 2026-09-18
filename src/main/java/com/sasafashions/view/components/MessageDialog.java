package com.sasafashions.view.components;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Provides reusable message windows for the application.
 *
 * @author SASA Group
 * @version 1.0
 */
public final class MessageDialog {

    private MessageDialog() {
    }

    /**
     * Displays an information message.
     *
     * @param parent parent form
     * @param message message to display
     * @param title dialog title
     */
    public static void showInformation(
            Component parent,
            String message,
            String title
    ) {

        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a warning message.
     *
     * @param parent parent form
     * @param message warning to display
     * @param title dialog title
     */
    public static void showWarning(
            Component parent,
            String message,
            String title
    ) {

        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Displays an error message.
     *
     * @param parent parent form
     * @param message error to display
     * @param title dialog title
     */
    public static void showError(
            Component parent,
            String message,
            String title
    ) {

        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays a Yes or No confirmation message.
     *
     * @param parent parent form
     * @param message confirmation question
     * @param title dialog title
     * @return true when the user chooses Yes
     */
    public static boolean confirm(
            Component parent,
            String message,
            String title
    ) {

        int answer = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return answer == JOptionPane.YES_OPTION;
    }
}