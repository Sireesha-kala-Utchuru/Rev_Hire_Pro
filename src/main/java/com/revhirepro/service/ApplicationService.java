package com.revhirepro.service;

import com.revhirepro.dao.ApplicationDao;
import com.revhirepro.dao.NotificationDao;
import com.revhirepro.model.NotificationCategory;
import com.revhirepro.util.Jdbc;

import java.sql.Connection;
import java.sql.ResultSet;

public class ApplicationService {

    private final ApplicationDao dao = new ApplicationDao();
    private final NotificationDao notificationDao = new NotificationDao();

    public long apply(long jobId, long jobSeekerId, String coverLetter) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            long applicationId = dao.apply(connection, jobId, jobSeekerId, coverLetter);

            notificationDao.create(connection, jobSeekerId, NotificationCategory.APPLICATION,
                    "Applied successfully. applicationId=" + applicationId + ", jobId=" + jobId);

            connection.commit();
            System.out.println("Applied successfully. applicationId=" + applicationId);
            return applicationId;
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Apply failed: " + ex.getMessage());
            return -1;
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void myApplications(long jobSeekerId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            try (ResultSet rs = dao.listForJobSeeker(connection, jobSeekerId)) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println(
                            "appId=" + rs.getLong("application_id") +
                            " | jobId=" + rs.getLong("job_id") +
                            " | " + rs.getString("title") +
                            " | status=" + rs.getString("status") +
                            " | appliedAt=" + rs.getTimestamp("created_at") +
                            " | updatedAt=" + rs.getTimestamp("updated_at")
                    );
                }
                if (!any) System.out.println("No applications yet.");
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("List applications failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void withdraw(long applicationId, long jobSeekerId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            dao.withdraw(connection, applicationId, jobSeekerId);
            notificationDao.create(connection, jobSeekerId, NotificationCategory.APPLICATION,
                    "Application withdrawn. applicationId=" + applicationId);
            connection.commit();
            System.out.println("Withdraw request processed.");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Withdraw failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void applicantsForJob(long employerId, long jobId) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            try (ResultSet rs = dao.listApplicantsForJob(connection, employerId, jobId)) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println(
                            "appId=" + rs.getLong("application_id") +
                            " | status=" + rs.getString("status") +
                            " | seekerId=" + rs.getLong("user_id") +
                            " | " + rs.getString("full_name") +
                            " | expYears=" + rs.getInt("experience_years") +
                            " | loc=" + rs.getString("location") +
                            " | skills=" + rs.getString("skills") +
                            " | appliedAt=" + rs.getTimestamp("created_at")
                    );
                }
                if (!any) System.out.println("No applicants for this job.");
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("View applicants failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void updateStatus(long employerId, long applicationId, String newStatus) {
        Connection connection = null;
        try {
            connection = Jdbc.getConnection();
            ApplicationDao.AppRow row = dao.findForEmployer(connection, applicationId, employerId);
            if (row == null) { System.out.println("Application not found for your jobs."); return; }

            if ("WITHDRAWN".equals(row.status)) { System.out.println("Application already withdrawn."); return; }

            dao.setStatus(connection, applicationId, newStatus);
            notificationDao.create(connection, row.jobSeekerId, NotificationCategory.APPLICATION,
                    "Your applicationId=" + applicationId + " status updated to " + newStatus);

            connection.commit();
            System.out.println("Status updated.");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Update status failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }
}
