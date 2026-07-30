package com.freelancer.system.ui;

import com.freelancer.system.db.DatabaseManager;
import com.freelancer.system.model.EscrowTransaction;
import com.freelancer.system.model.Project;
import com.freelancer.system.service.ProjectService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class EscrowDialog extends JDialog {
    private final ProjectService projectService;
    private JLabel totalEscrowLabel;
    private JLabel totalPaidLabel;
    private JLabel totalLeftLabel;
    private DefaultTableModel tableModel;
    private JTable table;

    public EscrowDialog(JFrame owner, ProjectService projectService) {
        super(owner, "Escrow Management", true);
        this.projectService = projectService;

        setSize(1000, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ThemeManager.BG_PRIMARY);
        mainPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Header
        JPanel headerPanel = createHeaderPanel();

        // Stats Panel
        JPanel statsPanel = createStatsPanel();

        // Table Panel
        JPanel tablePanel = createTablePanel();

        // Control Panel
        JPanel controlPanel = createControlPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("💰 Escrow Payment Dashboard");
        titleLabel.setFont(ThemeManager.getFont(Font.BOLD, 24));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Track client payments and manage fund withdrawals");
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

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 140));

        totalEscrowLabel = new JLabel("$0.00");
        totalPaidLabel = new JLabel("$0.00");
        totalLeftLabel = new JLabel("$0.00");

        panel.add(ThemeManager.createStatCard("💵 Total In Escrow", totalEscrowLabel,
                ThemeManager.INFO, new Color(37, 99, 235)));
        panel.add(ThemeManager.createStatCard("✅ Total Client Paid", totalPaidLabel,
                ThemeManager.SUCCESS, new Color(22, 163, 74)));
        panel.add(ThemeManager.createStatCard("⏳ Left to Pay", totalLeftLabel,
                ThemeManager.WARNING, new Color(217, 119, 6)));

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ThemeManager.BG_CARD);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(0, 0, 0, 0)));

        // Table Header
        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.setBackground(ThemeManager.BG_CARD);
        headerSection.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel tableTitle = new JLabel("Transaction History");
        tableTitle.setFont(ThemeManager.getFont(Font.BOLD, 16));
        tableTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        headerSection.add(tableTitle, BorderLayout.WEST);

        // Table
        String[] columns = { "ID", "Date", "Project", "Description", "Amount", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(ThemeManager.TEXT_PRIMARY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(ThemeManager.getFont(Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeManager.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 44));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.BORDER_LIGHT));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.BG_CARD);
        scrollPane.setPreferredSize(new Dimension(0, 250));

        container.add(headerSection, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.BORDER_LIGHT),
                new EmptyBorder(16, 32, 16, 32)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        JButton addPaymentBtn = ThemeManager.createButton("➕ Record Payment",
                ThemeManager.BG_CARD, ThemeManager.TEXT_PRIMARY, false);
        addPaymentBtn.addActionListener(e -> showAddPaymentDialog());

        JButton withdrawBtn = ThemeManager.createButton("💸 Withdraw Selected",
                ThemeManager.WARNING, Color.WHITE, true);
        withdrawBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String status = (String) tableModel.getValueAt(selectedRow, 5);
                if ("ESCROW".equals(status)) {
                    DatabaseManager.updateEscrowTransactionStatus(id,
                            EscrowTransaction.TransactionStatus.WITHDRAWN);
                    refreshData();
                    JOptionPane.showMessageDialog(this, "✅ Amount withdrawn successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "⚠ This amount is not in escrow.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Please select a transaction to withdraw.");
            }
        });

        JButton withdrawAllBtn = ThemeManager.createGradientButton("💰 Withdraw All Remaining",
                ThemeManager.PRIMARY_START, ThemeManager.PRIMARY_END);
        withdrawAllBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to withdraw ALL funds currently in escrow?",
                    "Confirm Withdrawal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseManager.withdrawAllEscrow();
                refreshData();
                JOptionPane.showMessageDialog(this, "✅ All escrow funds withdrawn successfully!");
            }
        });

        buttonPanel.add(addPaymentBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(withdrawAllBtn);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        List<EscrowTransaction> transactions = DatabaseManager.getEscrowTransactions();

        for (EscrowTransaction t : transactions) {
            // Get project name
            String projectName = "Project #" + t.getProjectId();
            for (Project p : projectService.getProjects()) {
                if (p.getId() == t.getProjectId()) {
                    projectName = p.getName();
                    break;
                }
            }

            tableModel.addRow(new Object[] {
                    t.getId(),
                    t.getTransactionDate(),
                    projectName,
                    t.getDescription(),
                    String.format("$%.2f", t.getAmount()),
                    t.getStatus().name()
            });
        }

        double inEscrow = DatabaseManager.getTotalEscrowBalance();
        double totalPaid = DatabaseManager.getTotalClientPaid();
        double totalProjectValue = DatabaseManager.getTotalProjectValue();
        double leftToPay = totalProjectValue - totalPaid;

        totalEscrowLabel.setText(String.format("$%.2f", inEscrow));
        totalPaidLabel.setText(String.format("$%.2f", totalPaid));
        totalLeftLabel.setText(String.format("$%.2f", Math.max(0, leftToPay)));
    }

    private void showAddPaymentDialog() {
        JDialog dialog = new JDialog(this, "Record Client Payment", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(ThemeManager.BG_PRIMARY);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(ThemeManager.BG_CARD);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(30, 30, 30, 30)));

        // Project selection
        JLabel projectLabel = new JLabel("Select Project");
        projectLabel.setFont(ThemeManager.getFont(Font.BOLD, 12));
        projectLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        projectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<Project> projectBox = new JComboBox<>();
        projectBox.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        projectBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        projectBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Project p : projectService.getProjects()) {
            projectBox.addItem(p);
        }

        // Amount field
        JLabel amountLabel = new JLabel("Payment Amount");
        amountLabel.setFont(ThemeManager.getFont(Font.BOLD, 12));
        amountLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField amountField = ThemeManager.createTextField("Enter amount (e.g., 1500.00)");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description field
        JLabel descLabel = new JLabel("Description (Optional)");
        descLabel.setFont(ThemeManager.getFont(Font.BOLD, 12));
        descLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField descField = ThemeManager.createTextField("e.g., Milestone 1 Payment");
        descField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        descField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Save button
        JButton saveBtn = ThemeManager.createGradientButton("💾 Record Payment",
                ThemeManager.PRIMARY_START, ThemeManager.PRIMARY_END);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveBtn.addActionListener(e -> {
            try {
                Project p = (Project) projectBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                if (p != null && amount > 0) {
                    EscrowTransaction t = new EscrowTransaction(
                            p.getId(),
                            amount,
                            LocalDate.now(),
                            EscrowTransaction.TransactionStatus.ESCROW,
                            descField.getText().isEmpty() ? "Client Payment" : descField.getText());
                    DatabaseManager.addEscrowTransaction(t);
                    refreshData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "✅ Payment recorded successfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "⚠ Please enter a valid amount.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "⚠ Invalid amount format. Please enter a number.");
            }
        });

        // Add components
        formPanel.add(projectLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(projectBox);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(amountField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(descLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(descField);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(saveBtn);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ThemeManager.BG_PRIMARY);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.add(formPanel, BorderLayout.CENTER);

        dialog.add(container, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}
