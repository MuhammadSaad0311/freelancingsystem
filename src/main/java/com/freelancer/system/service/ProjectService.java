package com.freelancer.system.service;

import com.freelancer.system.db.DatabaseManager;
import com.freelancer.system.model.Project;
import com.freelancer.system.model.User;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class ProjectService {
    // Thread-safe list for in-memory state
    private final List<Project> projects;

    // Executor for background tasks (Database ops, PDF generation)
    // Executor for background tasks (Database ops, PDF generation)
    private final ExecutorService executorService;

    // Scheduled Executor for the Background Monitor
    private final ScheduledExecutorService monitorExecutor;

    // Active Timer Sessions: ProjectID -> StartTime
    private final ConcurrentHashMap<Integer, LocalDateTime> activeTimers;

    // Callback for UI updates
    private Runnable onDataChanged;

    public ProjectService() {
        this.projects = Collections.synchronizedList(DatabaseManager.loadProjects());
        this.activeTimers = new ConcurrentHashMap<>();

        this.executorService = Executors.newFixedThreadPool(4); // Fixed pool as requested
        this.monitorExecutor = Executors.newSingleThreadScheduledExecutor();

        startBackgroundMonitor();
    }

    public void startTimer(int projectId) {
        if (!activeTimers.containsKey(projectId)) {
            activeTimers.put(projectId, LocalDateTime.now());
            notifyUI();
        }
    }

    public void stopTimer(int projectId) {
        LocalDateTime startTime = activeTimers.remove(projectId);
        if (startTime != null) {
            LocalDateTime endTime = LocalDateTime.now();
            Project p = findProjectById(projectId);
            if (p != null) {
                double durationHours = java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;
                double cost = 0.0;

                if (p.getProjectType() == com.freelancer.system.model.ProjectType.HOURLY) {
                    cost = durationHours * p.getHourlyRate();
                    // Update in-memory state so UI updates immediately
                    p.setPayment(p.getPayment() + cost);
                }

                // Save to DB asynchronously
                double finalCost = cost;
                int userId = com.freelancer.system.service.AuthService.getCurrentUser().getId();
                executorService.submit(() -> {
                    DatabaseManager.addWorkLog(projectId, startTime.toString(), endTime.toString(), durationHours,
                            finalCost, userId);
                });
            }
            notifyUI();
        }
    }

    public boolean isTimerRunning(int projectId) {
        return activeTimers.containsKey(projectId);
    }

    public String getTimerDuration(int projectId) {
        LocalDateTime startTime = activeTimers.get(projectId);
        if (startTime == null)
            return "00:00:00";

        long seconds = java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    private Project findProjectById(int id) {
        synchronized (projects) {
            for (Project p : projects) {
                if (p.getId() == id)
                    return p;
            }
        }
        return null;
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public List<Project> getProjects() {
        User currentUser = com.freelancer.system.service.AuthService.getCurrentUser();
        List<Project> allProjects = DatabaseManager.loadProjects();

        if (currentUser == null || currentUser.getRole() == com.freelancer.system.model.UserRole.ADMIN) {
            return allProjects;
        }

        // Filter for Employee
        List<Integer> assignedProjectIds = DatabaseManager.getProjectIdsForUser(currentUser.getId());
        List<Project> myProjects = new ArrayList<>();
        for (Project p : allProjects) {
            if (assignedProjectIds.contains(p.getId())) {
                myProjects.add(p);
            }
        }
        return myProjects;
    }

    public void addProject(Project project) {
        executorService.submit(() -> {
            int id = DatabaseManager.addProject(project);
            if (id != -1) {
                project.setId(id);
                projects.add(project);
                notifyUI();
            }
        });
    }

    public void startBackgroundMonitor() {
        monitorExecutor.scheduleAtFixedRate(() -> {
            // Check deadlines and update active timer durations for UI
            boolean timerActive = !activeTimers.isEmpty();

            // Force a UI refresh to update timer counters if active
            if (timerActive) {
                SwingUtilities.invokeLater(() -> {
                    if (onDataChanged != null) {
                        onDataChanged.run();
                    }
                });
            }

        }, 0, 1, TimeUnit.SECONDS); // Update every second for timer tick
    }

    private void notifyUI() {
        if (onDataChanged != null) {
            SwingUtilities.invokeLater(onDataChanged);
        }
    }

    public void shutdown() {
        executorService.shutdown();
        monitorExecutor.shutdown();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
