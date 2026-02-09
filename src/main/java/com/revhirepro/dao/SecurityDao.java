package com.revhirepro.dao;

import java.sql.*;

public class SecurityDao {

    public ResultSet listQuestions(Connection connection) throws SQLException {
        String sql = "SELECT question_id, question_text FROM rh_security_questions ORDER BY question_id";
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    public void saveAnswer(Connection connection, long userId, int questionId, String answerHash) throws SQLException {
        String sql = "INSERT INTO rh_user_security_answers(user_id, question_id, answer_hash) VALUES(?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, questionId);
            ps.setString(3, answerHash);
            ps.executeUpdate();
        }
    }

    public String getAnswerHash(Connection connection, long userId, int questionId) throws SQLException {
        String sql = "SELECT answer_hash FROM rh_user_security_answers WHERE user_id=? AND question_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1);
            }
        }
    }
}
