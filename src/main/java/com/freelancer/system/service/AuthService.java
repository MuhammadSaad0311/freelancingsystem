package com.freelancer.system.service;

import com.freelancer.system.db.DatabaseManager;
import com.freelancer.system.model.User;

public class AuthService {
    private static User currentUser;

    public static User login(String email, String password) {
        User user = DatabaseManager.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == com.freelancer.system.model.UserRole.ADMIN;
    }
}
