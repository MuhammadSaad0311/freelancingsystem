package com.freelancer.system.model;

import java.time.LocalDateTime;

public class WorkLog {
    private int id;
    private int projectId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double durationHours;
    private double cost;

    public WorkLog(int id, int projectId, LocalDateTime startTime, LocalDateTime endTime, double durationHours,
            double cost) {
        this.id = id;
        this.projectId = projectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationHours = durationHours;
        this.cost = cost;
    }

    public WorkLog(int projectId, LocalDateTime startTime, LocalDateTime endTime, double durationHours, double cost) {
        this(-1, projectId, startTime, endTime, durationHours, cost);
    }

    // Getters and Setters
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(double durationHours) {
        this.durationHours = durationHours;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
