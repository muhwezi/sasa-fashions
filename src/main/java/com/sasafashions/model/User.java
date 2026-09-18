package com.sasafashions.model;

import java.time.LocalDateTime;

/**
 * Represents a user permitted to access the
 * Sasa Fashions system.
 *
 * <p>The object stores the password hash but never stores
 * or exposes a plain-text password.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class User {

    private String userId;
    private String employeeId;
    private String roleId;
    private String roleName;
    private String username;
    private String passwordHash;
    private String accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean mustChangePassword;
    private int failedAttempts;
    private LocalDateTime lockedUntil;
    private LocalDateTime passwordChangedAt;

    /**
     * Creates an empty user.
     */
    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(
            String employeeId
    ) {
        this.employeeId = employeeId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(
            String roleName
    ) {
        this.roleName = roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(
            String username
    ) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(
            String passwordHash
    ) {
        this.passwordHash = passwordHash;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(
            String accountStatus
    ) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(
            LocalDateTime lastLogin
    ) {
        this.lastLogin = lastLogin;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(
            boolean mustChangePassword
    ) {
        this.mustChangePassword =
                mustChangePassword;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(
            int failedAttempts
    ) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(
            LocalDateTime lockedUntil
    ) {
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(
            LocalDateTime passwordChangedAt
    ) {
        this.passwordChangedAt =
                passwordChangedAt;
    }

    /**
     * Checks whether the account is active.
     *
     * @return true when the account status is Active
     */
    public boolean isActive() {

        return "Active".equalsIgnoreCase(
                accountStatus
        );
    }

    /**
     * Checks whether the account is currently locked.
     *
     * @return true when the locking period has not expired
     */
    public boolean isLocked() {

        return lockedUntil != null
                && lockedUntil.isAfter(
                        LocalDateTime.now()
                );
    }

    /**
     * Checks whether the user is an administrator.
     *
     * @return true for the Administrator role
     */
    public boolean isAdministrator() {

        return "Administrator".equalsIgnoreCase(
                roleName
        );
    }

    /**
     * Returns the username and role.
     *
     * @return readable user description
     */
    @Override
    public String toString() {

        return username
                + " - "
                + valueOrEmpty(roleName);
    }

    /**
     * Converts null into an empty string.
     *
     * @param value possible null value
     * @return original value or an empty string
     */
    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}