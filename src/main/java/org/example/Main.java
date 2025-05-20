package org.example;

import org.example.PresentatoinLayer.MainFrame;

import javax.swing.*;

/**
 * Entry point of the Order Management application.
 * <p>
 * Launches the GUI by creating and displaying the {@link MainFrame}.
 */
public class Main {

    /**
     * Main method that starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

            MainFrame frame = new MainFrame();
            frame.setVisible(true);

    }
}
