package com.revhirepro.dao;

import java.sql.*;

public class ApplicationDao {

    public long apply(Connection connection, long jobId, long jobSeekerId, String coverLetter) throws SQLException {
        String sql = "INSERT INTO rh_applications(job_id, job_seeker_user_id, cover_letter) VALUES(?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, jobId);
            ps.setLong(2, jobSeekerId);
            ps.setString(3, coverLetter);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Apply failed");
    }

    public ResultSet listForJobSeeker(Connection connection, long jobSeekerId) throws SQLException {
        String sql = "SELECT a.application_id, a.job_id, j.title, a.status, a.created_at, a.updated_at " +
                "FROM rh_applications a JOIN rh_jobs j ON a.job_id=j.job_id " +
                "WHERE a.job_seeker_user_id=? ORDER BY a.created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, jobSeekerId);
        return ps.executeQuery();
    }

    public void withdraw(Connection connection, long applicationId, long jobSeekerId) throws SQLException {
        String sql = "UPDATE rh_applications SET status='WITHDRAWN' WHERE application_id=? AND job_seeker_user_id=? AND status IN ('APPLIED','SHORTLISTED')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, applicationId);
            ps.setLong(2, jobSeekerId);
            ps.executeUpdate();
        }
    }

    public ResultSet listApplicantsForJob(Connection connection, long employerId, long jobId) throws SQLException {
        String sql =
                "SELECT a.application_id, a.status, a.created_at, u.user_id, u.full_name, u.email, u.phone, p.location, p.experience_years, p.skills " +
                "FROM rh_applications a " +
                "JOIN rh_jobs j ON a.job_id=j.job_id " +
                "JOIN rh_users u ON a.job_seeker_user_id=u.user_id " +
                "LEFT JOIN rh_job_seeker_profiles p ON p.user_id=u.user_id " +
                "WHERE j.employer_user_id=? AND j.job_id=? " +
                "ORDER BY a.created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, employerId);
        ps.setLong(2, jobId);
        return ps.executeQuery();
    }

    public AppRow findForEmployer(Connection connection, long applicationId, long employerId) throws SQLException {
        String sql =
                "SELECT a.application_id, a.job_id, a.job_seeker_user_id, a.status " +
                "FROM rh_applications a JOIN rh_jobs j ON a.job_id=j.job_id " +
                "WHERE a.application_id=? AND j.employer_user_id=? FOR UPDATE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, applicationId);
            ps.setLong(2, employerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new AppRow(rs.getLong("application_id"), rs.getLong("job_id"), rs.getLong("job_seeker_user_id"), rs.getString("status"));
            }
        }
    }

    public void setStatus(Connection connection, long applicationId, String newStatus) throws SQLException {
        String sql = "UPDATE rh_applications SET status=? WHERE application_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, applicationId);
            ps.executeUpdate();
        }
    }

    public static class AppRow {
        public final long applicationId;
        public final long jobId;
        public final long jobSeekerId;
        public final String status;

        public AppRow(long applicationId, long jobId, long jobSeekerId, String status) {
            this.applicationId = applicationId;
            this.jobId = jobId;
            this.jobSeekerId = jobSeekerId;
            this.status = status;
        }
    }
}
