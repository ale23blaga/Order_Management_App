package org.example;

import org.example.Connection.ConnectionFactory;
import org.example.Model.Student;
import org.example.PresentatoinLayer.MainFrame;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    private final static String findStatementString = "SELECT * FROM student WHERE id = ?";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
