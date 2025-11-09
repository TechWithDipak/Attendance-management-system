package com.attendance.db;

import com.attendance.model.AttendanceRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String sql = "SELECT a.id, a.student_id, a.teacher_id, s.name AS student_name, s.roll_no AS roll_no, a.course, a.date, a.status, a.location FROM attendance a JOIN students s ON s.id = a.student_id WHERE a.teacher_id=? ORDER BY a.date DESC";
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
                            rs.getString("student_name"),
                            rs.getString("roll_no"),
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

    public List<AttendanceRecord> findByTeacherWithFilters(int teacherId, String course, LocalDate date, boolean presentOnly) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT a.id, a.student_id, a.teacher_id, s.name AS student_name, s.roll_no AS roll_no, a.course, a.date, a.status, a.location FROM attendance a JOIN students s ON s.id = a.student_id WHERE a.teacher_id=?");
        List<Object> params = new ArrayList<>();
        params.add(teacherId);
        if (course != null && !course.isBlank()) {
            sql.append(" AND a.course=?");
            params.add(course);
        }
        if (date != null) {
            sql.append(" AND a.date=?");
            params.add(Date.valueOf(date));
        }
        if (presentOnly) {
            sql.append(" AND a.status='Present'");
        }
        sql.append(" ORDER BY a.date DESC");

        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof java.sql.Date) {
                    ps.setDate(i + 1, (java.sql.Date) p);
                } else if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else {
                    throw new SQLException("Unsupported parameter type: " + p);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord ar = new AttendanceRecord(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("teacher_id"),
                            rs.getString("student_name"),
                            rs.getString("roll_no"),
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
        String sql = "SELECT a.id, a.student_id, a.teacher_id, s.name AS student_name, s.roll_no AS roll_no, a.course, a.date, a.status, a.location FROM attendance a JOIN students s ON s.id = a.student_id WHERE a.student_id=? ORDER BY a.date DESC";
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
                            rs.getString("student_name"),
                            rs.getString("roll_no"),
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

    public List<AttendanceRecord> findByStudentWithFilters(int studentId, String course, LocalDate date) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT a.id, a.student_id, a.teacher_id, s.name AS student_name, s.roll_no AS roll_no, a.course, a.date, a.status, a.location FROM attendance a JOIN students s ON s.id = a.student_id WHERE a.student_id=?");
        List<Object> params = new ArrayList<>();
        params.add(studentId);
        if (course != null && !course.isBlank()) {
            sql.append(" AND a.course=?");
            params.add(course);
        }
        if (date != null) {
            sql.append(" AND a.date=?");
            params.add(Date.valueOf(date));
        }
        sql.append(" ORDER BY a.date DESC");

        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof java.sql.Date) {
                    ps.setDate(i + 1, (java.sql.Date) p);
                } else if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else {
                    throw new SQLException("Unsupported parameter type: " + p);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord ar = new AttendanceRecord(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("teacher_id"),
                            rs.getString("student_name"),
                            rs.getString("roll_no"),
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

    public double calculateAttendancePercentageForStudent(int studentId) throws SQLException {
        String totalSql = "SELECT COUNT(*) AS cnt FROM attendance WHERE student_id=?";
        String presentSql = "SELECT COUNT(*) AS cnt FROM attendance WHERE student_id=? AND status='Present'";
        int total = 0;
        int present = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psTotal = conn.prepareStatement(totalSql);
             PreparedStatement psPresent = conn.prepareStatement(presentSql)) {
            psTotal.setInt(1, studentId);
            try (ResultSet rs = psTotal.executeQuery()) { if (rs.next()) total = rs.getInt("cnt"); }
            psPresent.setInt(1, studentId);
            try (ResultSet rs = psPresent.executeQuery()) { if (rs.next()) present = rs.getInt("cnt"); }
        }
        if (total == 0) return 0.0;
        return (present * 100.0) / total;
    }

    public Map<Integer, String> getPresentStatusesForSession(int teacherId, String course, LocalDate date) throws SQLException {
        String sql = "SELECT student_id, location FROM attendance WHERE teacher_id=? AND course=? AND date=? AND status='Present'";
        Map<Integer, String> map = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setString(2, course);
            ps.setDate(3, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("student_id"), rs.getString("location"));
                }
            }
        }
        return map;
    }
}


