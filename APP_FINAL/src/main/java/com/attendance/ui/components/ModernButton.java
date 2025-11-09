package com.attendance.ui.components;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private Color primaryColor = new Color(67, 97, 238); // Material Design primary
    private Color hoverColor = new Color(89, 115, 245);
    private Color pressedColor = new Color(55, 80, 200);
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setPrimaryColor(Color color) {
        this.primaryColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();
        int arc = 12;

        Color bgColor = isPressed ? pressedColor : (isHovered ? hoverColor : primaryColor);
        
        // Gradient background
        g2d.setColor(bgColor);
        g2d.fillRoundRect(0, 0, width, height, arc, arc);

        // Subtle shadow
        if (!isPressed) {
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillRoundRect(0, 2, width, height, arc, arc);
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, width, height - 2, arc, arc);
        }

        // Inner highlight
        g2d.setColor(new Color(255, 255, 255, 20));
        g2d.fillRoundRect(1, 1, width - 2, height / 2, arc - 2, arc - 2);

        g2d.dispose();
        super.paintComponent(g);
    }
}

