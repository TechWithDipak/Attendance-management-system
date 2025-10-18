package com.attendance.ui;

import com.attendance.db.AttendanceDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Student;
import com.attendance.util.LocationValidator;
import com.attendance.util.QRGenerator;
import com.attendance.util.QRScanner;
import com.attendance.ui.components.GlassPanel;
import com.google.zxing.NotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private final Student student;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID","Student","Teacher","Course","Date","Status","Location"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public StudentDashboardFrame(Student student) {
        this.student = student;
        setTitle("Student Dashboard - " + student.getName());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        GlassPanel root = new GlassPanel();
        root.setLayout(new BorderLayout());

        JLabel header = new JLabel("Welcome, " + student.getName(), SwingConstants.CENTER);
        header.setForeground(Color.WHITE);
        root.add(header, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        table.setSelectionBackground(new Color(255, 255, 255, 100)); // Semi-transparent selection
        table.setSelectionForeground(Color.BLACK);
        
        // Set header colors for better visibility
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(13, 31, 120, 200));
        
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        GlassPanel actions = new GlassPanel();
        actions.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);

        JButton scanFromImage = new JButton("Scan QR from Image");
        JButton refresh = new JButton("View Attendance");
        JButton logout = new JButton("Logout");

        Color btnBg = new Color(13, 31, 120, 180);
        Color btnFg = Color.WHITE;
        scanFromImage.setForeground(btnFg);
        scanFromImage.setBackground(btnBg);
        scanFromImage.setContentAreaFilled(true);
        scanFromImage.setOpaque(true);
        scanFromImage.setBorderPainted(false);
        scanFromImage.setFocusPainted(false);

        refresh.setForeground(btnFg);
        refresh.setBackground(btnBg);
        refresh.setContentAreaFilled(true);
        refresh.setOpaque(true);
        refresh.setBorderPainted(false);
        refresh.setFocusPainted(false);

        logout.setForeground(btnFg);
        logout.setBackground(btnBg);
        logout.setContentAreaFilled(true);
        logout.setOpaque(true);
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);

        gbc.gridx=0; gbc.gridy=0; actions.add(scanFromImage, gbc);
        gbc.gridx=1; gbc.gridy=0; actions.add(refresh, gbc);
        gbc.gridx=2; gbc.gridy=0; actions.add(logout, gbc);

        scanFromImage.addActionListener(e -> handleScan());
        refresh.addActionListener(e -> loadAttendance());
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
        loadAttendance();
    }

    private void handleScan() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String text = QRScanner.decodeFromImage(file);
                QRGenerator.SessionPayload payload = QRScanner.parsePayload(text);
                // Simulated student current location from stored profile
                double currLat = student.getLatitude();
                double currLon = student.getLongitude();
                boolean within = LocationValidator.isWithinMeters(currLat, currLon, payload.teacherLatitude, payload.teacherLongitude, 15);
                if (!within) {
                    JOptionPane.showMessageDialog(this, "You are not within 15 meters of class location.", "Location", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                new AttendanceDao().markAttendance(student.getId(), payload.teacherId, payload.course, "Present", currLat + "," + currLon);
                JOptionPane.showMessageDialog(this, "✅ Attendance Marked Successfully");
                loadAttendance();
            } catch (NotFoundException nf) {
                JOptionPane.showMessageDialog(this, "No QR code found in the image.", "Not Found", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAttendance() {
        try {
            List<AttendanceRecord> list = new AttendanceDao().findByStudent(student.getId());
            tableModel.setRowCount(0);
            for (AttendanceRecord r : list) {
                tableModel.addRow(new Object[]{ r.getId(), r.getStudentId(), r.getTeacherId(), r.getCourse(), r.getDate(), r.getStatus(), r.getLocation() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


