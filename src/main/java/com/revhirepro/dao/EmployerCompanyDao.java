package com.revhirepro.dao;

import java.sql.*;

public class EmployerCompanyDao {

    public void createCompany(Connection connection, long userId, String name, String industry, String size, String desc, String website, String location) throws SQLException {
        String sql = "INSERT INTO rh_employer_companies(user_id, company_name, industry, company_size, description, website, location) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, name);
            ps.setString(3, industry);
            ps.setString(4, size);
            ps.setString(5, desc);
            ps.setString(6, website);
            ps.setString(7, location);
            ps.executeUpdate();
        }
    }

    public void updateCompany(Connection connection, long userId, String name, String industry, String size, String desc, String website, String location) throws SQLException {
        String sql = "UPDATE rh_employer_companies SET company_name=?, industry=?, company_size=?, description=?, website=?, location=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, industry);
            ps.setString(3, size);
            ps.setString(4, desc);
            ps.setString(5, website);
            ps.setString(6, location);
            ps.setLong(7, userId);
            ps.executeUpdate();
        }
    }

    public ResultSet getCompany(Connection connection, long userId) throws SQLException {
        String sql = "SELECT company_name, industry, company_size, description, website, location, updated_at FROM rh_employer_companies WHERE user_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        return ps.executeQuery();
    }
}
