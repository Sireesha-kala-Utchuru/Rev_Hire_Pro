package com.revhirepro.dao;

import java.sql.*;

public class JobDao {

    public long create(Connection connection, long employerUserId, String title, String description, String skills,
                       int minExp, String education, String location, Integer salaryMin, Integer salaryMax,
                       String jobType, java.sql.Date deadline) throws SQLException {
        String sql = "INSERT INTO rh_jobs(employer_user_id, title, description, skills, min_experience_years, education, location, salary_min, salary_max, job_type, deadline) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, employerUserId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, skills);
            ps.setInt(5, minExp);
            ps.setString(6, education);
            ps.setString(7, location);
            if (salaryMin == null) ps.setNull(8, Types.INTEGER); else ps.setInt(8, salaryMin);
            if (salaryMax == null) ps.setNull(9, Types.INTEGER); else ps.setInt(9, salaryMax);
            ps.setString(10, jobType);
            if (deadline == null) ps.setNull(11, Types.DATE); else ps.setDate(11, deadline);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Create job failed");
    }

    public void setStatus(Connection connection, long jobId, long employerUserId, String status) throws SQLException {
        String sql = "UPDATE rh_jobs SET status=? WHERE job_id=? AND employer_user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, jobId);
            ps.setLong(3, employerUserId);
            ps.executeUpdate();
        }
    }

    public ResultSet listByEmployer(Connection connection, long employerUserId) throws SQLException {
        String sql = "SELECT job_id, title, location, job_type, status, created_at FROM rh_jobs WHERE employer_user_id=? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, employerUserId);
        return ps.executeQuery();
    }

    public ResultSet searchOpen(Connection connection, String roleTitle, String location, Integer expYears,
                                String companyNameLike, Integer salaryMin, String jobType) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT j.job_id, j.title, ec.company_name, j.location, j.min_experience_years, j.salary_min, j.salary_max, j.job_type, j.deadline ");
        sb.append("FROM rh_jobs j JOIN rh_employer_companies ec ON j.employer_user_id = ec.user_id ");
        sb.append("WHERE j.status='OPEN' ");

        if (roleTitle != null && !roleTitle.isBlank()) sb.append("AND j.title LIKE ? ");
        if (location != null && !location.isBlank()) sb.append("AND j.location LIKE ? ");
        if (expYears != null) sb.append("AND j.min_experience_years <= ? ");
        if (companyNameLike != null && !companyNameLike.isBlank()) sb.append("AND ec.company_name LIKE ? ");
        if (salaryMin != null) sb.append("AND (j.salary_max IS NULL OR j.salary_max >= ?) ");
        if (jobType != null && !jobType.isBlank()) sb.append("AND j.job_type = ? ");
        sb.append("ORDER BY j.created_at DESC LIMIT 50");

        PreparedStatement ps = connection.prepareStatement(sb.toString());
        int i = 1;
        if (roleTitle != null && !roleTitle.isBlank()) ps.setString(i++, "%" + roleTitle + "%");
        if (location != null && !location.isBlank()) ps.setString(i++, "%" + location + "%");
        if (expYears != null) ps.setInt(i++, expYears);
        if (companyNameLike != null && !companyNameLike.isBlank()) ps.setString(i++, "%" + companyNameLike + "%");
        if (salaryMin != null) ps.setInt(i++, salaryMin);
        if (jobType != null && !jobType.isBlank()) ps.setString(i++, jobType);

        return ps.executeQuery();
    }

    public boolean existsOpenJob(Connection connection, long jobId) throws SQLException {
        String sql = "SELECT 1 FROM rh_jobs WHERE job_id=? AND status='OPEN'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
