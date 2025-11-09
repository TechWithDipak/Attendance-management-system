package com.attendance.ui;

import com.attendance.db.StudentDao;
import com.attendance.db.TeacherDao;
import com.attendance.model.Student;
import com.attendance.model.Teacher;
import com.attendance.ui.components.ModernButton;
import com.attendance.ui.components.ModernTextField;
import com.attendance.ui.components.ModernPasswordField;
import com.attendance.ui.components.ModernCard;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class LoginFrame extends JFrame {
    private final ModernTextField usernameField = new ModernTextField(20);
    private final ModernPasswordField passwordField = new ModernPasswordField(20);

    public LoginFrame() {
        setTitle("Attendance System - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(520, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) {}

        // Modern background panel with gradient
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int width = getWidth();
                int height = getHeight();
                
                // Modern gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(67, 97, 238),
                    0, height, new Color(156, 136, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
                
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Modern card container
        ModernCard card = new ModernCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);

        // Title with modern typography
        JLabel title = new JLabel("Attendance Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(40, 30, 30, 30);
        card.add(title, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 30, 30);
        card.add(subtitle, gbc);

        // Username field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 30, 8, 30);
        card.add(userLabel, gbc);
        
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 30, 20, 30);
        card.add(usernameField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLabel.setForeground(new Color(60, 60, 60));
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 30, 8, 30);
        card.add(passLabel, gbc);
        
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 30, 30, 30);
        card.add(passwordField, gbc);

        // Buttons
        ModernButton teacherBtn = new ModernButton("Login as Teacher");
        teacherBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        teacherBtn.setPreferredSize(new java.awt.Dimension(200, 48));
        
        ModernButton studentBtn = new ModernButton("Login as Student");
        studentBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentBtn.setPreferredSize(new java.awt.Dimension(200, 48));
        studentBtn.setPrimaryColor(new Color(76, 175, 80)); // Green variant

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 30, 30, 10);
        card.add(teacherBtn, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 10, 30, 30);
        card.add(studentBtn, gbc);

        teacherBtn.addActionListener(e -> loginTeacher());
        studentBtn.addActionListener(e -> loginStudent());

        // Center the card
        backgroundPanel.add(card, BorderLayout.CENTER);
        setContentPane(backgroundPanel);
    }

    private void loginTeacher() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Teacher teacher = new TeacherDao().findByUsernameAndPassword(username, password);
            if (teacher != null) {
                TeacherDashboardFrame frame = new TeacherDashboardFrame(teacher);
                frame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid teacher credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loginStudent() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Student student = new StudentDao().findByUsernameAndPassword(username, password);
            if (student != null) {
                StudentDashboardFrame frame = new StudentDashboardFrame(student);
                frame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid student credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
