package com.freelancer.system.ui;

import com.freelancer.system.model.Milestone;
import com.freelancer.system.model.Project;
import com.freelancer.system.model.ProjectType;
import com.freelancer.system.service.ProjectService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDialog extends JDialog {
    private final ProjectService projectService;
    private JTextField nameField;
    private JTextField clientField;
    private JTextField deadlineField;
    private JTextField rateField;
    private JTextField fixedPaymentField;
    private JComboBox<String> statusBox;
    private JRadioButton hourlyBtn;
    private JRadioButton fixedBtn;
    private JPanel milestonesContainer;
    private JScrollPane milestonesScroll;
    private JButton addMilestoneBtn;

    public ProjectDialog(JFrame owner, ProjectService projectService) {
        super(owner, "Create New Project", true);
        this.projectService = projectService;

        setSize(700, 850);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 24));
        mainPanel.setBackground(ThemeManager.BG_PRIMARY);
        mainPanel.setBorder(new EmptyBorder(32, 40, 32, 40));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("🚀 Project Blueprint");
        titleLabel.setFont(ThemeManager.getFont(Font.BOLD, 26));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Configure the details for your new freelance engagement");
        subtitleLabel.setFont(ThemeManager.getFont(Font.PLAIN, 14));
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

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT, 1),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Project Type Toggle
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        typePanel.setOpaque(false);
        hourlyBtn = new JRadioButton("Hourly Rate");
        fixedBtn = new JRadioButton("Fixed Price");
        hourlyBtn.setFont(ThemeManager.getFont(Font.BOLD, 14));
        fixedBtn.setFont(ThemeManager.getFont(Font.BOLD, 14));
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(hourlyBtn);
        typeGroup.add(fixedBtn);
        fixedBtn.setSelected(true);
        typePanel.add(fixedBtn);
        typePanel.add(hourlyBtn);

        addFormLabel(panel, "Billing Methodology", gbc);
        panel.add(typePanel, gbc);
        gbc.gridy++;

        // Basic Fields
        nameField = ThemeManager.createTextField("e.g. Mobile App Redesign");
        clientField = ThemeManager.createTextField("e.g. Acme Corp");
        deadlineField = ThemeManager.createTextField("YYYY-MM-DD");
        deadlineField.setText(LocalDate.now().plusMonths(1).toString());

        addFormLabel(panel, "Project Name", gbc);
        panel.add(nameField, gbc);
        gbc.gridy++;

        addFormLabel(panel, "Client Name", gbc);
        panel.add(clientField, gbc);
        gbc.gridy++;

        addFormLabel(panel, "Agreement Deadline", gbc);
        panel.add(deadlineField, gbc);
        gbc.gridy++;

        // Status
        String[] statuses = { "Pending", "In Progress", "Completed" };
        statusBox = new JComboBox<>(statuses);
        statusBox.setFont(ThemeManager.getFont(Font.PLAIN, 14));
        statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        addFormLabel(panel, "Initial Status", gbc);
        panel.add(statusBox, gbc);
        gbc.gridy++;

        // Hourly Rate (Conditional)
        rateField = ThemeManager.createTextField("0.00");
        addFormLabel(panel, "Hourly Rate ($)", gbc);
        panel.add(rateField, gbc);
        gbc.gridy++;

        // Fixed Price Milestones (Conditional)
        fixedPaymentField = ThemeManager.createTextField("0.00");
        fixedPaymentField.setEditable(false);
        fixedPaymentField.setBackground(ThemeManager.BG_SECONDARY);
        addFormLabel(panel, "Consolidated Total ($)", gbc);
        panel.add(fixedPaymentField, gbc);
        gbc.gridy++;

        milestonesContainer = new JPanel();
        milestonesContainer.setLayout(new BoxLayout(milestonesContainer, BoxLayout.Y_AXIS));
        milestonesContainer.setBackground(ThemeManager.BG_CARD);

        milestonesScroll = new JScrollPane(milestonesContainer);
        milestonesScroll.setPreferredSize(new Dimension(0, 200));
        milestonesScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_LIGHT), "Payment Milestones"));

        addMilestoneBtn = new JButton("➕ Add New Milestone");
        addMilestoneBtn.setFont(ThemeManager.getFont(Font.BOLD, 12));
        addMilestoneBtn.setForeground(ThemeManager.INFO);
        addMilestoneBtn.setContentAreaFilled(false);
        addMilestoneBtn.setBorderPainted(false);
        addMilestoneBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMilestoneBtn.addActionListener(e -> {
            addMilestoneRow();
            revalidate();
            repaint();
        });

        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(milestonesScroll, gbc);
        gbc.gridy++;

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addMilestoneBtn, gbc);

        // Visibility Toggle Logic
        Runnable updateVisibility = () -> {
            boolean isHourly = hourlyBtn.isSelected();
            rateField.setVisible(isHourly);
            milestonesScroll.setVisible(!isHourly);
            addMilestoneBtn.setVisible(!isHourly);
            fixedPaymentField.setVisible(!isHourly);

            // Re-find the labels to toggle them too
            // This is a bit tricky with GridBagLayout, but for now we'll just hide the
            // fields
            // which works well enough for the user experience.

            revalidate();
            repaint();
        };

        hourlyBtn.addActionListener(e -> updateVisibility.run());
        fixedBtn.addActionListener(e -> updateVisibility.run());
        updateVisibility.run();

        return panel;
    }

    private void addFormLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeManager.getFont(Font.BOLD, 12));
        label.setForeground(ThemeManager.TEXT_SECONDARY);
        panel.add(label, gbc);
        gbc.gridy++;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JButton saveButton = ThemeManager.createGradientButton("Create Project", ThemeManager.PRIMARY_START,
                ThemeManager.PRIMARY_END);
        saveButton.setPreferredSize(new Dimension(250, 50));
        saveButton.addActionListener(e -> saveProject());

        panel.add(saveButton, BorderLayout.EAST);
        return panel;
    }

    private void addMilestoneRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JTextField desc = ThemeManager.createTextField("Milestone Description");
        desc.setPreferredSize(new Dimension(300, 35));

        JTextField amt = ThemeManager.createTextField("0.00");
        amt.setPreferredSize(new Dimension(100, 35));

        JButton remove = new JButton("✕");
        remove.setFont(new Font("SansSerif", Font.PLAIN, 18));
        remove.setForeground(ThemeManager.DANGER);
        remove.setContentAreaFilled(false);
        remove.setBorderPainted(false);
        remove.setCursor(new Cursor(Cursor.HAND_CURSOR));

        amt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateTotal();
            }
        });

        remove.addActionListener(e -> {
            milestonesContainer.remove(row);
            milestonesContainer.revalidate();
            milestonesContainer.repaint();
            updateTotal();
        });

        row.add(desc);
        row.add(amt);
        row.add(remove);
        milestonesContainer.add(row);
        milestonesContainer.revalidate();
    }

    private void updateTotal() {
        double total = 0;
        for (Component c : milestonesContainer.getComponents()) {
            if (c instanceof JPanel) {
                try {
                    JPanel row = (JPanel) c;
                    JTextField amtRow = (JTextField) row.getComponent(1);
                    total += Double.parseDouble(amtRow.getText());
                } catch (Exception ignored) {
                }
            }
        }
        fixedPaymentField.setText(String.format("%.2f", total));
    }

    private void saveProject() {
        try {
            String name = nameField.getText();
            String client = clientField.getText();
            LocalDate deadline = LocalDate.parse(deadlineField.getText());
            String status = (String) statusBox.getSelectedItem();
            ProjectType type = hourlyBtn.isSelected() ? ProjectType.HOURLY : ProjectType.FIXED;

            if (name.isEmpty() || client.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠ Project and Client names are required.");
                return;
            }

            double rate = 0;
            double payment = 0;
            List<Milestone> milestones = new ArrayList<>();

            if (type == ProjectType.HOURLY) {
                rate = Double.parseDouble(rateField.getText());
            } else {
                for (Component c : milestonesContainer.getComponents()) {
                    if (c instanceof JPanel) {
                        JPanel row = (JPanel) c;
                        String mDesc = ((JTextField) row.getComponent(0)).getText();
                        double mAmt = Double.parseDouble(((JTextField) row.getComponent(1)).getText());
                        milestones.add(new Milestone(mDesc, mAmt));
                        payment += mAmt;
                    }
                }
            }

            Project project = new Project(name, client, deadline, payment, rate, status, type);
            project.setMilestones(milestones);

            projectService.addProject(project);
            dispose();
            JOptionPane.showMessageDialog(getParent(), "🎉 Project created successfully!");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "⚠ Invalid date format. Use YYYY-MM-DD.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "⚠ Invalid numeric value entered.");
        }
    }
}
