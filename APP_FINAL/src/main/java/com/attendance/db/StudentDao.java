package com.attendance.db;

import com.attendance.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    public Student findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT id, name, roll_no, username, password, latitude, longitude FROM students WHERE username=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("roll_no"),
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

        public int insertStudent(String name, String rollNo, String username, String password, Double latitude, Double longitude) throws SQLException {
            String sql = "INSERT INTO students (name, roll_no, username, password, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, rollNo);
                ps.setString(3, username);
                ps.setString(4, password);
                if (latitude == null) {
                    ps.setNull(5, Types.DOUBLE);
                } else {
                    ps.setDouble(5, latitude);
                }
                if (longitude == null) {
                    ps.setNull(6, Types.DOUBLE);
                } else {
                    ps.setDouble(6, longitude);
                }
                int affected = ps.executeUpdate();
                if (affected == 0) return -1;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
            return -1;
        }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, name, roll_no, username, password, latitude, longitude FROM students ORDER BY name";
        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("roll_no"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                ));
            }
        }
        return list;
    }
}


