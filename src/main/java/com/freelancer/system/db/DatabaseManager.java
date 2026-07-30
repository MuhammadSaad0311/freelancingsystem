package com.freelancer.system.db;

import com.freelancer.system.model.Project;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:freelancer.db";

    public static void initializeDatabase() {
        String createProjectsTable = "CREATE TABLE IF NOT EXISTS projects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "client TEXT NOT NULL," +
                "deadline TEXT NOT NULL," +
                "payment REAL NOT NULL," +
                "hourly_rate REAL DEFAULT 0.0," +
                "status TEXT NOT NULL," +
                "project_type TEXT DEFAULT 'FIXED'" +
                ");";

        String createWorkLogsTable = "CREATE TABLE IF NOT EXISTS work_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "project_id INTEGER NOT NULL," +
                "start_time TEXT NOT NULL," +
                "end_time TEXT NOT NULL," +
                "duration_hours REAL NOT NULL," +
                "cost REAL NOT NULL," +
                "FOREIGN KEY(project_id) REFERENCES projects(id)" +
                ");";

        String createMilestonesTable = "CREATE TABLE IF NOT EXISTS milestones (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "project_id INTEGER NOT NULL," +
                "description TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "FOREIGN KEY(project_id) REFERENCES projects(id)" +
                ");";

        String createEscrowTable = "CREATE TABLE IF NOT EXISTS escrow_transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "project_id INTEGER NOT NULL," +
                "amount REAL NOT NULL," +
                "transaction_date TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "description TEXT," +
                "FOREIGN KEY(project_id) REFERENCES projects(id)" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(createProjectsTable);
            stmt.execute(createWorkLogsTable);
            stmt.execute(createMilestonesTable);
            stmt.execute(createEscrowTable);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL" +
                    ");";
            stmt.execute(createUsersTable);

            String createAssignmentsTable = "CREATE TABLE IF NOT EXISTS project_assignments (" +
                    "project_id INTEGER NOT NULL," +
                    "user_id INTEGER NOT NULL," +
                    "role TEXT," +
                    "FOREIGN KEY(project_id) REFERENCES projects(id)," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ");";
            stmt.execute(createAssignmentsTable);

            // Migration check: Add hourly_rate if missing
            try {
                stmt.execute("ALTER TABLE projects ADD COLUMN hourly_rate REAL DEFAULT 0.0;");
            } catch (SQLException ignored) {
            }

            // Migration check: Add user_id to work_logs
            try {
                stmt.execute("ALTER TABLE work_logs ADD COLUMN user_id INTEGER DEFAULT -1;");
            } catch (SQLException ignored) {
            }

            // Ensure default admin exists
            createDefaultAdmin();

            System.out.println("Database initialized.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static int addProject(Project project) {
        String sql = "INSERT INTO projects(name, client, deadline, payment, hourly_rate, status, project_type) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getClient());
            pstmt.setString(3, project.getDeadline().toString()); // ISO-8601 format
            pstmt.setDouble(4, project.getPayment());
            pstmt.setDouble(5, project.getHourlyRate());
            pstmt.setString(6, project.getStatus());
            pstmt.setString(7, project.getProjectType().name());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int projectId = generatedKeys.getInt(1);
                        // Add Milestones
                        for (com.freelancer.system.model.Milestone m : project.getMilestones()) {
                            addMilestone(projectId, m);
                        }
                        return projectId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding project: " + e.getMessage());
        }
        return -1;
    }

    private static void addMilestone(int projectId, com.freelancer.system.model.Milestone milestone) {
        String sql = "INSERT INTO milestones(project_id, description, amount) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setString(2, milestone.getDescription());
            pstmt.setDouble(3, milestone.getAmount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding milestone: " + e.getMessage());
        }
    }

    public static void addWorkLog(int projectId, String startTime, String endTime, double duration, double cost,
            int userId) {
        String sql = "INSERT INTO work_logs(project_id, start_time, end_time, duration_hours, cost, user_id) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setString(2, startTime);
            pstmt.setString(3, endTime);
            pstmt.setDouble(4, duration);
            pstmt.setDouble(5, cost);
            pstmt.setInt(6, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding work log: " + e.getMessage());
        }
    }

    public static List<Integer> getProjectIdsForUser(int userId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT project_id FROM project_assignments WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("project_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching assigned projects: " + e.getMessage());
        }
        return ids;
    }

    public static List<Project> loadProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                double hourlyRate = 0;
                try {
                    hourlyRate = rs.getDouble("hourly_rate");
                } catch (Exception ignored) {
                }

                String typeStr = "FIXED";
                try {
                    typeStr = rs.getString("project_type");
                } catch (Exception ignored) {
                }

                com.freelancer.system.model.ProjectType type = com.freelancer.system.model.ProjectType.valueOf(typeStr);

                Project p = new Project(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("client"),
                        LocalDate.parse(rs.getString("deadline")),
                        rs.getDouble("payment"),
                        hourlyRate,
                        rs.getString("status"),
                        type);

                // Load milestones for this project
                p.setMilestones(loadMilestones(p.getId()));

                // For Hourly projects, payment = sum of work logs cost
                if (type == com.freelancer.system.model.ProjectType.HOURLY) {
                    p.setPayment(getProjectEarnings(p.getId()));
                }

                projects.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error loading projects: " + e.getMessage());
        }
        return projects;
    }

    private static void createDefaultAdmin() {
        if (getUserByEmail("admin@company.com") == null) {
            addUser(new com.freelancer.system.model.User("System Admin", "admin@company.com", "admin123",
                    com.freelancer.system.model.UserRole.ADMIN));
        }
    }

    public static void addUser(com.freelancer.system.model.User user) {
        String sql = "INSERT INTO users(name, email, password, role) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword()); // In production, hash this!
            pstmt.setString(4, user.getRole().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    public static com.freelancer.system.model.User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new com.freelancer.system.model.User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            com.freelancer.system.model.UserRole.valueOf(rs.getString("role")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
        return null;
    }

    public static List<com.freelancer.system.model.User> getAllUsers() {
        List<com.freelancer.system.model.User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new com.freelancer.system.model.User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        com.freelancer.system.model.UserRole.valueOf(rs.getString("role"))));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static void assignUserToProject(int projectId, int userId, String role) {
        String sql = "INSERT INTO project_assignments(project_id, user_id, role) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error assigning user: " + e.getMessage());
        }
    }

    public static void removeUserFromProject(int projectId, int userId) {
        String sql = "DELETE FROM project_assignments WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing user assignment: " + e.getMessage());
        }
    }

    public static List<Integer> getProjectUserIds(int projectId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM project_assignments WHERE project_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting project users: " + e.getMessage());
        }
        return userIds;
    }

    private static double getProjectEarnings(int projectId) {
        String sql = "SELECT SUM(cost) FROM work_logs WHERE project_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting project earnings: " + e.getMessage());
        }
        return 0.0;
    }

    private static List<com.freelancer.system.model.Milestone> loadMilestones(int projectId) {
        List<com.freelancer.system.model.Milestone> milestones = new ArrayList<>();
        String sql = "SELECT * FROM milestones WHERE project_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    milestones.add(new com.freelancer.system.model.Milestone(
                            rs.getInt("id"),
                            rs.getInt("project_id"),
                            rs.getString("description"),
                            rs.getDouble("amount")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading milestones: " + e.getMessage());
        }
        return milestones;
    }

    public static void updateProjectStatus(int id, String status) {
        String sql = "UPDATE projects SET status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating project status: " + e.getMessage());
        }
    }

    public static void addEscrowTransaction(com.freelancer.system.model.EscrowTransaction transaction) {
        String sql = "INSERT INTO escrow_transactions(project_id, amount, transaction_date, status, description) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaction.getProjectId());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getTransactionDate().toString());
            pstmt.setString(4, transaction.getStatus().name());
            pstmt.setString(5, transaction.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding escrow transaction: " + e.getMessage());
        }
    }

    public static List<com.freelancer.system.model.EscrowTransaction> getEscrowTransactions() {
        List<com.freelancer.system.model.EscrowTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM escrow_transactions";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new com.freelancer.system.model.EscrowTransaction(
                        rs.getInt("id"),
                        rs.getInt("project_id"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("transaction_date")),
                        com.freelancer.system.model.EscrowTransaction.TransactionStatus
                                .valueOf(rs.getString("status")),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading escrow transactions: " + e.getMessage());
        }
        return transactions;
    }

    public static void updateEscrowTransactionStatus(int id,
            com.freelancer.system.model.EscrowTransaction.TransactionStatus status) {
        String sql = "UPDATE escrow_transactions SET status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating escrow transaction: " + e.getMessage());
        }
    }

    public static double getTotalEscrowBalance() {
        String sql = "SELECT SUM(amount) FROM escrow_transactions WHERE status = 'ESCROW'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating escrow balance: " + e.getMessage());
        }
        return 0.0;
    }

    public static double getTotalClientPaid() {
        String sql = "SELECT SUM(amount) FROM escrow_transactions";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total paid: " + e.getMessage());
        }
        return 0.0;
    }

    public static double getTotalProjectValue() {
        String sql = "SELECT SUM(payment) FROM projects";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total project value: " + e.getMessage());
        }
        return 0.0;
    }

    public static void withdrawAllEscrow() {
        String sql = "UPDATE escrow_transactions SET status = 'WITHDRAWN' WHERE status = 'ESCROW'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error withdrawing all escrow: " + e.getMessage());
        }
    }
}
