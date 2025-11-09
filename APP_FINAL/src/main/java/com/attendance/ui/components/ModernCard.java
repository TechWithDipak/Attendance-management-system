package com.attendance.ui.components;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ModernCard extends JPanel {
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255, 240);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 40);

    public ModernCard() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();
        int arc = 16;

        // Shadow
        g2d.setColor(SHADOW_COLOR);
        g2d.fillRoundRect(4, 4, width, height, arc, arc);

        // Card background
        g2d.setColor(CARD_BACKGROUND);
        g2d.fillRoundRect(0, 0, width, height, arc, arc);

        // Subtle border
        g2d.setColor(new Color(240, 240, 240));
        g2d.setStroke(new java.awt.BasicStroke(1.0f));
        g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        g2d.dispose();
        super.paintComponent(g);
    }
}

