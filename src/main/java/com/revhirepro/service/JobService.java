package com.revhirepro.service;

import com.revhirepro.dao.JobDao;
import com.revhirepro.util.ConnectionProvider;
import com.revhirepro.util.Jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;

public class JobService {

    private final JobDao jobDao;
    private final ConnectionProvider connectionProvider;

    public JobService() {
        this(new JobDao(), Jdbc::getConnection);
    }

    public JobService(JobDao jobDao, ConnectionProvider connectionProvider) {
        this.jobDao = jobDao;
        this.connectionProvider = connectionProvider;
    }

    public long createJob(long employerId, String title, String desc, String skills, int minExp, String education,
                          String location, Integer salaryMin, Integer salaryMax, String jobType, Date deadline) {
        Connection connection = null;
        try {
            connection = connectionProvider.get();
            long jobId = jobDao.create(connection, employerId, title, desc, skills, minExp, education, location, salaryMin, salaryMax, jobType, deadline);
            connection.commit();
            System.out.println("Job created. jobId=" + jobId);
            return jobId;
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Create job failed: " + ex.getMessage());
            return -1;
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void listMyJobs(long employerId) {
        Connection connection = null;
        try {
            connection = connectionProvider.get();
            try (ResultSet rs = jobDao.listByEmployer(connection, employerId)) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println(
                            "jobId=" + rs.getLong("job_id") +
                            " | " + rs.getString("title") +
                            " | " + rs.getString("location") +
                            " | " + rs.getString("job_type") +
                            " | status=" + rs.getString("status") +
                            " | " + rs.getTimestamp("created_at")
                    );
                }
                if (!any) System.out.println("No jobs posted yet.");
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("List jobs failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void closeJob(long employerId, long jobId) {
        setStatus(employerId, jobId, "CLOSED");
    }

    public void openJob(long employerId, long jobId) {
        setStatus(employerId, jobId, "OPEN");
    }

    private void setStatus(long employerId, long jobId, String status) {
        Connection connection = null;
        try {
            connection = connectionProvider.get();
            jobDao.setStatus(connection, jobId, employerId, status);
            connection.commit();
            System.out.println("Job status updated to " + status + ".");
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Update job status failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void searchJobs(String roleTitle, String location, Integer expYears, String company, Integer salaryMin, String jobType) {
        Connection connection = null;
        try {
            connection = connectionProvider.get();
            try (ResultSet rs = jobDao.searchOpen(connection, roleTitle, location, expYears, company, salaryMin, jobType)) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println(
                            "jobId=" + rs.getLong("job_id") +
                            " | " + rs.getString("title") +
                            " | company=" + rs.getString("company_name") +
                            " | loc=" + rs.getString("location") +
                            " | exp<= " + rs.getInt("min_experience_years") +
                            " | salary=" + rs.getObject("salary_min") + " - " + rs.getObject("salary_max") +
                            " | type=" + rs.getString("job_type") +
                            " | deadline=" + rs.getObject("deadline")
                    );
                }
                if (!any) System.out.println("No matching jobs.");
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Search jobs failed: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public boolean jobIsOpen(long jobId) {
        Connection connection = null;
        try {
            connection = connectionProvider.get();
            boolean ok = jobDao.existsOpenJob(connection, jobId);
            connection.rollback();
            return ok;
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            return false;
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }
}
