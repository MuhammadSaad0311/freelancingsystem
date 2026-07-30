package com.freelancer.system.ui;

import com.freelancer.system.db.DatabaseManager;
import com.freelancer.system.model.User;
import com.freelancer.system.model.UserRole;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ManageUsersDialog extends JDialog {
    private DefaultTableModel userModel;
    private JTable userTable;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passField;
    private JComboBox<UserRole> roleBox;

    public ManageUsersDialog(JFrame owner) {
        super(owner, "User Management", true);
        setSize(850, 650);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(0, 24));
        mainPanel.setBackground(ThemeManager.BG_PRIMARY);
        mainPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table Panel (Center)
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Form Panel (South)
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("👥 Team Management");
        titleLabel.setFont(ThemeManager.getFont(Font.BOLD, 24));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Add, remove, and manage your team members and their roles");
        subtitleLabel.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ThemeManager.BG_CARD);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(0, 0, 0, 0)));

        // Table Header Section
        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.setBackground(ThemeManager.BG_CARD);
        headerSection.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel tableTitle = new JLabel("Existing Users");
        tableTitle.setFont(ThemeManager.getFont(Font.BOLD, 16));
        tableTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        headerSection.add(tableTitle, BorderLayout.WEST);

        // User Table
        String[] columns = { "ID", "Name", "Email", "Role" };
        userModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userModel);
        userTable.setRowHeight(48);
        userTable.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        userTable.setShowVerticalLines(false);
        userTable.setShowHorizontalLines(true);
        userTable.setGridColor(new Color(241, 245, 249));
        userTable.setSelectionBackground(new Color(239, 246, 255));
        userTable.setSelectionForeground(ThemeManager.TEXT_PRIMARY);

        JTableHeader header = userTable.getTableHeader();
        header.setFont(ThemeManager.getFont(Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeManager.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 44));

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.BG_CARD);

        container.add(headerSection, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ThemeManager.BG_CARD);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(24, 24, 24, 24)));

        JLabel formTitle = new JLabel("Add New Member");
        formTitle.setFont(ThemeManager.getFont(Font.BOLD, 16));
        formTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JPanel inputsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        inputsPanel.setOpaque(false);

        // Name
        JPanel nameBox = createInputBox("Full Name", nameField = ThemeManager.createTextField("John Doe"));
        // Email
        JPanel emailBox = createInputBox("Email Address",
                emailField = ThemeManager.createTextField("john@example.com"));
        // Password
        JPanel passBox = createInputBox("Password", passField = ThemeManager.createPasswordField("••••••••"));
        // Role
        roleBox = new JComboBox<>(UserRole.values());
        roleBox.setFont(ThemeManager.getFont(Font.PLAIN, 14));
        JPanel roleBoxPanel = createInputBox("Account Role", roleBox);

        inputsPanel.add(nameBox);
        inputsPanel.add(emailBox);
        inputsPanel.add(passBox);
        inputsPanel.add(roleBoxPanel);

        JButton addUserBtn = ThemeManager.createGradientButton("➕ Add User", ThemeManager.PRIMARY_START,
                ThemeManager.PRIMARY_END);
        addUserBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passField.getPassword());
            UserRole role = (UserRole) roleBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            User newUser = new User(name, email, password, role);
            DatabaseManager.addUser(newUser);

            // Clear fields
            nameField.setText("");
            emailField.setText("");
            passField.setText("");

            refreshData();
            JOptionPane.showMessageDialog(this, "User added successfully!");
        });

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(formTitle, BorderLayout.WEST);
        topRow.add(addUserBtn, BorderLayout.EAST);

        container.add(topRow, BorderLayout.NORTH);
        container.add(Box.createVerticalStrut(16), BorderLayout.CENTER); // Not correct for BorderLayout center, but
                                                                         // we'll use a wrapper

        // Use a simple vertical box layout wrapper for the content
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);
        contentWrapper.add(Box.createVerticalStrut(16));
        contentWrapper.add(inputsPanel);

        container.add(contentWrapper, BorderLayout.CENTER);

        return container;
    }

    private JPanel createInputBox(String labelStr, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelStr);
        label.setFont(ThemeManager.getFont(Font.BOLD, 12));
        label.setForeground(ThemeManager.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);

        return panel;
    }

    private void refreshData() {
        userModel.setRowCount(0);
        List<User> users = DatabaseManager.getAllUsers();
        for (User u : users) {
            userModel.addRow(new Object[] { u.getId(), u.getName(), u.getEmail(), u.getRole() });
        }
    }
}
