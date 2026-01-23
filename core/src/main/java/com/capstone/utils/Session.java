package com.capstone.utils;

import com.capstone.models.User;

public class Session {

    private static User currentUser;

    private Session() {} // prevent instantiation

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}