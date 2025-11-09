package com.attendance.ui.components;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernTextField extends JTextField {
    private boolean isFocused = false;
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Color FOCUS_BORDER_COLOR = new Color(67, 97, 238);
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);

    public ModernTextField(int columns) {
        super(columns);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(new Color(33, 33, 33));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        setOpaque(false);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = 8;

        // Background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, width, height, arc, arc);

        // Border
        g2d.setColor(isFocused ? FOCUS_BORDER_COLOR : BORDER_COLOR);
        g2d.setStroke(new java.awt.BasicStroke(isFocused ? 2.0f : 1.0f));
        g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        g2d.dispose();
        super.paintComponent(g);
    }
}

