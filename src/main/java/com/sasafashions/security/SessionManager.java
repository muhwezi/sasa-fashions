package com.sasafashions.security;

import com.sasafashions.model.User;

/**
 * Maintains information about the user currently logged into the
 * Sasa Fashions Management System.
 *
 * <p>This class uses static methods because only one user session
 * should be active while the desktop application is running.</p>
 *
 * @author SASA Group
 */
public final class SessionManager {

    /**
     * The user currently logged into the application.
     */
    private static User currentUser;

    /**
     * Prevents objects of SessionManager from being created.
     */
    private SessionManager() {
    }

    /**
     * Starts a new application session.
     *
     * @param user authenticated user
     * @throws IllegalArgumentException when the supplied user is null
     */
    public static synchronized void login(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "The logged-in user cannot be null."
            );
        }

        currentUser = user;
    }

    /**
     * Ends the current application session.
     */
    public static synchronized void logout() {
        currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return current user, or null when nobody is logged in
     */
    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return true when a session exists
     */
    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Returns the ID of the currently logged-in user.
     *
     * @return user ID, or null when nobody is logged in
     */
    public static synchronized String getCurrentUserId() {

        if (currentUser == null) {
            return null;
        }

        return currentUser.getUserId();
    }

    /**
     * Returns the username of the currently logged-in user.
     *
     * @return username, or an empty string when nobody is logged in
     */
    public static synchronized String getCurrentUsername() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getUsername();
    }

    /**
     * Checks whether the logged-in user is an administrator.
     *
     * @return true when the current user has the Administrator role
     */
    public static synchronized boolean isAdministrator() {

        return currentUser != null
                && currentUser.isAdministrator();
    }

    /**
     * Ensures that a user has logged into the application.
     *
     * @throws IllegalStateException when no user is logged in
     */
    public static synchronized void requireLogin() {

        if (currentUser == null) {
            throw new IllegalStateException(
                    "You must log in before using the application."
            );
        }
    }

    /**
     * Ensures that the logged-in user is an administrator.
     *
     * @throws SecurityException when the user is not an administrator
     */
    public static synchronized void requireAdministrator() {

        requireLogin();

        if (!currentUser.isAdministrator()) {
            throw new SecurityException(
                    "Administrator permission is required."
            );
        }
    }
}