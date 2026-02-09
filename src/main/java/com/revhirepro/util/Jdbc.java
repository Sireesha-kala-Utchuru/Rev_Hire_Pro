package com.revhirepro.util;

import com.revhirepro.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Jdbc {
    private Jdbc() {}

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                AppConfig.get("db.url"),
                AppConfig.get("db.user"),
                AppConfig.get("db.password")
        );
        connection.setAutoCommit(false);
        return connection;
    }

    public static void rollbackQuietly(Connection connection) {
        if (connection == null) return;
        try { connection.rollback(); }
        catch (Exception ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
    }

    public static void closeQuietly(Connection connection) {
        if (connection == null) return;
        try { connection.close(); }
        catch (Exception ex) { System.err.println("Close failed: " + ex.getMessage()); }
    }
}
