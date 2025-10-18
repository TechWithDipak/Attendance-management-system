package com.attendance.ui;

import com.attendance.db.AttendanceDao;
import com.attendance.db.StudentDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Teacher;
import com.attendance.util.ExcelExporter;
import com.attendance.util.LocationValidator;
import com.attendance.util.QRGenerator;
import com.attendance.ui.components.GlassPanel;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.List;

public class TeacherDashboardFrame extends JFrame {
    private final Teacher teacher;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID","Student","Teacher","Course","Date","Status","Location"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public TeacherDashboardFrame(Teacher teacher) {
        this.teacher = teacher;
        setTitle("Teacher Dashboard - " + teacher.getName());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        GlassPanel root = new GlassPanel();
        root.setLayout(new BorderLayout());

        JLabel header = new JLabel("Welcome, " + teacher.getName(), SwingConstants.CENTER);
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

        JTextField courseField = new JTextField(16);
        JButton genQR = new JButton("Generate QR");
        JButton refresh = new JButton("View Attendance");
        JButton export = new JButton("Export Excel");
        JButton toggleAddStudent = new JButton("Add Student");
        JButton logout = new JButton("Logout");

        Color btnBg = new Color(13, 31, 120, 180);
        Color btnFg = Color.WHITE;
        genQR.setForeground(btnFg);
        genQR.setBackground(btnBg);
        genQR.setContentAreaFilled(true);
        genQR.setOpaque(true);
        genQR.setBorderPainted(false);
        genQR.setFocusPainted(false);

        refresh.setForeground(btnFg);
        refresh.setBackground(btnBg);
        refresh.setContentAreaFilled(true);
        refresh.setOpaque(true);
        refresh.setBorderPainted(false);
        refresh.setFocusPainted(false);

        export.setForeground(btnFg);
        export.setBackground(btnBg);
        export.setContentAreaFilled(true);
        export.setOpaque(true);
        export.setBorderPainted(false);
        export.setFocusPainted(false);

        toggleAddStudent.setForeground(btnFg);
        toggleAddStudent.setBackground(btnBg);
        toggleAddStudent.setContentAreaFilled(true);
        toggleAddStudent.setOpaque(true);
        toggleAddStudent.setBorderPainted(false);
        toggleAddStudent.setFocusPainted(false);

        logout.setForeground(btnFg);
        logout.setBackground(btnBg);
        logout.setContentAreaFilled(true);
        logout.setOpaque(true);
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);

        JLabel courseLbl = new JLabel("Course:");
        courseLbl.setForeground(Color.WHITE);
        gbc.gridx=0; gbc.gridy=0; actions.add(courseLbl, gbc);
        gbc.gridx=1; gbc.gridy=0; actions.add(courseField, gbc);
        gbc.gridx=2; gbc.gridy=0; actions.add(genQR, gbc);
        gbc.gridx=3; gbc.gridy=0; actions.add(refresh, gbc);
        gbc.gridx=4; gbc.gridy=0; actions.add(export, gbc);
        gbc.gridx=5; gbc.gridy=0; actions.add(toggleAddStudent, gbc);

        gbc.gridx=6; gbc.gridy=0; actions.add(logout, gbc);

        // Hidden Add Student panel displayed on demand
        GlassPanel addStudentPanel = new GlassPanel();
        addStudentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcAdd = new GridBagConstraints();
        gbcAdd.insets = new Insets(6,6,6,6);
        gbcAdd.fill = GridBagConstraints.HORIZONTAL;

        JLabel addStudentLbl = new JLabel("Add Student"); addStudentLbl.setForeground(Color.WHITE);
        JTextField nameField = new JTextField(12);
        JTextField rollField = new JTextField(10);
        JTextField usernameField = new JTextField(12);
        JTextField passwordField = new JTextField(12);
        JTextField latField = new JTextField(8);
        JTextField lonField = new JTextField(8);
        JButton addStudentBtn = new JButton("Create");

        addStudentBtn.setForeground(btnFg);
        addStudentBtn.setBackground(btnBg);
        addStudentBtn.setContentAreaFilled(true);
        addStudentBtn.setOpaque(true);
        addStudentBtn.setBorderPainted(false);
        addStudentBtn.setFocusPainted(false);

        JLabel nameLbl = new JLabel("Name:"); nameLbl.setForeground(Color.WHITE);
        JLabel rollLbl = new JLabel("Roll No:"); rollLbl.setForeground(Color.WHITE);
        JLabel userLbl = new JLabel("Username:"); userLbl.setForeground(Color.WHITE);
        JLabel passLbl = new JLabel("Password:"); passLbl.setForeground(Color.WHITE);
        JLabel latLbl = new JLabel("Lat (opt):"); latLbl.setForeground(Color.WHITE);
        JLabel lonLbl = new JLabel("Lon (opt):"); lonLbl.setForeground(Color.WHITE);

        gbcAdd.gridx=0; gbcAdd.gridy=0; addStudentPanel.add(addStudentLbl, gbcAdd);
        gbcAdd.gridx=0; gbcAdd.gridy=1; addStudentPanel.add(nameLbl, gbcAdd);
        gbcAdd.gridx=1; gbcAdd.gridy=1; addStudentPanel.add(nameField, gbcAdd);
        gbcAdd.gridx=2; gbcAdd.gridy=1; addStudentPanel.add(rollLbl, gbcAdd);
        gbcAdd.gridx=3; gbcAdd.gridy=1; addStudentPanel.add(rollField, gbcAdd);
        gbcAdd.gridx=0; gbcAdd.gridy=2; addStudentPanel.add(userLbl, gbcAdd);
        gbcAdd.gridx=1; gbcAdd.gridy=2; addStudentPanel.add(usernameField, gbcAdd);
        gbcAdd.gridx=2; gbcAdd.gridy=2; addStudentPanel.add(passLbl, gbcAdd);
        gbcAdd.gridx=3; gbcAdd.gridy=2; addStudentPanel.add(passwordField, gbcAdd);
        gbcAdd.gridx=0; gbcAdd.gridy=3; addStudentPanel.add(latLbl, gbcAdd);
        gbcAdd.gridx=1; gbcAdd.gridy=3; addStudentPanel.add(latField, gbcAdd);
        gbcAdd.gridx=2; gbcAdd.gridy=3; addStudentPanel.add(lonLbl, gbcAdd);
        gbcAdd.gridx=3; gbcAdd.gridy=3; addStudentPanel.add(lonField, gbcAdd);
        gbcAdd.gridx=3; gbcAdd.gridy=4; addStudentPanel.add(addStudentBtn, gbcAdd);
        addStudentPanel.setVisible(false);

        genQR.addActionListener(e -> {
            String course = courseField.getText().trim();
            if (course.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter course name", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Simulated teacher current location equals stored location; real app would read GPS
            double currentLat = teacher.getLatitude();
            double currentLon = teacher.getLongitude();
            boolean ok = LocationValidator.isWithinMeters(currentLat, currentLon, teacher.getLatitude(), teacher.getLongitude(), 15);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "You are not at the classroom location.", "Location Check", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                QRGenerator.SessionPayload payload = QRGenerator.createPayload(teacher.getId(), course, teacher.getLatitude(), teacher.getLongitude());
                Path path = Path.of("output/qrcodes/" + payload.sessionId + ".png");
                QRGenerator.generateQrPng(payload, 350, path);
                JOptionPane.showMessageDialog(this, "QR generated at: " + path.toAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refresh.addActionListener(e -> loadAttendance());
        export.addActionListener(e -> doExport());
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        addStudentBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String roll = rollField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            Double lat = null;
            Double lon = null;
            try {
                if (!latField.getText().trim().isEmpty()) lat = Double.parseDouble(latField.getText().trim());
                if (!lonField.getText().trim().isEmpty()) lon = Double.parseDouble(lonField.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Latitude/Longitude must be numeric", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.isEmpty() || roll.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill name, roll, username and password", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = new StudentDao().insertStudent(name, roll, username, password, lat, lon);
                if (id > 0) {
                    JOptionPane.showMessageDialog(this, "Student added with ID: " + id);
                    nameField.setText("");
                    rollField.setText("");
                    usernameField.setText("");
                    passwordField.setText("");
                    latField.setText("");
                    lonField.setText("");
                    addStudentPanel.setVisible(false);
                    actions.revalidate();
                    actions.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add student", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        toggleAddStudent.addActionListener(e -> {
            addStudentPanel.setVisible(!addStudentPanel.isVisible());
            actions.revalidate();
            actions.repaint();
        });

        root.add(actions, BorderLayout.SOUTH);
        root.add(addStudentPanel, BorderLayout.NORTH);

        setContentPane(root);
        loadAttendance();
    }

    private void loadAttendance() {
        try {
            List<AttendanceRecord> list = new AttendanceDao().findByTeacher(teacher.getId());
            tableModel.setRowCount(0);
            for (AttendanceRecord r : list) {
                tableModel.addRow(new Object[]{ r.getId(), r.getStudentId(), r.getTeacherId(), r.getCourse(), r.getDate(), r.getStatus(), r.getLocation() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doExport() {
        try {
            List<AttendanceRecord> list = new AttendanceDao().findByTeacher(teacher.getId());
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("attendance_export.xlsx"));
            int res = chooser.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                ExcelExporter.exportAttendance(list, chooser.getSelectedFile().toPath());
                JOptionPane.showMessageDialog(this, "Exported to " + chooser.getSelectedFile().getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


