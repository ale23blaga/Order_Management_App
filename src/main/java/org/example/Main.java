package org.example;

import org.example.Model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    private final static String findStatementString = "SELECT * FROM student WHERE id = ?";

    public static void main(String[] args) {
        Student s = findByID(1);
        if (s != null) {
            System.out.println("Student found: " + s.getName());
        } else {
            System.out.println("Student not found.");
        }
    }

    public static Student findByID(int studentId) {
        Student toReturn = null;
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement findStatement = null;
        ResultSet rs = null;

        try {
            findStatement = dbConnection.prepareStatement(findStatementString);
            findStatement.setInt(1, studentId);
            rs = findStatement.executeQuery();

            if (rs.next()) {
                toReturn = new Student();
                toReturn.setId(rs.getInt("id"));
                toReturn.setName(rs.getString("name"));
                toReturn.setAddress(rs.getString("address"));
                toReturn.setEmail(rs.getString("email"));
                toReturn.setAge(rs.getInt("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(rs);
            ConnectionFactory.close(findStatement);
            ConnectionFactory.close(dbConnection);
        }

        return toReturn;
    }
}
