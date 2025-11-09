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
    private float opacity = 0.85f;
    private boolean useModernGradient = true;

    public GlassPanel() {
        setOpaque(false);
    }

    public GlassPanel(boolean modern) {
        this();
        this.useModernGradient = modern;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }

    public void setModernGradient(boolean modern) {
        this.useModernGradient = modern;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        int arc = 20;

        if (useModernGradient) {
            // Modern gradient: Deep blue to purple-blue
            Color startColor = new Color(67, 97, 238, (int) (opacity * 255)); // Modern blue
            Color midColor = new Color(103, 126, 255, (int) (opacity * 255));
            Color endColor = new Color(156, 136, 255, (int) (opacity * 255)); // Purple-blue
            
            // Multi-stop gradient effect
            GradientPaint gradient1 = new GradientPaint(0, 0, startColor, 0, h / 2, midColor);
            GradientPaint gradient2 = new GradientPaint(0, h / 2, midColor, 0, h, endColor);
            
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.setPaint(gradient1);
            g2d.fillRoundRect(0, 0, w, h / 2, arc, arc);
            g2d.setPaint(gradient2);
            g2d.fillRoundRect(0, h / 2, w, h / 2, arc, arc);
        } else {
            // Original gradient for backward compatibility
            Color baseTop = new Color(0x0D, 0x1F, 0x78, (int) (opacity * 255));
            Color baseBottom = new Color(0x0D, 0x1F, 0x78, (int) (opacity * 255 * 0.9));
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.setPaint(new GradientPaint(0, 0, baseTop, 0, h, baseBottom));
            g2d.fillRoundRect(0, 0, w, h, arc, arc);
        }

        // Modern glass effect: subtle inner highlight
        Graphics2D gHighlight = (Graphics2D) g2d.create();
        gHighlight.setComposite(AlphaComposite.SrcOver.derive(0.15f));
        gHighlight.setColor(Color.WHITE);
        gHighlight.fillRoundRect(2, 2, w - 4, Math.max(12, h / 4), arc - 4, arc - 4);
        gHighlight.dispose();

        // Modern border with subtle glow
        Graphics2D gBorder = (Graphics2D) g2d.create();
        gBorder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBorder.setStroke(new BasicStroke(1.5f));
        
        // Outer glow
        gBorder.setColor(new Color(255, 255, 255, 80));
        gBorder.drawRoundRect(1, 1, w - 3, h - 3, arc - 2, arc - 2);
        
        // Inner border
        gBorder.setColor(new Color(255, 255, 255, 40));
        gBorder.setStroke(new BasicStroke(1.0f));
        gBorder.drawRoundRect(2, 2, w - 5, h - 5, arc - 4, arc - 4);
        gBorder.dispose();

        g2d.dispose();
        super.paintComponent(g);
    }
}
