package com.revhirepro.dao;

import com.revhirepro.model.Role;
import com.revhirepro.model.User;

import java.sql.*;

public class UserDao {
    public long insert(Connection connection, Role role, String fullName, String email, String phone, String passwordHash) throws SQLException {
        String sql = "INSERT INTO rh_users(role, full_name, email, phone, password_hash) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.name());
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, passwordHash);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Insert user failed");
    }

    public User findByLogin(Connection connection, String login) throws SQLException {
        String sql = "SELECT user_id, role, full_name, email, phone, is_locked FROM rh_users WHERE email=? OR phone=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(
                        rs.getLong("user_id"),
                        Role.valueOf(rs.getString("role")),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getBoolean("is_locked")
                );
            }
        }
    }

    public String getPasswordHash(Connection connection, long userId) throws SQLException {
        String sql = "SELECT password_hash FROM rh_users WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1);
            }
        }
    }

    public void updatePassword(Connection connection, long userId, String newHash) throws SQLException {
        String sql = "UPDATE rh_users SET password_hash=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void incFailedAttempts(Connection connection, long userId) throws SQLException {
        String sql = "UPDATE rh_users SET failed_attempts = failed_attempts + 1 WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    public int getFailedAttempts(Connection connection, long userId) throws SQLException {
        String sql = "SELECT failed_attempts FROM rh_users WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return 0;
                return rs.getInt(1);
            }
        }
    }

    public void resetFailedAttempts(Connection connection, long userId) throws SQLException {
        String sql = "UPDATE rh_users SET failed_attempts=0 WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    public void lock(Connection connection, long userId) throws SQLException {
        String sql = "UPDATE rh_users SET is_locked=TRUE WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}
