package com.freelancer.system.ui;

import com.freelancer.system.db.DatabaseManager;
import com.freelancer.system.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AssignUserDialog extends JDialog {
    private final int projectId;
    private JList<User> userList;

    public AssignUserDialog(JFrame owner, int projectId) {
        super(owner, "Assign Team Member", true);
        this.projectId = projectId;

        setSize(450, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ThemeManager.BG_PRIMARY);
        mainPanel.setBorder(new EmptyBorder(32, 32, 32, 32));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // List Panel
        JPanel listPanel = createListPanel();
        mainPanel.add(listPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        loadUsers();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("👤 Assign Member");
        titleLabel.setFont(ThemeManager.getFont(Font.BOLD, 22));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Select a team member to assign to this project");
        subtitleLabel.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createListPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ThemeManager.BG_CARD);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(10, 10, 10, 10)));

        userList = new JList<>();
        userList.setFont(ThemeManager.getFont(Font.PLAIN, 14));
        userList.setSelectionBackground(new Color(239, 246, 255));
        userList.setSelectionForeground(ThemeManager.TEXT_PRIMARY);
        userList.setFixedCellHeight(40);
        userList.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.BG_CARD);

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);

        JButton assignBtn = ThemeManager.createGradientButton("Assign Member", ThemeManager.PRIMARY_START,
                ThemeManager.PRIMARY_END);
        assignBtn.addActionListener(e -> {
            User selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                DatabaseManager.assignUserToProject(projectId, selectedUser.getId(), "MEMBER");
                JOptionPane.showMessageDialog(this, "✅ User assigned successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Please select a user to assign.");
            }
        });

        panel.add(assignBtn);
        return panel;
    }

    private void loadUsers() {
        DefaultListModel<User> listModel = new DefaultListModel<>();
        java.util.List<User> users = DatabaseManager.getAllUsers();
        for (User u : users) {
            listModel.addElement(u);
        }
        userList.setModel(listModel);
    }
}
