package com.sasafashions.view.components;

import java.awt.Cursor;
import javax.swing.AbstractButton;
import javax.swing.JFrame;

/**
 * Provides reusable appearance and behaviour settings
 * for Sasa Fashions application forms.
 *
 * @author SASA Group
 * @version 1.0
 */
public final class FormStyler {

    /**
     * A private constructor prevents objects of this
     * utility class from being created.
     */
    private FormStyler() {
    }

    /**
     * Applies standard settings to an application frame.
     *
     * @param frame frame to prepare
     */
    public static void prepareFrame(JFrame frame) {

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        frame.setResizable(false);
    }

    /**
     * Applies a consistent appearance to buttons.
     *
     * The parameter uses a Java array through varargs,
     * allowing several buttons to be supplied at once.
     *
     * @param buttons buttons to style
     */
    public static void styleButtons(
            AbstractButton... buttons
    ) {

        for (AbstractButton button : buttons) {

            if (button != null) {

                button.setFocusPainted(false);

                button.setCursor(
                        Cursor.getPredefinedCursor(
                                Cursor.HAND_CURSOR
                        )
                );
            }
        }
    }
}