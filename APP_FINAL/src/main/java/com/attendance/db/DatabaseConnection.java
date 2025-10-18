package com.attendance.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static Connection cachedConnection;

    private DatabaseConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (cachedConnection != null && !cachedConnection.isClosed()) {
            return cachedConnection;
        }
        Properties properties = new Properties();
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new IllegalStateException("application.properties not found in classpath");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load DB properties", e);
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.password");
        cachedConnection = DriverManager.getConnection(url, user, pass);
        return cachedConnection;
    }
}


