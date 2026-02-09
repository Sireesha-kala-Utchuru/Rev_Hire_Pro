package com.revhirepro.service;

import com.revhirepro.dao.NotificationDao;
import com.revhirepro.util.Jdbc;

import java.sql.Connection;
import java.sql.ResultSet;

public class NotificationService {
    private final NotificationDao dao = new NotificationDao();

    public void listRecent(long userId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            try (ResultSet rs = dao.listRecent(connection, userId, 20)) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println(
                            rs.getLong("notification_id") +
                            " | " + rs.getString("category") +
                            " | " + rs.getString("message") +
                            " | read=" + rs.getBoolean("is_read") +
                            " | " + rs.getTimestamp("created_at")
                    );
                }
                if (!any) System.out.println("No notifications.");
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("List notifications failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void markAllRead(long userId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            dao.markAllRead(connection, userId);
            connection.commit();
            System.out.println("All notifications marked as read.");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Mark read failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }
}
