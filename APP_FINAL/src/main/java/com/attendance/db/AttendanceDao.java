package com.attendance.db;

import com.attendance.model.AttendanceRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {
    public void markAttendance(int studentId, int teacherId, String course, String status, String location) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, teacher_id, course, date, status, location) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, teacherId);
            ps.setString(3, course);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.setString(5, status);
            ps.setString(6, location);
            ps.executeUpdate();
        }
    }

    public List<AttendanceRecord> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT id, student_id, teacher_id, course, date, status, location FROM attendance WHERE teacher_id=? ORDER BY date DESC";
        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord ar = new AttendanceRecord(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("teacher_id"),
                            rs.getString("course"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("status"),
                            rs.getString("location")
                    );
                    list.add(ar);
                }
            }
        }
        return list;
    }

    public List<AttendanceRecord> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT id, student_id, teacher_id, course, date, status, location FROM attendance WHERE student_id=? ORDER BY date DESC";
        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord ar = new AttendanceRecord(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("teacher_id"),
                            rs.getString("course"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("status"),
                            rs.getString("location")
                    );
                    list.add(ar);
                }
            }
        }
        return list;
    }
}


