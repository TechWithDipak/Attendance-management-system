package com.attendance.ui;

import com.attendance.db.AttendanceDao;
import com.attendance.db.StudentDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Teacher;
import com.attendance.util.ExcelExporter;
import com.attendance.util.QRGenerator;
import com.attendance.ui.components.GlassPanel;
import com.attendance.ui.components.ModernButton;
import com.attendance.ui.components.ModernTextField;
import com.attendance.ui.components.ModernCard;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import javax.imageio.ImageIO;

public class TeacherDashboardFrame extends JFrame {
    private final Teacher teacher;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Roll No","Student","Course","Date","Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private DefaultTableModel studentsModel;
    private JLabel qrCountdownLabel;
    private JLabel qrImageLabel;
    private ModernTextField studentsCourseField;
    private ModernTextField studentsSearchField;
    private Timer countdownTimer;
    private Timer pollTimer;
    private Timer toastTimer;
    private long sessionExpiryMs = 0L;
    private String activeCourse = null;
    private Path currentQRPath = null;
    private JPanel toastPanel;

    public TeacherDashboardFrame(Teacher teacher) {
        this.teacher = teacher;
        setTitle("Teacher Dashboard - " + teacher.getName());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        // Modern background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(67, 97, 238), 0, getHeight(), new Color(156, 136, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Modern header
        GlassPanel headerPanel = new GlassPanel(true);
        headerPanel.setLayout(new BorderLayout());
        JLabel header = new JLabel("Welcome, " + teacher.getName(), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        headerPanel.add(header, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Attendance tab content
        ModernCard tableCard = new ModernCard();
        tableCard.setLayout(new BorderLayout());

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(new Color(33, 33, 33));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(67, 97, 238));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowGrid(true);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));

        // Modern header styling
        table.getTableHeader().setForeground(new Color(60, 60, 60));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));

        // Enhanced zebra striping with modern colors
        javax.swing.table.DefaultTableCellRenderer cellRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (!isSelected) {
                    c.setBackground((row % 2 == 0) ? Color.WHITE : new Color(248, 248, 248));
                } else {
                    c.setBackground(new Color(67, 97, 238));
                    c.setForeground(Color.WHITE);
                }
                setBorder(new javax.swing.border.EmptyBorder(10, 15, 10, 15));
                return c;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setOpaque(false);
        tableCard.add(scroll, BorderLayout.CENTER);
        
        // Students tab content
        ModernCard studentsCard = new ModernCard();
        studentsCard.setLayout(new BorderLayout());

        // Top bar for students tab
        JPanel studentsTop = new JPanel(new GridBagLayout());
        studentsTop.setOpaque(false);
        GridBagConstraints gs = new GridBagConstraints();
        gs.insets = new Insets(12, 15, 12, 15);
        gs.anchor = GridBagConstraints.WEST;
        
        JLabel studentsTitle = new JLabel("Students");
        studentsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        studentsTitle.setForeground(new Color(33,33,33));
        
        studentsCourseField = new ModernTextField(16);
        studentsSearchField = new ModernTextField(16);
        ModernButton applySearch = new ModernButton("Search");
        applySearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        ModernButton genQRStudents = new ModernButton("Generate QR");
        genQRStudents.setFont(new Font("Segoe UI", Font.BOLD, 14));
        genQRStudents.setPreferredSize(new java.awt.Dimension(140, 40));

        gs.gridx=0; gs.gridy=0; studentsTop.add(studentsTitle, gs);
        gs.gridx=1; gs.gridy=0; 
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectLabel.setForeground(new Color(60, 60, 60));
        studentsTop.add(subjectLabel, gs);
        gs.gridx=2; gs.gridy=0; studentsTop.add(studentsCourseField, gs);
        gs.gridx=3; gs.gridy=0; 
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchLabel.setForeground(new Color(60, 60, 60));
        studentsTop.add(searchLabel, gs);
        gs.gridx=4; gs.gridy=0; studentsTop.add(studentsSearchField, gs);
        gs.gridx=5; gs.gridy=0; studentsTop.add(applySearch, gs);
        gs.gridx=6; gs.gridy=0; gs.anchor = GridBagConstraints.EAST; gs.fill = GridBagConstraints.NONE;
        studentsTop.add(genQRStudents, gs);

        studentsCard.add(studentsTop, BorderLayout.NORTH);

        // Main content area - split between table and QR panel
        JPanel studentsContent = new JPanel(new BorderLayout());
        studentsContent.setOpaque(false);

        // Students table on left
        studentsModel = new DefaultTableModel(new Object[]{"Name","Roll No","Status","Timestamp"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable studentsTable = new JTable(studentsModel);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentsTable.setRowHeight(36);
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentsTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 40));
        
        // Status column renderer with colors
        studentsTable.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("Present".equals(value)) {
                    label.setForeground(new Color(76, 175, 80));
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                } else {
                    label.setForeground(new Color(244, 67, 54));
                    label.setFont(label.getFont().deriveFont(Font.PLAIN));
                }
                return label;
            }
        });
        
        JScrollPane studentsScroll = new JScrollPane(studentsTable);
        studentsScroll.setBorder(new javax.swing.border.EmptyBorder(15, 15, 15, 15));
        studentsScroll.getViewport().setBackground(Color.WHITE);
        studentsContent.add(studentsScroll, BorderLayout.CENTER);

        // QR panel on right - centered and prominent
        ModernCard qrCard = new ModernCard();
        qrCard.setLayout(new BorderLayout());
        qrCard.setPreferredSize(new java.awt.Dimension(320, 0));
        
        JPanel qrContainer = new JPanel();
        qrContainer.setLayout(new BorderLayout());
        qrContainer.setOpaque(false);
        qrContainer.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        
        qrImageLabel = new JLabel("QR Code will appear here", SwingConstants.CENTER);
        qrImageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qrImageLabel.setForeground(new Color(120, 120, 120));
        qrImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        qrImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrImageLabel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        
        qrContainer.add(qrImageLabel, BorderLayout.CENTER);
        
        // Countdown timer label
        qrCountdownLabel = new JLabel("", SwingConstants.CENTER);
        qrCountdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        qrCountdownLabel.setForeground(new Color(67, 97, 238));
        qrCountdownLabel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        
        // Download QR button
        ModernButton downloadQRBtn = new ModernButton("Download QR");
        downloadQRBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        downloadQRBtn.setPreferredSize(new java.awt.Dimension(0, 42));
        downloadQRBtn.setPrimaryColor(new Color(76, 175, 80)); // Green
        downloadQRBtn.setEnabled(false);
        
        downloadQRBtn.addActionListener(e -> {
            if (currentQRPath != null && Files.exists(currentQRPath)) {
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new java.io.File("attendance_qr_" + activeCourse + ".png"));
                int res = chooser.showSaveDialog(this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    try {
                        Files.copy(currentQRPath, chooser.getSelectedFile().toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        showToast("QR code saved successfully!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Failed to save QR: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        JPanel qrControls = new JPanel(new BorderLayout());
        qrControls.setOpaque(false);
        qrControls.setBorder(new javax.swing.border.EmptyBorder(10, 20, 10, 20));
        qrControls.add(qrCountdownLabel, BorderLayout.NORTH);
        qrControls.add(downloadQRBtn, BorderLayout.CENTER);
        
        qrContainer.add(qrControls, BorderLayout.SOUTH);
        qrCard.add(qrContainer, BorderLayout.CENTER);
        
        studentsContent.add(qrCard, BorderLayout.EAST);
        studentsCard.add(studentsContent, BorderLayout.CENTER);

        // Toast notification panel
        toastPanel = new JPanel();
        toastPanel.setOpaque(false);
        toastPanel.setLayout(new BorderLayout());
        toastPanel.setVisible(false);

        // Tabbed container
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("Attendance", tableCard);
        tabs.addTab("Students", studentsCard);

        backgroundPanel.add(tabs, BorderLayout.CENTER);
        backgroundPanel.add(toastPanel, BorderLayout.PAGE_START);

        // Modern action panel (only for Attendance tab)
        ModernCard actionCard = new ModernCard();
        actionCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);

        ModernTextField filterCourse = new ModernTextField(15);
        ModernTextField filterDate = new ModernTextField(12);

        ModernButton refresh = new ModernButton("View Attendance");
        ModernButton export = new ModernButton("Export Excel");
        ModernButton toggleAddStudent = new ModernButton("Add Student");
        ModernButton logout = new ModernButton("Logout");
        logout.setPrimaryColor(new Color(244, 67, 54)); // Red for logout

        JCheckBox presentOnly = new JCheckBox("Present only");
        presentOnly.setOpaque(false);
        presentOnly.setForeground(new Color(60, 60, 60));
        presentOnly.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Labels
        JLabel filterCourseLbl = new JLabel("Filter Course:");
        filterCourseLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCourseLbl.setForeground(new Color(60, 60, 60));
        
        JLabel filterDateLbl = new JLabel("Date:");
        filterDateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterDateLbl.setForeground(new Color(60, 60, 60));

        // Row 0: Actions
        gbc.gridx = 0; gbc.gridy = 0; actionCard.add(export, gbc);
        gbc.gridx = 1; gbc.gridy = 0; actionCard.add(toggleAddStudent, gbc);
        gbc.gridx = 2; gbc.gridy = 0; actionCard.add(logout, gbc);

        // Row 1: Filters
        gbc.gridx = 0; gbc.gridy = 1; actionCard.add(filterCourseLbl, gbc);
        gbc.gridx = 1; gbc.gridy = 1; actionCard.add(filterCourse, gbc);
        gbc.gridx = 2; gbc.gridy = 1; actionCard.add(filterDateLbl, gbc);
        gbc.gridx = 3; gbc.gridy = 1; actionCard.add(filterDate, gbc);
        gbc.gridx = 4; gbc.gridy = 1; actionCard.add(presentOnly, gbc);
        gbc.gridx = 5; gbc.gridy = 1; actionCard.add(refresh, gbc);

        // Add Student panel
        ModernCard addStudentCard = new ModernCard();
        addStudentCard.setLayout(new GridBagLayout());
        GridBagConstraints gbcAdd = new GridBagConstraints();
        gbcAdd.insets = new Insets(10, 15, 10, 15);
        gbcAdd.fill = GridBagConstraints.HORIZONTAL;

        JLabel addStudentLbl = new JLabel("Add New Student");
        addStudentLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addStudentLbl.setForeground(new Color(33, 33, 33));
        
        ModernTextField nameField = new ModernTextField(15);
        ModernTextField rollField = new ModernTextField(12);
        ModernTextField usernameField = new ModernTextField(15);
        ModernTextField passwordField = new ModernTextField(15);
        ModernButton addStudentBtn = new ModernButton("Create Student");
        addStudentBtn.setPrimaryColor(new Color(76, 175, 80)); // Green

        JLabel nameLbl = new JLabel("Name:"); nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); nameLbl.setForeground(new Color(60, 60, 60));
        JLabel rollLbl = new JLabel("Roll No:"); rollLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); rollLbl.setForeground(new Color(60, 60, 60));
        JLabel userLbl = new JLabel("Username:"); userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); userLbl.setForeground(new Color(60, 60, 60));
        JLabel passLbl = new JLabel("Password:"); passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); passLbl.setForeground(new Color(60, 60, 60));

        gbcAdd.gridx = 0; gbcAdd.gridy = 0; gbcAdd.gridwidth = 4; addStudentCard.add(addStudentLbl, gbcAdd);
        gbcAdd.gridwidth = 1;
        gbcAdd.gridx = 0; gbcAdd.gridy = 1; addStudentCard.add(nameLbl, gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 1; addStudentCard.add(nameField, gbcAdd);
        gbcAdd.gridx = 2; gbcAdd.gridy = 1; addStudentCard.add(rollLbl, gbcAdd);
        gbcAdd.gridx = 3; gbcAdd.gridy = 1; addStudentCard.add(rollField, gbcAdd);
        gbcAdd.gridx = 0; gbcAdd.gridy = 2; addStudentCard.add(userLbl, gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 2; addStudentCard.add(usernameField, gbcAdd);
        gbcAdd.gridx = 2; gbcAdd.gridy = 2; addStudentCard.add(passLbl, gbcAdd);
        gbcAdd.gridx = 3; gbcAdd.gridy = 2; addStudentCard.add(passwordField, gbcAdd);
        gbcAdd.gridx = 3; gbcAdd.gridy = 3; addStudentCard.add(addStudentBtn, gbcAdd);
        addStudentCard.setVisible(false);

        // Place Add Student panel inside the action area (below filters)
        GridBagConstraints gbcAddHost = new GridBagConstraints();
        gbcAddHost.gridx = 0;
        gbcAddHost.gridy = 2;
        gbcAddHost.gridwidth = 6;
        gbcAddHost.fill = GridBagConstraints.HORIZONTAL;
        gbcAddHost.insets = new Insets(0, 0, 0, 0);
        actionCard.add(addStudentCard, gbcAddHost);

        // Load students list initially
        loadStudentsTable(null);

        // Search handling
        applySearch.addActionListener(e -> loadStudentsTable(studentsSearchField.getText().trim()));

        // Generate QR in Students tab
        genQRStudents.addActionListener(e -> {
            String course = studentsCourseField.getText().trim();
            if (course.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter subject/course", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                QRGenerator.SessionPayload payload = QRGenerator.createPayload(teacher.getId(), course, teacher.getLatitude(), teacher.getLongitude());
                Path path = Path.of("output/qrcodes/" + payload.sessionId + ".png");
                QRGenerator.generateQrPng(payload, 400, path);
                currentQRPath = path;
                
                // Show QR in UI
                BufferedImage img = ImageIO.read(path.toFile());
                ImageIcon icon = new ImageIcon(img.getScaledInstance(280, 280, java.awt.Image.SCALE_SMOOTH));
                qrImageLabel.setText("");
                qrImageLabel.setIcon(icon);

                // Enable download button
                downloadQRBtn.setEnabled(true);

                // Reset all students to Absent
                loadStudentsTable(studentsSearchField.getText().trim());

                // Start 10-min countdown
                activeCourse = course;
                sessionExpiryMs = payload.timestamp + 10 * 60 * 1000L;
                startCountdownAndPolling();
                
                showToast("QR code generated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refresh.addActionListener(e -> loadAttendance(filterCourse.getText().trim(), filterDate.getText().trim(), presentOnly.isSelected()));
        export.addActionListener(e -> doExport());
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        addStudentBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String roll = rollField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (name.isEmpty() || roll.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = new StudentDao().insertStudent(name, roll, username, password, null, null);
                if (id > 0) {
                    JOptionPane.showMessageDialog(this, "Student added successfully!");
                    nameField.setText("");
                    rollField.setText("");
                    usernameField.setText("");
                    passwordField.setText("");
                    addStudentCard.setVisible(false);
                    actionCard.revalidate();
                    actionCard.repaint();
                    loadStudentsTable(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add student", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        toggleAddStudent.addActionListener(e -> {
            addStudentCard.setVisible(!addStudentCard.isVisible());
            actionCard.revalidate();
            actionCard.repaint();
        });

        backgroundPanel.add(actionCard, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
        loadAttendance(null, null, false);
    }

    private void showToast(String message) {
        toastPanel.removeAll();
        ModernCard toastCard = new ModernCard();
        toastCard.setLayout(new BorderLayout());
        JLabel toastLabel = new JLabel(message, SwingConstants.CENTER);
        toastLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toastLabel.setForeground(new Color(33, 33, 33));
        toastLabel.setBorder(new javax.swing.border.EmptyBorder(12, 20, 12, 20));
        toastCard.add(toastLabel, BorderLayout.CENTER);
        toastPanel.add(toastCard, BorderLayout.CENTER);
        toastPanel.setVisible(true);
        toastPanel.revalidate();
        toastPanel.repaint();
        
        if (toastTimer != null) toastTimer.stop();
        toastTimer = new Timer(3000, e -> {
            toastPanel.setVisible(false);
            toastPanel.revalidate();
            toastPanel.repaint();
        });
        toastTimer.setRepeats(false);
        toastTimer.start();
    }

    private void loadAttendance(String courseFilter, String dateStr, boolean presentOnly) {
        try {
            AttendanceDao dao = new AttendanceDao();
            java.time.LocalDate date = null;
            if (dateStr != null && !dateStr.isBlank()) {
                try { date = java.time.LocalDate.parse(dateStr); } catch (Exception ignored) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Validation", JOptionPane.WARNING_MESSAGE);
                }
            }
            List<AttendanceRecord> list = (courseFilter != null && !courseFilter.isBlank()) || date != null || presentOnly
                    ? dao.findByTeacherWithFilters(teacher.getId(), courseFilter, date, presentOnly)
                    : dao.findByTeacher(teacher.getId());
            tableModel.setRowCount(0);
            for (AttendanceRecord r : list) {
                tableModel.addRow(new Object[]{ r.getStudentRollNo(), r.getStudentName(), r.getCourse(), r.getDate(), r.getStatus() });
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

    private void loadStudentsTable(String query) {
        try {
            List<com.attendance.model.Student> students = new StudentDao().findAll();
            studentsModel.setRowCount(0);
            String q = (query == null) ? "" : query.toLowerCase();
            for (com.attendance.model.Student s : students) {
                if (!q.isEmpty()) {
                    String key = (s.getName() + " " + s.getRollNo()).toLowerCase();
                    if (!key.contains(q)) continue;
                }
                studentsModel.addRow(new Object[]{ s.getName(), s.getRollNo(), "Absent", "-" });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startCountdownAndPolling() {
        // Countdown timer label updater
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            long remaining = sessionExpiryMs - System.currentTimeMillis();
            if (remaining <= 0) {
                qrCountdownLabel.setText("QR expired");
                qrCountdownLabel.setForeground(new Color(244, 67, 54));
                ((Timer)e.getSource()).stop();
                if (pollTimer != null) pollTimer.stop();
            } else {
                long mins = remaining / 60000;
                long secs = (remaining % 60000) / 1000;
                qrCountdownLabel.setText(String.format("QR expires in: %02d:%02d", mins, secs));
                qrCountdownLabel.setForeground(new Color(67, 97, 238));
            }
        });
        countdownTimer.start();

        // Poll attendance to update statuses
        if (pollTimer != null) pollTimer.stop();
        pollTimer = new Timer(2000, e -> refreshStudentStatuses());
        pollTimer.start();
        refreshStudentStatuses();
    }

    private void refreshStudentStatuses() {
        if (activeCourse == null || System.currentTimeMillis() > sessionExpiryMs) {
            if (pollTimer != null) pollTimer.stop();
            return;
        }
        try {
            AttendanceDao dao = new AttendanceDao();
            Map<Integer, String> presentMap = dao.getPresentStatusesForSession(teacher.getId(), activeCourse, LocalDate.now());
            // Build rollNo->studentId map requires fetching all students
            List<com.attendance.model.Student> students = new StudentDao().findAll();
            java.util.Map<String, Integer> rollToId = new java.util.HashMap<>();
            for (com.attendance.model.Student s : students) rollToId.put(s.getRollNo(), s.getId());

            for (int r = 0; r < studentsModel.getRowCount(); r++) {
                String roll = String.valueOf(studentsModel.getValueAt(r, 1));
                Integer sid = rollToId.get(roll);
                if (sid != null && presentMap.containsKey(sid)) {
                    String oldStatus = String.valueOf(studentsModel.getValueAt(r, 2));
                    studentsModel.setValueAt("Present", r, 2);
                    String ts = presentMap.get(sid);
                    studentsModel.setValueAt(ts != null ? ts : "-", r, 3);
                    // Show toast when student is marked present
                    if ("Absent".equals(oldStatus)) {
                        String studentName = String.valueOf(studentsModel.getValueAt(r, 0));
                        showToast(studentName + " marked present");
                    }
                } else {
                    // Only set to Absent if not already Present (to avoid overwriting)
                    String currentStatus = String.valueOf(studentsModel.getValueAt(r, 2));
                    if (!"Present".equals(currentStatus)) {
                        studentsModel.setValueAt("Absent", r, 2);
                        studentsModel.setValueAt("-", r, 3);
                    }
                }
            }
        } catch (Exception ex) {
            // fail silently to avoid UI spam
        }
    }
}
