package com.attendance.ui;

import com.attendance.db.StudentDao;
import com.attendance.db.TeacherDao;
import com.attendance.model.Student;
import com.attendance.model.Teacher;
import com.attendance.ui.components.GlassPanel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);

    public LoginFrame() {
        setTitle("Attendance System - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(480, 360);
        setLocationRelativeTo(null);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        GlassPanel content = new GlassPanel();
        content.setLayout(new BorderLayout());

        JLabel title = new JLabel("Attendance Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        content.add(title, BorderLayout.NORTH);

        GlassPanel form = new GlassPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; form.add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(usernameField, gbc);
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; form.add(passLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(passwordField, gbc);

        JButton teacherBtn = new JButton("Login as Teacher");
        JButton studentBtn = new JButton("Login as Student");

        Color btnBg = new Color(13, 31, 120, 180);
        Color btnFg = Color.WHITE;
        teacherBtn.setForeground(btnFg);
        teacherBtn.setBackground(btnBg);
        teacherBtn.setContentAreaFilled(true);
        teacherBtn.setOpaque(true);
        teacherBtn.setBorderPainted(false);
        teacherBtn.setFocusPainted(false);

        studentBtn.setForeground(btnFg);
        studentBtn.setBackground(btnBg);
        studentBtn.setContentAreaFilled(true);
        studentBtn.setOpaque(true);
        studentBtn.setBorderPainted(false);
        studentBtn.setFocusPainted(false);

        teacherBtn.addActionListener(e -> loginTeacher());
        studentBtn.addActionListener(e -> loginStudent());

        gbc.gridx = 0; gbc.gridy = 2; form.add(teacherBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 2; form.add(studentBtn, gbc);

        content.add(form, BorderLayout.CENTER);
        setContentPane(content);
    }

    private void loginTeacher() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
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


