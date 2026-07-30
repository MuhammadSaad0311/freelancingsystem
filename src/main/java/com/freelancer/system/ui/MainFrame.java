package com.freelancer.system.ui;

import com.freelancer.system.db.DatabaseManager;
import com.freelancer.system.model.Project;
import com.freelancer.system.service.AuthService;
import com.freelancer.system.service.InvoiceGenerator;
import com.freelancer.system.service.ProjectService;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {
    private final ProjectService projectService;
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private JLabel totalProjectsLabel;
    private JLabel earningsLabel;
    private JLabel nextDeadlineLabel;

    public MainFrame() {
        super("Freelancer Workspace");

        // Initialize Database FIRST
        DatabaseManager.initializeDatabase();

        // Force Login
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);
        if (!loginDialog.isAuthenticated()) {
            System.exit(0);
        }

        this.projectService = new ProjectService();

        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);

        // Modern Header with Gradient
        JPanel headerPanel = createModernHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Area
        JPanel contentPanel = new JPanel(new BorderLayout(0, 24));
        contentPanel.setBackground(ThemeManager.BG_PRIMARY);
        contentPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Enhanced Dashboard Stats
        JPanel statsPanel = createModernStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // Modern Table Panel
        JPanel tableContainer = createModernTablePanel();
        contentPanel.add(tableContainer, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Modern Control Panel
        JPanel controlPanel = createModernControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        projectService.setOnDataChanged(this::refreshTable);
        refreshTable();
        setLocationRelativeTo(null);
    }

    private JPanel createModernHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.BORDER_LIGHT),
                new EmptyBorder(20, 32, 20, 32)));

        // Left side - Title
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Freelancer Hub");
        titleLabel.setFont(ThemeManager.getFont(Font.BOLD, 26));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Manage your projects with ease");
        subtitleLabel.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);

        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);

        // Right side - Quick Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        // Using simple text for icons for now
        JButton notificationBtn = createIconButton("🔔", "Notifications");
        JButton settingsBtn = createIconButton("⚙️", "Settings");

        rightPanel.add(notificationBtn);
        rightPanel.add(settingsBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createIconButton(String icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setFont(ThemeManager.getFont(Font.PLAIN, 18));
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(44, 44));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 22; background: " + ThemeManager.toHex(ThemeManager.BG_SECONDARY));

        return btn;
    }

    private JPanel createModernStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);

        totalProjectsLabel = new JLabel("0");
        earningsLabel = new JLabel("$0.00");
        nextDeadlineLabel = new JLabel("—");

        panel.add(ThemeManager.createStatCard("Active Projects", totalProjectsLabel,
                new Color(59, 130, 246), new Color(37, 99, 235)));
        panel.add(ThemeManager.createStatCard("Total Earnings", earningsLabel,
                new Color(16, 185, 129), new Color(5, 150, 105)));
        panel.add(ThemeManager.createStatCard("Next Deadline", nextDeadlineLabel,
                new Color(245, 158, 11), new Color(217, 119, 6)));

        // Add a "Welcome" or "User" card
        JLabel userLabel = new JLabel(AuthService.isAdmin() ? "ADMIN" : "EMPLOYEE");
        userLabel.setFont(ThemeManager.getFont(Font.BOLD, 20));
        panel.add(ThemeManager.createStatCard("Access Level", userLabel,
                ThemeManager.PRIMARY_START, ThemeManager.PRIMARY_END));

        return panel;
    }

    private JPanel createModernTablePanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ThemeManager.BG_CARD);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(0, 0, 0, 0)));
        container.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        // Table Header Section
        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.setBackground(ThemeManager.BG_CARD);
        headerSection.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel tableTitle = new JLabel("Projects Overview");
        tableTitle.setFont(ThemeManager.getFont(Font.BOLD, 18));
        tableTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        JTextField searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍 Search projects...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        filterPanel.add(searchField);

        headerSection.add(tableTitle, BorderLayout.WEST);
        headerSection.add(filterPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = { "ID", "Project", "Client", "Deadline", "Payment", "Rate", "Timer", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        projectTable = new JTable(tableModel);
        projectTable.setRowHeight(56);
        projectTable.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        projectTable.setShowVerticalLines(false);
        projectTable.setShowHorizontalLines(true);
        projectTable.setGridColor(new Color(241, 245, 249));
        projectTable.setSelectionBackground(new Color(239, 246, 255));
        projectTable.setSelectionForeground(ThemeManager.TEXT_PRIMARY);

        JTableHeader header = projectTable.getTableHeader();
        header.setFont(ThemeManager.getFont(Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeManager.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 48));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.BORDER_LIGHT));

        ProjectCellRenderer renderer = new ProjectCellRenderer();
        for (int i = 0; i < projectTable.getColumnCount(); i++) {
            projectTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(projectTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.BG_CARD);

        container.add(headerSection, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createModernControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.BORDER_LIGHT),
                new EmptyBorder(16, 32, 16, 32)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        JButton startTimerBtn = ThemeManager.createButton("Start Timer", ThemeManager.SUCCESS, Color.WHITE, true);
        JButton stopTimerBtn = ThemeManager.createButton("Stop Timer", ThemeManager.DANGER, Color.WHITE, true);
        JButton invoiceButton = ThemeManager.createButton("Invoice", ThemeManager.BG_CARD, ThemeManager.TEXT_PRIMARY,
                false);
        JButton addButton = ThemeManager.createGradientButton("New Project", ThemeManager.PRIMARY_START,
                ThemeManager.PRIMARY_END);

        startTimerBtn.addActionListener(e -> {
            int selectedRow = projectTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                projectService.startTimer(id);
            }
        });

        stopTimerBtn.addActionListener(e -> {
            int selectedRow = projectTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                projectService.stopTimer(id);
            }
        });

        invoiceButton.addActionListener(e -> {
            int selectedRow = projectTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Project project = findProjectById(id);
                if (project != null)
                    generateInvoice(project);
            }
        });

        addButton.addActionListener(e -> showAddProjectDialog());

        if (AuthService.isAdmin()) {
            JButton manageUsersBtn = ThemeManager.createButton("Manage Users", ThemeManager.INFO, Color.WHITE, true);
            manageUsersBtn.addActionListener(e -> showManageUsersDialog());
            buttonPanel.add(manageUsersBtn);

            JButton assignBtn = ThemeManager.createButton("Assign User", Color.GRAY, Color.WHITE, true);
            assignBtn.addActionListener(e -> showAssignUserDialog());
            buttonPanel.add(assignBtn);

            JButton escrowBtn = ThemeManager.createButton("Escrow Funds", new Color(139, 92, 246), Color.WHITE, true);
            escrowBtn.addActionListener(e -> showEscrowDialog());
            buttonPanel.add(escrowBtn);
        }

        buttonPanel.add(startTimerBtn);
        buttonPanel.add(stopTimerBtn);
        buttonPanel.add(invoiceButton);
        buttonPanel.add(addButton);

        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    private void showManageUsersDialog() {
        new ManageUsersDialog(this).setVisible(true);
    }

    private void showAssignUserDialog() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a project to assign users.");
            return;
        }
        int projectId = (int) tableModel.getValueAt(selectedRow, 0);
        new AssignUserDialog(this, projectId).setVisible(true);
    }

    private void showAddProjectDialog() {
        new ProjectDialog(this, projectService).setVisible(true);
    }

    private void showEscrowDialog() {
        new EscrowDialog(this, projectService).setVisible(true);
    }

    private Project findProjectById(int id) {
        synchronized (projectService.getProjects()) {
            for (Project p : projectService.getProjects()) {
                if (p.getId() == id)
                    return p;
            }
        }
        return null;
    }

    private void generateInvoice(Project project) {
        projectService.getExecutorService().submit(() -> {
            String fileName = InvoiceGenerator.generateInvoice(project);
            SwingUtilities.invokeLater(() -> {
                if (fileName != null) {
                    JOptionPane.showMessageDialog(this, "Invoice generated: " + fileName);
                } else {
                    JOptionPane.showMessageDialog(this, "Error generating invoice.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private void refreshTable() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refreshTable);
            return;
        }

        int selectedRow = projectTable.getSelectedRow();
        int selectedId = -1;
        if (selectedRow != -1 && selectedRow < tableModel.getRowCount()) {
            try {
                selectedId = (int) tableModel.getValueAt(selectedRow, 0);
            } catch (Exception ignored) {
            }
        }

        tableModel.setRowCount(0);
        double totalEarnings = 0;
        LocalDate nearestDeadline = null;

        List<Project> projects = projectService.getProjects();
        synchronized (projects) {
            for (Project p : projects) {
                String timerStatus = projectService.isTimerRunning(p.getId())
                        ? "RUNNING: " + projectService.getTimerDuration(p.getId())
                        : "";

                tableModel.addRow(new Object[] {
                        p.getId(), p.getName(), p.getClient(), p.getDeadline(),
                        p.getPayment(), p.getHourlyRate(), timerStatus, p.getStatus()
                });
                totalEarnings += p.getPayment();

                if (!"Completed".equalsIgnoreCase(p.getStatus())) {
                    if (nearestDeadline == null || p.getDeadline().isBefore(nearestDeadline)) {
                        nearestDeadline = p.getDeadline();
                    }
                }
            }
            totalProjectsLabel.setText(String.valueOf(projects.size()));
            earningsLabel.setText(String.format("$%.2f", totalEarnings));

            if (nearestDeadline != null) {
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nearestDeadline);
                nextDeadlineLabel.setText(daysUntil + " days");
            } else {
                nextDeadlineLabel.setText("—");
            }
        }

        if (selectedId != -1) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == selectedId) {
                    projectTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }
}