package com.revhirepro.dao;

import com.revhirepro.model.NotificationCategory;

import java.sql.*;

public class NotificationDao {

    public void create(Connection connection, long userId, NotificationCategory category, String message) throws SQLException {
        String sql = "INSERT INTO rh_notifications(user_id, category, message) VALUES(?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, category.name());
            ps.setString(3, message);
            ps.executeUpdate();
        }
    }

    public ResultSet listRecent(Connection connection, long userId, int limit) throws SQLException {
        String sql = "SELECT notification_id, category, message, is_read, created_at FROM rh_notifications WHERE user_id=? ORDER BY created_at DESC LIMIT ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ps.setInt(2, limit);
        return ps.executeQuery();
    }

    public void markAllRead(Connection connection, long userId) throws SQLException {
        String sql = "UPDATE rh_notifications SET is_read=TRUE WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}
