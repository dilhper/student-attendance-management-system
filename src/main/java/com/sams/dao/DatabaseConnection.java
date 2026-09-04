package com.sams.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the MySQL database connection.
 * Reads connection details from db.properties on the classpath.
 * Maintains a single connection instance for the desktop application lifetime.
 */
public class DatabaseConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASS;
    private static Connection connection;

    static {
        Properties props = new Properties();
        try (InputStream is = DatabaseConnection.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load db.properties — using defaults.");
        }
        URL  = props.getProperty("db.url",      "jdbc:mysql://localhost:3306/sams_db");
        USER = props.getProperty("db.username",  "root");
        PASS = props.getProperty("db.password",  "");
    }

    private DatabaseConnection() { /* utility class */ }

    /**
     * Returns a reusable Connection. Re-creates it if closed or null.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }
}
