package com.freelancer.system.model;

import java.time.LocalDate;

public class EscrowTransaction {
    private int id;
    private int projectId;
    private double amount;
    private LocalDate transactionDate;
    private TransactionStatus status;
    private String description;

    public enum TransactionStatus {
        ESCROW,
        WITHDRAWN
    }

    public EscrowTransaction(int id, int projectId, double amount, LocalDate transactionDate, TransactionStatus status,
            String description) {
        this.id = id;
        this.projectId = projectId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.status = status;
        this.description = description;
    }

    public EscrowTransaction(int projectId, double amount, LocalDate transactionDate, TransactionStatus status,
            String description) {
        this(-1, projectId, amount, transactionDate, status, description);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
