package org.example.Connection;

import java.sql.*;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/schooldb";
    private static final String USER = "root";
    private static final String PASS = "ale230404";

    private static ConnectionFactory signleInstance = new ConnectionFactory();

    private ConnectionFactory() {
        try{
            Class.forName(DRIVER);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection createConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(DBURL, USER, PASS);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection(){
        return signleInstance.createConnection();
    }

    public static void close (Connection connection){
        try{
            if (connection != null){
                connection.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void close(Statement statement){
        try{
            if (statement != null){
                statement.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void close(ResultSet resultSet){
        try{
            if (resultSet != null){
                resultSet.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
