package com.attendance.ui.components;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

public class GlassPanel extends JPanel {
    private float opacity = 0.75f;

    public GlassPanel() {
        setOpaque(false);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 24;

        // Base translucent blue with subtle vertical gradient for depth
        Color baseTop = new Color(0x0D, 0x1F, 0x78, (int) (opacity * 255)); // #0d1f78 with alpha
        Color baseBottom = new Color(0x0D, 0x1F, 0x78, (int) (opacity * 255 * 0.9));
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setPaint(new GradientPaint(0, 0, baseTop, 0, h, baseBottom));
        g2d.fillRoundRect(0, 0, w, h, arc, arc);

        // Soft inner highlight at top (simulates light/blur sheen)
        Graphics2D gHighlight = (Graphics2D) g2d.create();
        gHighlight.setComposite(AlphaComposite.SrcOver.derive(0.12f));
        gHighlight.setColor(Color.WHITE);
        gHighlight.fillRoundRect(2, 2, w - 4, Math.max(8, h / 3), arc - 6, arc - 6);
        gHighlight.dispose();

        // Subtle outer border and inner border for glass edge
        Graphics2D gBorder = (Graphics2D) g2d.create();
        gBorder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBorder.setStroke(new BasicStroke(1.2f));
        gBorder.setColor(new Color(255, 255, 255, 60));
        gBorder.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        gBorder.setColor(new Color(13, 31, 120, 90));
        gBorder.drawRoundRect(1, 1, w - 3, h - 3, arc - 4, arc - 4);
        gBorder.dispose();

        g2d.dispose();
        super.paintComponent(g);
    }
}


