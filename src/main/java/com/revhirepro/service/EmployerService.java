package com.revhirepro.service;

import com.revhirepro.dao.EmployerCompanyDao;
import com.revhirepro.util.Jdbc;

import java.sql.Connection;
import java.sql.ResultSet;

public class EmployerService {
    private final EmployerCompanyDao dao = new EmployerCompanyDao();

    public void createCompany(long userId, String name, String industry, String size, String desc, String website, String location) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            dao.createCompany(connection, userId, name, industry, size, desc, website, location);
            connection.commit();
            System.out.println("Company profile created.");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Create company failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void updateCompany(long userId, String name, String industry, String size, String desc, String website, String location) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            dao.updateCompany(connection, userId, name, industry, size, desc, website, location);
            connection.commit();
            System.out.println("Company profile updated.");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Update company failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void viewCompany(long userId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            try (ResultSet rs = dao.getCompany(connection, userId)) {
                if (!rs.next()) { System.out.println("Company profile not found."); return; }
                System.out.println("Company: " + rs.getString("company_name"));
                System.out.println("Industry: " + rs.getString("industry"));
                System.out.println("Size: " + rs.getString("company_size"));
                System.out.println("Website: " + rs.getString("website"));
                System.out.println("Location: " + rs.getString("location"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Updated At: " + rs.getTimestamp("updated_at"));
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("View company failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }
}
