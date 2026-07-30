package com.freelancer.system.model;

public class Milestone {
    private int id;
    private int projectId;
    private String description;
    private double amount;

    public Milestone(int id, int projectId, String description, double amount) {
        this.id = id;
        this.projectId = projectId;
        this.description = description;
        this.amount = amount;
    }

    public Milestone(String description, double amount) {
        this(-1, -1, description, amount);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
