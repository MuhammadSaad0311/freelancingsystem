package com.freelancer.system.model;

import java.time.LocalDate;

public class Project {
    private int id; // ID (Auto-increment in DB, but kept here)
    private String name;
    private String client;
    private LocalDate deadline;
    private double payment; // Represents total payment or initial budget
    private double hourlyRate;
    private String status;
    private ProjectType projectType;
    private java.util.List<Milestone> milestones;

    public Project(int id, String name, String client, LocalDate deadline, double payment, double hourlyRate,
            String status, ProjectType projectType) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.deadline = deadline;
        this.payment = payment;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.projectType = projectType;
        this.milestones = new java.util.ArrayList<>();
    }

    public Project(String name, String client, LocalDate deadline, double payment, double hourlyRate, String status,
            ProjectType projectType) {
        this(-1, name, client, deadline, payment, hourlyRate, status, projectType); // ID will be assigned by DB
    }

    // Keep old constructor for backward compatibility if needed, defaulting to
    // FIXED or determining by rate
    public Project(int id, String name, String client, LocalDate deadline, double payment, double hourlyRate,
            String status) {
        this(id, name, client, deadline, payment, hourlyRate, status,
                hourlyRate > 0 ? ProjectType.HOURLY : ProjectType.FIXED);
    }

    public Project(String name, String client, LocalDate deadline, double payment, double hourlyRate, String status) {
        this(-1, name, client, deadline, payment, hourlyRate, status);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public java.util.List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(java.util.List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public void addMilestone(Milestone milestone) {
        this.milestones.add(milestone);
    }

    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "', client='" + client + "', type=" + projectType + "}";
    }
}
