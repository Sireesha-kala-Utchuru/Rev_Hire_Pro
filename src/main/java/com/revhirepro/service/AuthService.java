package com.revhirepro.service;

import com.revhirepro.dao.SecurityDao;
import com.revhirepro.dao.UserDao;
import com.revhirepro.model.Role;
import com.revhirepro.model.User;
import com.revhirepro.util.ConnectionProvider;
import com.revhirepro.util.Jdbc;
import com.revhirepro.util.PasswordUtil;

import java.sql.Connection;
import java.sql.ResultSet;

public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserDao userDao;
    private final SecurityDao securityDao;
    private final ConnectionProvider connectionProvider;

    public AuthService() {
        this(new UserDao(), new SecurityDao(), Jdbc::getConnection);
    }

    public AuthService(UserDao userDao, SecurityDao securityDao, ConnectionProvider connectionProvider) {
        this.userDao = userDao;
        this.securityDao = securityDao;
        this.connectionProvider = connectionProvider;
    }

    public User register(Role role, String fullName, String email, String phone, String password,
                         int question1Id, String answer1, int question2Id, String answer2) {

        Connection connection = null;
        try {
            connection = connectionProvider.get();

            long userId = userDao.insert(connection, role, fullName, email, phone, PasswordUtil.hash(password));
            securityDao.saveAnswer(connection, userId, question1Id, PasswordUtil.hash(answer1));
            securityDao.saveAnswer(connection, userId, question2Id, PasswordUtil.hash(answer2));

            connection.commit();
            System.out.println("Registration success. You can login now.");
            return userDao.findByLogin(connection, email);
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Registration failed: " + ex.getMessage());
            return null;
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public void showQuestions() {
        Connection connection = null;
        try {
            connection = connectionProvider.get();
            try (ResultSet resultSet = securityDao.listQuestions(connection)) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt("question_id") + ": " + resultSet.getString("question_text"));
                }
            }
            connection.rollback();
        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Failed to load questions: " + ex.getMessage());
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public User login(String login, String password) {
        Connection connection = null;
        try {
            connection = connectionProvider.get();

            User user = userDao.findByLogin(connection, login);
            if (user == null) { System.out.println("User not found."); return null; }
            if (user.isLocked()) { System.out.println("Account locked due to failed attempts."); return null; }

            String passwordHash = userDao.getPasswordHash(connection, user.getId());
            if (!PasswordUtil.matches(password, passwordHash)) {
                userDao.incFailedAttempts(connection, user.getId());

                int failedAttempts = userDao.getFailedAttempts(connection, user.getId());
                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    userDao.lock(connection, user.getId());
                    System.out.println("Too many attempts. Account locked.");
                } else {
                    System.out.println("Invalid password. Attempts left: " + (MAX_FAILED_ATTEMPTS - failedAttempts));
                }

                connection.commit();
                return null;
            }

            userDao.resetFailedAttempts(connection, user.getId());
            connection.commit();
            System.out.println("Login successful. Welcome, " + user.getName() + "!");
            return user;

        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Login failed: " + ex.getMessage());
            return null;
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public boolean changePassword(long userId, String currentPassword, String newPassword) {
        Connection connection = null;
        try {
            connection = connectionProvider.get();

            String passwordHash = userDao.getPasswordHash(connection, userId);
            if (!PasswordUtil.matches(currentPassword, passwordHash)) {
                System.out.println("Current password is incorrect.");
                return false;
            }

            userDao.updatePassword(connection, userId, PasswordUtil.hash(newPassword));
            connection.commit();
            System.out.println("Password changed successfully.");
            return true;

        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Change password failed: " + ex.getMessage());
            return false;
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }

    public boolean resetPasswordWithSecurity(String login, int question1Id, String answer1,
                                            int question2Id, String answer2, String newPassword) {

        Connection connection = null;
        try {
            connection = connectionProvider.get();

            User user = userDao.findByLogin(connection, login);
            if (user == null) { System.out.println("User not found."); return false; }

            String answerHash1 = securityDao.getAnswerHash(connection, user.getId(), question1Id);
            String answerHash2 = securityDao.getAnswerHash(connection, user.getId(), question2Id);
            if (answerHash1 == null || answerHash2 == null) { System.out.println("Security answers not set."); return false; }

            if (!PasswordUtil.matches(answer1, answerHash1) || !PasswordUtil.matches(answer2, answerHash2)) {
                System.out.println("Security answers mismatch.");
                return false;
            }

            userDao.updatePassword(connection, user.getId(), PasswordUtil.hash(newPassword));
            connection.commit();
            System.out.println("Password reset successful. Please login.");
            return true;

        } catch (Exception ex) {
            Jdbc.rollbackQuietly(connection);
            System.err.println("Reset password failed: " + ex.getMessage());
            return false;
        } finally {
            Jdbc.closeQuietly(connection);
        }
    }
}
