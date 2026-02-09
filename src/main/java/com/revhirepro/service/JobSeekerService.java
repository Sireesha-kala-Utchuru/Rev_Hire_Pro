package com.revhirepro.service;

import com.revhirepro.dao.JobSeekerProfileDao;
import com.revhirepro.util.Jdbc;

import java.sql.Connection;
import java.sql.ResultSet;

public class JobSeekerService {
    private final JobSeekerProfileDao dao = new JobSeekerProfileDao();

    public void createEmptyProfile(long userId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            dao.createEmpty(connection, userId);
            connection.commit();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Create profile failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void updateProfile(long userId, String location, int expYears, String skills) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            dao.upsertProfile(connection, userId, location, expYears, skills);
            connection.commit();
            System.out.println("Profile updated.");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Update profile failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void saveResume(long userId, String resumeText) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            dao.saveResume(connection, userId, resumeText);
            connection.commit();
            System.out.println("Resume saved.");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Save resume failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void viewProfile(long userId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            try (ResultSet rs = dao.getProfile(connection, userId)) {
                if (!rs.next()) { System.out.println("Profile not found."); return; }
                System.out.println("Location: " + rs.getString("location"));
                System.out.println("Experience Years: " + rs.getInt("experience_years"));
                System.out.println("Skills: " + rs.getString("skills"));
                String resume = rs.getString("resume_text");
                if (resume == null || resume.isBlank()) System.out.println("Resume: (not created)");
                else System.out.println("Resume:\n" + resume);
                System.out.println("Updated At: " + rs.getTimestamp("updated_at"));
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("View profile failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }
}
