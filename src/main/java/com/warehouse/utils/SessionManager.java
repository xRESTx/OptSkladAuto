package com.warehouse.utils;

public class SessionManager {
    private static String currentUserLogin;

    public static String getCurrentUserLogin() {
        return currentUserLogin;
    }

    public static void setCurrentUserLogin(String login) {
        currentUserLogin = login;
    }
}
