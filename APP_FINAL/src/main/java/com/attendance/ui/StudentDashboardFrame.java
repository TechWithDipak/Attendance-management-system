package com.attendance.ui;

import com.attendance.db.AttendanceDao;
import com.attendance.db.TeacherDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Student;
import com.attendance.util.QRGenerator;
import com.attendance.util.QRScanner;
import com.attendance.ui.components.GlassPanel;
import com.attendance.ui.components.ModernButton;
import com.attendance.ui.components.ModernTextField;
import com.attendance.ui.components.ModernCard;
import com.google.zxing.NotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
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
import java.io.File;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private final Student student;
    private JLabel percentageDisplay;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Teacher","Course","Date","Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public StudentDashboardFrame(Student student) {
        this.student = student;
        setTitle("Student Dashboard - " + student.getName());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
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
        JLabel header = new JLabel("Welcome, " + student.getName(), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        headerPanel.add(header, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Modern card for table
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

        // Enhanced zebra striping
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
        
        backgroundPanel.add(tableCard, BorderLayout.CENTER);

        // Modern action panel
        ModernCard actionCard = new ModernCard();
        actionCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);

        ModernTextField filterCourse = new ModernTextField(15);
        ModernTextField filterDate = new ModernTextField(12);

        ModernButton scanFromImage = new ModernButton("Scan QR Code");
        ModernButton refresh = new ModernButton("View All");
        ModernButton applyFilter = new ModernButton("Apply Filter");
        ModernButton logout = new ModernButton("Logout");
        logout.setPrimaryColor(new Color(244, 67, 54)); // Red for logout

        // Attendance percentage widget - modern card style
        ModernCard percentageCard = new ModernCard();
        percentageCard.setLayout(new BorderLayout());
        percentageDisplay = new JLabel("Attendance: --%", SwingConstants.CENTER);
        percentageDisplay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        percentageDisplay.setForeground(new Color(33, 33, 33));
        percentageCard.add(percentageDisplay, BorderLayout.CENTER);
        percentageCard.setPreferredSize(new java.awt.Dimension(250, 50));

        // Labels
        JLabel filterCourseLbl = new JLabel("Filter Course:");
        filterCourseLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCourseLbl.setForeground(new Color(60, 60, 60));
        
        JLabel filterDateLbl = new JLabel("Date:");
        filterDateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterDateLbl.setForeground(new Color(60, 60, 60));

        // Row 0: Actions
        gbc.gridx = 0; gbc.gridy = 0; actionCard.add(scanFromImage, gbc);
        gbc.gridx = 1; gbc.gridy = 0; actionCard.add(refresh, gbc);
        gbc.gridx = 2; gbc.gridy = 0; actionCard.add(logout, gbc);

        // Row 1: Filters and percentage
        gbc.gridx = 0; gbc.gridy = 1; actionCard.add(filterCourseLbl, gbc);
        gbc.gridx = 1; gbc.gridy = 1; actionCard.add(filterCourse, gbc);
        gbc.gridx = 2; gbc.gridy = 1; actionCard.add(filterDateLbl, gbc);
        gbc.gridx = 3; gbc.gridy = 1; actionCard.add(filterDate, gbc);
        gbc.gridx = 4; gbc.gridy = 1; actionCard.add(applyFilter, gbc);
        gbc.gridx = 5; gbc.gridy = 1; actionCard.add(percentageCard, gbc);

        scanFromImage.addActionListener(e -> handleScan());
        refresh.addActionListener(e -> loadAttendance(null, null, percentageDisplay));
        applyFilter.addActionListener(e -> loadAttendance(filterCourse.getText().trim(), filterDate.getText().trim(), percentageDisplay));
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        backgroundPanel.add(actionCard, BorderLayout.SOUTH);
        setContentPane(backgroundPanel);
        loadAttendance(null, null, percentageDisplay);
    }

    private void handleScan() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String text = QRScanner.decodeFromImage(file);
                QRGenerator.SessionPayload payload = QRScanner.parsePayload(text);
                // Enforce 10-minute window from payload timestamp
                long now = System.currentTimeMillis();
                if (now - payload.timestamp > 10 * 60 * 1000) {
                    JOptionPane.showMessageDialog(this, "QR session expired. Please contact your teacher.", "Expired", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String timestamp = java.time.LocalTime.now().withNano(0).toString();
                new AttendanceDao().markAttendance(student.getId(), payload.teacherId, payload.course, "Present", timestamp);
                JOptionPane.showMessageDialog(this, "✅ Attendance Marked Successfully");
                loadAttendance(null, null, percentageDisplay);
            } catch (NotFoundException nf) {
                JOptionPane.showMessageDialog(this, "No QR code found in the image.", "Not Found", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAttendance(String courseFilter, String dateStr, JLabel percentageLabel) {
        try {
            AttendanceDao dao = new AttendanceDao();
            java.time.LocalDate date = null;
            if (dateStr != null && !dateStr.isBlank()) {
                try { date = java.time.LocalDate.parse(dateStr); } catch (Exception ignored) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Validation", JOptionPane.WARNING_MESSAGE);
                }
            }
            List<AttendanceRecord> list = (courseFilter != null && !courseFilter.isBlank()) || date != null
                    ? dao.findByStudentWithFilters(student.getId(), courseFilter, date)
                    : dao.findByStudent(student.getId());
            tableModel.setRowCount(0);
            TeacherDao teacherDao = new TeacherDao();
            for (AttendanceRecord r : list) {
                String teacherName = teacherDao.findNameById(r.getTeacherId());
                tableModel.addRow(new Object[]{ teacherName, r.getCourse(), r.getDate(), r.getStatus() });
            }
            double pct = dao.calculateAttendancePercentageForStudent(student.getId());
            String verdict = pct >= 75.0 ? "✓ Above 75%" : "✗ Below 75%";
            Color verdictColor = pct >= 75.0 ? new Color(76, 175, 80) : new Color(244, 67, 54);
            percentageLabel.setText(String.format("<html><center>Attendance: <b>%.1f%%</b><br><span style='color:rgb(%d,%d,%d)'>%s</span></center></html>", 
                pct, verdictColor.getRed(), verdictColor.getGreen(), verdictColor.getBlue(), verdict));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
