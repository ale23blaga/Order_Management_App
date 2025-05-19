package org.example.Connection;

import java.sql.*;
import java.util.logging.Logger;

/**
 * Manages database connections using a singleton pattern.
 * Provides methods to open and close connections, statements, and result sets.
 * Automatically loads the MySQL JDBC driver.
 */
public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/ordersdb";
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

    /**
     * Creates a connection to the database.
     * @return an active {@link java.sql.Connection} or {@code null} if the connection fails
     */
    private Connection createConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(DBURL, USER, PASS);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Returns a new connection to the database.
     * @return an active {@link java.sql.Connection} or {@code null} if the connection fails
     */
    public static Connection getConnection(){
        return signleInstance.createConnection();
    }

    /**
     * Safely closes a {@link java.sql.Connection}, suppressing any SQL exceptions.
     * @param connection the connection to close
     */
    public static void close (Connection connection){
        try{
            if (connection != null){
                connection.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Safely closes a {@link java.sql.Statement}, suppressing any SQL exceptions.
     * @param statement the statement to close
     */
    public static void close(Statement statement){
        try{
            if (statement != null){
                statement.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Safely closes a {@link java.sql.ResultSet}, suppressing any SQL exceptions.
     * @param resultSet the result set to close
     */
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
