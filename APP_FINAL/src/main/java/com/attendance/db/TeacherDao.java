package com.attendance.db;

import com.attendance.model.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherDao {
    public Teacher findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT id, name, username, password, latitude, longitude FROM teachers WHERE username=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude")
                    );
                }
            }
        }
        return null;
    }
}


