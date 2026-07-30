package com.freelancer.system.ui;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Centralized theme manager for consistent UI styling across the application
 */
public class ThemeManager {
    // Primary Colors - Indigo to Purple Gradient
    public static final Color PRIMARY_START = new Color(99, 102, 241); // Indigo-500
    public static final Color PRIMARY_END = new Color(139, 92, 246); // Purple-500
    public static final Color PRIMARY_DARK = new Color(79, 70, 229); // Indigo-600

    // Accent Colors
    public static final Color SUCCESS = new Color(16, 185, 129); // Emerald-500
    public static final Color WARNING = new Color(245, 158, 11); // Amber-500
    public static final Color DANGER = new Color(239, 68, 68); // Red-500
    public static final Color INFO = new Color(59, 130, 246); // Blue-500

    // Background Colors
    public static final Color BG_PRIMARY = new Color(248, 250, 252); // Slate-50
    public static final Color BG_SECONDARY = new Color(241, 245, 249); // Slate-100
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_HOVER = new Color(241, 245, 249); // Slate-100

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42); // Slate-900
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139); // Slate-500
    public static final Color TEXT_MUTED = new Color(148, 163, 184); // Slate-400

    // Border Colors
    public static final Color BORDER_LIGHT = new Color(226, 232, 240); // Slate-200
    public static final Color BORDER_DEFAULT = new Color(203, 213, 225); // Slate-300

    // Fonts
    private static Font baseFont;

    static {
        baseFont = UIManager.getFont("Label.font");
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.PLAIN, 13);
        }
    }

    public static Font getFont(int style, float size) {
        return baseFont.deriveFont(style, size);
    }

    /**
     * Create a modern gradient button
     */
    public static JButton createGradientButton(String text, Color startColor, Color endColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, getModel().isPressed() ? startColor.darker() : startColor,
                        getWidth(), getHeight(), getModel().isPressed() ? endColor.darker() : endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };

        button.setFont(getFont(Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 44));

        return button;
    }

    /**
     * Create a modern flat button
     */
    public static JButton createButton(String text, Color bgColor, Color fgColor, boolean filled) {
        JButton btn = new JButton(text);
        btn.setFont(getFont(Font.BOLD, 13));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(!filled);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 44));

        if (filled) {
            btn.setBackground(bgColor);
            Color hoverColor = bgColor.darker();
            btn.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 10; borderWidth: 0; hoverBackground: " + toHex(hoverColor));
        } else {
            btn.setBackground(bgColor);
            btn.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 10; borderWidth: 2; borderColor: " + toHex(BORDER_DEFAULT));
        }

        return btn;
    }

    /**
     * Create a styled text field
     */
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setFont(getFont(Font.PLAIN, 14));
        field.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; borderWidth: 1; focusWidth: 2; borderColor: " + toHex(BORDER_LIGHT));
        field.setPreferredSize(new Dimension(0, 44));
        return field;
    }

    /**
     * Create a styled password field
     */
    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setFont(getFont(Font.PLAIN, 14));
        field.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; borderWidth: 1; focusWidth: 2; borderColor: " + toHex(BORDER_LIGHT));
        field.setPreferredSize(new Dimension(0, 44));
        return field;
    }

    /**
     * Create a gradient panel
     */
    public static JPanel createGradientPanel(Color startColor, Color endColor) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                        0, 0, startColor,
                        getWidth(), getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    /**
     * Create a card panel with shadow effect
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                new EmptyBorder(20, 20, 20, 20)));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        return card;
    }

    /**
     * Create a stat card with gradient
     */
    public static JPanel createStatCard(String title, JLabel valueLabel, Color gradStart, Color gradEnd) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, gradStart,
                        getWidth(), getHeight(), gradEnd);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(0, 140));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(getFont(Font.BOLD, 13));
        titleLbl.setForeground(new Color(255, 255, 255, 200));

        valueLabel.setFont(getFont(Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Convert Color to hex string
     */
    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Create a label with icon (using Unicode)
     */
    public static JLabel createIconLabel(String icon, String text, Color color) {
        JLabel label = new JLabel(icon + " " + text);
        label.setFont(getFont(Font.PLAIN, 13));
        label.setForeground(color);
        return label;
    }

    /**
     * Create a status badge
     */
    public static JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(status);
        badge.setFont(getFont(Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));

        switch (status.toLowerCase()) {
            case "completed":
                badge.setBackground(new Color(220, 252, 231)); // Green-100
                badge.setForeground(new Color(22, 163, 74)); // Green-600
                break;
            case "in progress":
                badge.setBackground(new Color(219, 234, 254)); // Blue-100
                badge.setForeground(new Color(37, 99, 235)); // Blue-600
                break;
            case "pending":
                badge.setBackground(new Color(254, 243, 199)); // Amber-100
                badge.setForeground(new Color(217, 119, 6)); // Amber-600
                break;
            default:
                badge.setBackground(BG_SECONDARY);
                badge.setForeground(TEXT_SECONDARY);
        }

        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 6");
        return badge;
    }
}
