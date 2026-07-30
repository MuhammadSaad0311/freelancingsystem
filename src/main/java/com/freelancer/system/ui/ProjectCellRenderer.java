package com.freelancer.system.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProjectCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Standard padding and font
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        setFont(ThemeManager.getFont(Font.PLAIN, 13));

        if (isSelected) {
            setBackground(new Color(239, 246, 255)); // Blue-50
            setForeground(ThemeManager.TEXT_PRIMARY);
        } else {
            setBackground(Color.WHITE);
            setForeground(ThemeManager.TEXT_PRIMARY);
        }

        try {
            // Status Column (Column 7) - Display as Badge
            if (column == 7) {
                String status = value != null ? value.toString() : "Pending";
                return ThemeManager.createStatusBadge(status);
            }

            // Timer Column (Column 6) - Highlight running timers
            if (column == 6 && value != null && value.toString().startsWith("RUNNING")) {
                setForeground(ThemeManager.DANGER);
                setFont(ThemeManager.getFont(Font.BOLD, 12));
            }

            // Payment and Rate columns - Format currency
            if (column == 4 || column == 5) {
                if (value instanceof Double) {
                    double amount = (Double) value;
                    setText(String.format("$%.2f", amount));
                    if (amount > 0) {
                        setForeground(ThemeManager.SUCCESS);
                        setFont(ThemeManager.getFont(Font.BOLD, 13));
                    }
                }
            }

            // Project Name column - Bold
            if (column == 1) {
                setFont(ThemeManager.getFont(Font.BOLD, 14));
                setForeground(ThemeManager.TEXT_PRIMARY);
            }

            // Deadline column - Formatting based on urgency
            if (column == 3) {
                Object deadlineObj = table.getValueAt(row, 3);
                LocalDate deadline = null;
                if (deadlineObj instanceof LocalDate) {
                    deadline = (LocalDate) deadlineObj;
                } else if (deadlineObj instanceof String) {
                    deadline = LocalDate.parse((String) deadlineObj);
                }

                if (deadline != null) {
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
                    String status = table.getValueAt(row, 7) != null ? table.getValueAt(row, 7).toString() : "";

                    if (!"Completed".equalsIgnoreCase(status)) {
                        if (daysLeft < 0) {
                            setForeground(ThemeManager.DANGER);
                            setFont(ThemeManager.getFont(Font.BOLD, 13));
                            setText("⚠️ Overdue (" + value + ")");
                        } else if (daysLeft <= 3) {
                            setForeground(ThemeManager.WARNING);
                            setFont(ThemeManager.getFont(Font.BOLD, 13));
                            setText("⏳ Due Soon (" + value + ")");
                        } else {
                            setForeground(ThemeManager.TEXT_SECONDARY);
                        }
                    } else {
                        setForeground(ThemeManager.TEXT_MUTED);
                    }
                }
            }

        } catch (Exception e) {
            // Fallback for parsing errors
        }

        return c;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof LocalDate) {
            setText(value.toString());
        } else if (value instanceof Double) {
            setText(String.format("%.2f", value));
        } else {
            super.setValue(value);
        }
    }
}