package com.revhirepro.dao;

import java.sql.*;

public class JobSeekerProfileDao {

    public void createEmpty(Connection connection, long userId) throws SQLException {
        String sql = "INSERT INTO rh_job_seeker_profiles(user_id, experience_years) VALUES(?, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    public void upsertProfile(Connection connection, long userId, String location, int expYears, String skills) throws SQLException {
        String sql =
                "INSERT INTO rh_job_seeker_profiles(user_id, location, experience_years, skills) VALUES(?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE location=VALUES(location), experience_years=VALUES(experience_years), skills=VALUES(skills)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, location);
            ps.setInt(3, expYears);
            ps.setString(4, skills);
            ps.executeUpdate();
        }
    }

    public void saveResume(Connection connection, long userId, String resumeText) throws SQLException {
        String sql = "UPDATE rh_job_seeker_profiles SET resume_text=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, resumeText);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public ResultSet getProfile(Connection connection, long userId) throws SQLException {
        String sql = "SELECT user_id, location, experience_years, skills, resume_text, updated_at FROM rh_job_seeker_profiles WHERE user_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        return ps.executeQuery();
    }
}
