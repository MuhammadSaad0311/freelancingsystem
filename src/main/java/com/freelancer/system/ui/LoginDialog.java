package com.freelancer.system.ui;

import com.freelancer.system.model.User;
import com.freelancer.system.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginDialog extends JDialog {
    private boolean authenticated = false;

    public LoginDialog(JFrame parent) {
        super(parent, "Sign In - Freelancer Workspace", true);
        setSize(480, 600);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.BG_PRIMARY);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header with gradient
        JPanel headerPanel = createHeaderPanel();

        // Form panel
        JPanel formPanel = createFormPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Handle close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Icon/Logo area with gradient circle
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient circle
                int size = 80;
                int x = (getWidth() - size) / 2;
                int y = 10;
                GradientPaint gradient = new GradientPaint(
                        x, y, ThemeManager.PRIMARY_START,
                        x + size, y + size, ThemeManager.PRIMARY_END);
                g2d.setPaint(gradient);
                g2d.fillOval(x, y, size, size);

                // Icon symbol
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
                String icon = "💼";
                FontMetrics fm = g2d.getFontMetrics();
                int iconX = (getWidth() - fm.stringWidth(icon)) / 2;
                int iconY = y + (size + fm.getAscent()) / 2 - 5;
                g2d.drawString(icon, iconX, iconY);
            }
        };
        iconPanel.setPreferredSize(new Dimension(400, 100));
        iconPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(ThemeManager.getFont(Font.BOLD, 28));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to manage your freelance projects");
        subtitleLabel.setFont(ThemeManager.getFont(Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(30, 30, 30, 30)));

        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(ThemeManager.getFont(Font.BOLD, 12));
        emailLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField emailField = ThemeManager.createTextField("Enter your email");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(ThemeManager.getFont(Font.BOLD, 12));
        passwordLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passwordField = ThemeManager.createPasswordField("Enter your password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Error label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeManager.getFont(Font.PLAIN, 12));
        errorLabel.setForeground(ThemeManager.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button with gradient
        JButton loginButton = ThemeManager.createGradientButton("Sign In",
                ThemeManager.PRIMARY_START, ThemeManager.PRIMARY_END);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠ Please fill in all fields");
                return;
            }

            User user = AuthService.login(email, password);
            if (user != null) {
                authenticated = true;
                dispose();
            } else {
                errorLabel.setText("⚠ Invalid email or password");
                passwordField.setText("");
            }
        });

        // Demo credentials hint
        JPanel demoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        demoPanel.setOpaque(false);
        demoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel demoLabel = new JLabel("<html><center>Demo Admin Account<br/>" +
                "<b>admin@company.com</b> / <b>admin123</b></center></html>");
        demoLabel.setFont(ThemeManager.getFont(Font.PLAIN, 11));
        demoLabel.setForeground(ThemeManager.TEXT_MUTED);
        demoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        demoLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        demoLabel.setOpaque(true);
        demoLabel.setBackground(ThemeManager.BG_SECONDARY);

        demoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                emailField.setText("admin@company.com");
                passwordField.setText("admin123");
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                demoLabel.setBackground(ThemeManager.BG_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                demoLabel.setBackground(ThemeManager.BG_SECONDARY);
            }
        });

        demoPanel.add(demoLabel);

        // Add components with spacing
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(demoPanel);

        return panel;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
