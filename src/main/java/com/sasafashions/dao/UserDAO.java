package com.sasafashions.dao;

import com.sasafashions.dao.util.IdGenerator;
import com.sasafashions.database.DatabaseConnection;
import com.sasafashions.model.User;
import com.sasafashions.security.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Provides database and authentication operations
 * for system-user accounts.
 *
 * <p>Passwords are always hashed before being stored.
 * Accounts are temporarily locked after three consecutive
 * unsuccessful login attempts.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class UserDAO {

    private static final int MAXIMUM_FAILED_ATTEMPTS = 3;
    private static final int LOCK_MINUTES = 15;

    /**
     * Generates the next user identifier.
     *
     * @return an identifier such as USR-0001
     * @throws SQLException when generation fails
     */
    public String generateNextUserId()
            throws SQLException {

        return IdGenerator.generateNextUserId();
    }

    /**
     * Creates a new user with a temporary password.
     *
     * @param user user-account information
     * @param initialPassword initial password
     * @param performedByUserId administrator creating the account
     * @throws SQLException when database access fails
     */
    public void createUser(
            User user,
            char[] initialPassword,
            String performedByUserId
    ) throws SQLException {

        validateUser(user);
        validateNewPassword(initialPassword);

        if (isBlank(user.getUserId())) {

            user.setUserId(
                    generateNextUserId()
            );
        }

        if (isBlank(user.getAccountStatus())) {
            user.setAccountStatus("Active");
        }

        String passwordHash =
                PasswordHasher.hashPassword(
                        initialPassword
                );

        String sql = """
                INSERT INTO users (
                    user_id,
                    employee_id,
                    role_id,
                    username,
                    password_hash,
                    account_status,
                    must_change_password,
                    failed_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, 1, 0)
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(
                            1,
                            user.getUserId()
                    );

                    setNullableString(
                            statement,
                            2,
                            user.getEmployeeId()
                    );

                    statement.setString(
                            3,
                            user.getRoleId()
                    );

                    statement.setString(
                            4,
                            user.getUsername()
                                    .trim()
                                    .toLowerCase()
                    );

                    statement.setString(
                            5,
                            passwordHash
                    );

                    statement.setString(
                            6,
                            user.getAccountStatus()
                    );

                    statement.executeUpdate();
                }

                insertAuditLog(
                        connection,
                        performedByUserId,
                        "CREATE_USER",
                        user.getUserId(),
                        "Created user account: "
                        + user.getUsername()
                );

                connection.commit();

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Authenticates a username and password.
     *
     * @param username entered username
     * @param password entered password
     * @return authenticated user
     * @throws SQLException when database access fails
     * @throws SecurityException when authentication fails
     */
    public User authenticate(
            String username,
            char[] password
    ) throws SQLException {

        if (isBlank(username)
                || password == null
                || password.length == 0) {

            throw new SecurityException(
                    "Username and password are required."
            );
        }

        String sql = """
                SELECT
                    u.*,
                    r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                WHERE LOWER(u.username) = LOWER(?)
                FOR UPDATE
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                User user = null;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(
                            1,
                            username.trim()
                    );

                    try (ResultSet resultSet =
                                 statement.executeQuery()) {

                        if (resultSet.next()) {
                            user = createUser(resultSet);
                        }
                    }
                }

                if (user == null) {

                    insertAuditLog(
                            connection,
                            null,
                            "LOGIN_FAILED",
                            null,
                            "Unknown username attempted login: "
                            + username.trim()
                    );

                    connection.commit();

                    throw new SecurityException(
                            "Invalid username or password."
                    );
                }

                if (!user.isActive()) {

                    insertAuditLog(
                            connection,
                            user.getUserId(),
                            "LOGIN_BLOCKED",
                            user.getUserId(),
                            "Login rejected because account is "
                            + user.getAccountStatus()
                    );

                    connection.commit();

                    throw new SecurityException(
                            "This account is not active."
                    );
                }

                LocalDateTime now =
                        LocalDateTime.now();

                if (user.getLockedUntil() != null
                        && user.getLockedUntil()
                                .isAfter(now)) {

                    insertAuditLog(
                            connection,
                            user.getUserId(),
                            "LOGIN_BLOCKED",
                            user.getUserId(),
                            "Login attempted during account lock"
                    );

                    connection.commit();

                    throw new SecurityException(
                            "Account is temporarily locked until "
                            + user.getLockedUntil()
                    );
                }

                /*
                 * If the old lock has expired, begin a new
                 * failed-attempt count.
                 */
                if (user.getLockedUntil() != null
                        && !user.getLockedUntil()
                                .isAfter(now)) {

                    user.setFailedAttempts(0);
                    user.setLockedUntil(null);
                }

                boolean passwordCorrect =
                        PasswordHasher.verifyPassword(
                                password,
                                user.getPasswordHash()
                        );

                if (!passwordCorrect) {

                    processFailedLogin(
                            connection,
                            user
                    );

                    insertAuditLog(
                            connection,
                            user.getUserId(),
                            "LOGIN_FAILED",
                            user.getUserId(),
                            "Incorrect password"
                    );

                    connection.commit();

                    if (user.getLockedUntil() != null) {

                        throw new SecurityException(
                                "Account locked for "
                                + LOCK_MINUTES
                                + " minutes after "
                                + MAXIMUM_FAILED_ATTEMPTS
                                + " failed login attempts."
                        );
                    }

                    int remaining =
                            MAXIMUM_FAILED_ATTEMPTS
                            - user.getFailedAttempts();

                    throw new SecurityException(
                            "Invalid username or password.\n"
                            + "Attempts remaining: "
                            + remaining
                    );
                }

                recordSuccessfulLogin(
                        connection,
                        user
                );

                insertAuditLog(
                        connection,
                        user.getUserId(),
                        "LOGIN_SUCCESS",
                        user.getUserId(),
                        "User logged into the system"
                );

                connection.commit();

                user.setLastLogin(now);
                user.setFailedAttempts(0);
                user.setLockedUntil(null);

                return user;

            } catch (SQLException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Finds a user by user ID.
     *
     * @param userId user identifier
     * @return matching user, or null
     * @throws SQLException when database access fails
     */
    public User findUser(
            String userId
    ) throws SQLException {

        String sql = """
                SELECT
                    u.*,
                    r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                WHERE u.user_id = ?
                """;

        return findSingleUser(sql, userId);
    }

    /**
     * Finds a user by username.
     *
     * @param username system username
     * @return matching user, or null
     * @throws SQLException when database access fails
     */
    public User findUserByUsername(
            String username
    ) throws SQLException {

        String sql = """
                SELECT
                    u.*,
                    r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                WHERE LOWER(u.username) = LOWER(?)
                """;

        return findSingleUser(sql, username);
    }

    /**
     * Returns all system users.
     *
     * @return list containing all users
     * @throws SQLException when database access fails
     */
    public ArrayList<User> findAllUsers()
            throws SQLException {

        ArrayList<User> users =
                new ArrayList<>();

        String sql = """
                SELECT
                    u.*,
                    r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                ORDER BY u.user_id
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
        }

        return users;
    }

    /**
     * Updates non-password user information.
     *
     * @param user updated user information
     * @param performedByUserId administrator performing the update
     * @return true when the user was updated
     * @throws SQLException when database access fails
     */
    public boolean updateUser(
            User user,
            String performedByUserId
    ) throws SQLException {

        validateUser(user);

        if (isBlank(user.getUserId())) {

            throw new IllegalArgumentException(
                    "User ID is required when editing."
            );
        }

        String sql = """
                UPDATE users
                SET employee_id = ?,
                    role_id = ?,
                    username = ?,
                    account_status = ?
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                boolean updated;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    setNullableString(
                            statement,
                            1,
                            user.getEmployeeId()
                    );

                    statement.setString(
                            2,
                            user.getRoleId()
                    );

                    statement.setString(
                            3,
                            user.getUsername()
                                    .trim()
                                    .toLowerCase()
                    );

                    statement.setString(
                            4,
                            user.getAccountStatus()
                    );

                    statement.setString(
                            5,
                            user.getUserId()
                    );

                    updated =
                            statement.executeUpdate() > 0;
                }

                if (updated) {

                    insertAuditLog(
                            connection,
                            performedByUserId,
                            "UPDATE_USER",
                            user.getUserId(),
                            "Updated user account: "
                            + user.getUsername()
                    );
                }

                connection.commit();
                return updated;

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Resets a user's password to a temporary password.
     *
     * <p>The user must change the temporary password
     * after their next login.</p>
     *
     * @param userId user being reset
     * @param temporaryPassword new temporary password
     * @param performedByUserId administrator performing the reset
     * @throws SQLException when database access fails
     */
    public void resetPassword(
            String userId,
            char[] temporaryPassword,
            String performedByUserId
    ) throws SQLException {

        validateNewPassword(temporaryPassword);

        String passwordHash =
                PasswordHasher.hashPassword(
                        temporaryPassword
                );

        String sql = """
                UPDATE users
                SET password_hash = ?,
                    must_change_password = 1,
                    failed_attempts = 0,
                    locked_until = NULL,
                    password_changed_at = CURRENT_TIMESTAMP
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int affectedRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(
                            1,
                            passwordHash
                    );

                    statement.setString(
                            2,
                            userId
                    );

                    affectedRows =
                            statement.executeUpdate();
                }

                if (affectedRows == 0) {

                    throw new SQLException(
                            "User was not found: " + userId
                    );
                }

                insertAuditLog(
                        connection,
                        performedByUserId,
                        "RESET_PASSWORD",
                        userId,
                        "Administrator reset user password"
                );

                connection.commit();

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Changes a user's password after verifying
     * the current password.
     *
     * @param userId user changing the password
     * @param currentPassword current password
     * @param newPassword replacement password
     * @throws SQLException when database access fails
     */
    public void changePassword(
            String userId,
            char[] currentPassword,
            char[] newPassword
    ) throws SQLException {

        validateNewPassword(newPassword);

        String selectSql = """
                SELECT password_hash
                FROM users
                WHERE user_id = ?
                FOR UPDATE
                """;

        String updateSql = """
                UPDATE users
                SET password_hash = ?,
                    must_change_password = 0,
                    failed_attempts = 0,
                    locked_until = NULL,
                    password_changed_at = CURRENT_TIMESTAMP
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                String existingHash = null;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     selectSql
                             )) {

                    statement.setString(1, userId);

                    try (ResultSet resultSet =
                                 statement.executeQuery()) {

                        if (resultSet.next()) {

                            existingHash =
                                    resultSet.getString(
                                            "password_hash"
                                    );
                        }
                    }
                }

                if (existingHash == null) {

                    throw new SQLException(
                            "User was not found: " + userId
                    );
                }

                if (!PasswordHasher.verifyPassword(
                        currentPassword,
                        existingHash
                )) {

                    throw new SecurityException(
                            "The current password is incorrect."
                    );
                }

                if (PasswordHasher.verifyPassword(
                        newPassword,
                        existingHash
                )) {

                    throw new IllegalArgumentException(
                            "The new password must be different "
                            + "from the current password."
                    );
                }

                String newHash =
                        PasswordHasher.hashPassword(
                                newPassword
                        );

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     updateSql
                             )) {

                    statement.setString(1, newHash);
                    statement.setString(2, userId);
                    statement.executeUpdate();
                }

                insertAuditLog(
                        connection,
                        userId,
                        "CHANGE_PASSWORD",
                        userId,
                        "User changed their password"
                );

                connection.commit();

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Removes a temporary account lock.
     *
     * @param userId user to unlock
     * @param performedByUserId administrator unlocking the account
     * @throws SQLException when database access fails
     */
    public void unlockAccount(
            String userId,
            String performedByUserId
    ) throws SQLException {

        String sql = """
                UPDATE users
                SET failed_attempts = 0,
                    locked_until = NULL
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, userId);

                    if (statement.executeUpdate() == 0) {

                        throw new SQLException(
                                "User was not found: "
                                + userId
                        );
                    }
                }

                insertAuditLog(
                        connection,
                        performedByUserId,
                        "UNLOCK_ACCOUNT",
                        userId,
                        "Administrator unlocked account"
                );

                connection.commit();

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Deletes a user account.
     *
     * @param userId user to delete
     * @param performedByUserId administrator performing the deletion
     * @return true when the user was deleted
     * @throws SQLException when database access fails
     */
    public boolean deleteUser(
            String userId,
            String performedByUserId
    ) throws SQLException {

        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                boolean deleted;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, userId);

                    deleted =
                            statement.executeUpdate() > 0;
                }

                if (deleted) {

                    insertAuditLog(
                            connection,
                            performedByUserId,
                            "DELETE_USER",
                            userId,
                            "Deleted user account"
                    );
                }

                connection.commit();
                return deleted;

            } catch (SQLException | RuntimeException exception) {

                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Returns the first user record.
     *
     * @return first user, or null
     * @throws SQLException when database access fails
     */
    public User findFirstUser()
            throws SQLException {

        String sql = """
                SELECT u.*, r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                ORDER BY u.user_id ASC
                LIMIT 1
                """;

        return findSingleUser(sql, null);
    }

    /**
     * Returns the last user record.
     *
     * @return last user, or null
     * @throws SQLException when database access fails
     */
    public User findLastUser()
            throws SQLException {

        String sql = """
                SELECT u.*, r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                ORDER BY u.user_id DESC
                LIMIT 1
                """;

        return findSingleUser(sql, null);
    }

    /**
     * Returns the next user.
     *
     * @param currentUserId current user identifier
     * @return next user, or null
     * @throws SQLException when database access fails
     */
    public User findNextUser(
            String currentUserId
    ) throws SQLException {

        String sql = """
                SELECT u.*, r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                WHERE u.user_id > ?
                ORDER BY u.user_id ASC
                LIMIT 1
                """;

        return findSingleUser(
                sql,
                currentUserId
        );
    }

    /**
     * Returns the previous user.
     *
     * @param currentUserId current user identifier
     * @return previous user, or null
     * @throws SQLException when database access fails
     */
    public User findPreviousUser(
            String currentUserId
    ) throws SQLException {

        String sql = """
                SELECT u.*, r.role_name
                FROM users u
                INNER JOIN roles r
                    ON u.role_id = r.role_id
                WHERE u.user_id < ?
                ORDER BY u.user_id DESC
                LIMIT 1
                """;

        return findSingleUser(
                sql,
                currentUserId
        );
    }

    /**
     * Records an unsuccessful password attempt.
     *
     * @param connection active transaction connection
     * @param user affected user
     * @throws SQLException when the user cannot be updated
     */
    private void processFailedLogin(
            Connection connection,
            User user
    ) throws SQLException {

        int failedAttempts =
                user.getFailedAttempts() + 1;

        LocalDateTime lockedUntil = null;

        if (failedAttempts
                >= MAXIMUM_FAILED_ATTEMPTS) {

            lockedUntil =
                    LocalDateTime.now()
                            .plusMinutes(
                                    LOCK_MINUTES
                            );

            failedAttempts = 0;
        }

        String sql = """
                UPDATE users
                SET failed_attempts = ?,
                    locked_until = ?
                WHERE user_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    failedAttempts
            );

            if (lockedUntil == null) {

                statement.setNull(
                        2,
                        Types.TIMESTAMP
                );

            } else {

                statement.setTimestamp(
                        2,
                        Timestamp.valueOf(
                                lockedUntil
                        )
                );
            }

            statement.setString(
                    3,
                    user.getUserId()
            );

            statement.executeUpdate();
        }

        user.setFailedAttempts(failedAttempts);
        user.setLockedUntil(lockedUntil);
    }

    /**
     * Records a successful login.
     *
     * @param connection active transaction connection
     * @param user authenticated user
     * @throws SQLException when the user cannot be updated
     */
    private void recordSuccessfulLogin(
            Connection connection,
            User user
    ) throws SQLException {

        String sql = """
                UPDATE users
                SET last_login = CURRENT_TIMESTAMP,
                    failed_attempts = 0,
                    locked_until = NULL
                WHERE user_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    user.getUserId()
            );

            statement.executeUpdate();
        }
    }

    /**
 * Writes an audit entry using the current transaction.
 *
 * @param connection active transaction connection
 * @param performedByUserId user performing the action
 * @param actionType action type
 * @param recordId affected user record
 * @param description action description
 * @throws SQLException when audit logging fails
 */
private void insertAuditLog(
        Connection connection,
        String performedByUserId,
        String actionType,
        String recordId,
        String description
) throws SQLException {

    String sql = """
            INSERT INTO audit_logs (
                user_id,
                action_type,
                table_name,
                record_id,
                description
            )
            VALUES (?, ?, 'users', ?, ?)
            """;

    try (PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        setNullableString(
                statement,
                1,
                performedByUserId
        );

        statement.setString(
                2,
                actionType
        );

        setNullableString(
                statement,
                3,
                recordId
        );

        setNullableString(
                statement,
                4,
                description
        );

        statement.executeUpdate();
    }
}

    /**
     * Runs a query that returns one user.
     *
     * @param sql user query
     * @param parameter optional query parameter
     * @return matching user, or null
     * @throws SQLException when database access fails
     */
    private User findSingleUser(
            String sql,
            String parameter
    ) throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            if (parameter != null) {
                statement.setString(1, parameter);
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return createUser(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Converts a database result into a User object.
     *
     * @param resultSet user database result
     * @return created user
     * @throws SQLException when values cannot be read
     */
    private User createUser(
            ResultSet resultSet
    ) throws SQLException {

        User user = new User();

        user.setUserId(
                resultSet.getString("user_id")
        );

        user.setEmployeeId(
                resultSet.getString("employee_id")
        );

        user.setRoleId(
                resultSet.getString("role_id")
        );

        user.setRoleName(
                resultSet.getString("role_name")
        );

        user.setUsername(
                resultSet.getString("username")
        );

        user.setPasswordHash(
                resultSet.getString("password_hash")
        );

        user.setAccountStatus(
                resultSet.getString("account_status")
        );

        user.setCreatedAt(
                toLocalDateTime(
                        resultSet.getTimestamp(
                                "created_at"
                        )
                )
        );

        user.setLastLogin(
                toLocalDateTime(
                        resultSet.getTimestamp(
                                "last_login"
                        )
                )
        );

        user.setMustChangePassword(
                resultSet.getBoolean(
                        "must_change_password"
                )
        );

        user.setFailedAttempts(
                resultSet.getInt(
                        "failed_attempts"
                )
        );

        user.setLockedUntil(
                toLocalDateTime(
                        resultSet.getTimestamp(
                                "locked_until"
                        )
                )
        );

        user.setPasswordChangedAt(
                toLocalDateTime(
                        resultSet.getTimestamp(
                                "password_changed_at"
                        )
                )
        );

        return user;
    }

    /**
     * Validates basic user information.
     *
     * @param user user to validate
     */
    private void validateUser(User user) {

        if (user == null) {

            throw new IllegalArgumentException(
                    "User information is required."
            );
        }

        if (isBlank(user.getRoleId())) {

            throw new IllegalArgumentException(
                    "Please select a role."
            );
        }

        if (isBlank(user.getUsername())) {

            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (!user.getUsername().matches(
                "[A-Za-z0-9._-]{3,50}"
        )) {

            throw new IllegalArgumentException(
                    "Username must contain 3 to 50 letters, "
                    + "numbers, dots, underscores or hyphens."
            );
        }

        if (isBlank(user.getAccountStatus())) {

            throw new IllegalArgumentException(
                    "Account status is required."
            );
        }
    }

    /**
     * Validates password strength.
     *
     * @param password password to validate
     */
    private void validateNewPassword(
            char[] password
    ) {

        if (password == null
                || password.length < 8) {

            throw new IllegalArgumentException(
                    "Password must contain at least "
                    + "8 characters."
            );
        }

        String value = new String(password);

        boolean hasUpper =
                value.matches(".*[A-Z].*");

        boolean hasLower =
                value.matches(".*[a-z].*");

        boolean hasDigit =
                value.matches(".*[0-9].*");

        boolean hasSpecial =
                value.matches(
                        ".*[^A-Za-z0-9].*"
                );

        if (!hasUpper
                || !hasLower
                || !hasDigit
                || !hasSpecial) {

            throw new IllegalArgumentException(
                    "Password must contain uppercase, "
                    + "lowercase, number and special character."
            );
        }
    }

    /**
     * Converts a SQL timestamp into LocalDateTime.
     *
     * @param timestamp SQL timestamp
     * @return local date-time or null
     */
    private LocalDateTime toLocalDateTime(
            Timestamp timestamp
    ) {

        return timestamp == null
                ? null
                : timestamp.toLocalDateTime();
    }

    /**
     * Assigns a string or SQL NULL.
     *
     * @param statement prepared statement
     * @param parameterNumber parameter number
     * @param value string value
     * @throws SQLException when assignment fails
     */
    private void setNullableString(
            PreparedStatement statement,
            int parameterNumber,
            String value
    ) throws SQLException {

        if (isBlank(value)) {

            statement.setNull(
                    parameterNumber,
                    Types.VARCHAR
            );

        } else {

            statement.setString(
                    parameterNumber,
                    value.trim()
            );
        }
    }

    /**
     * Checks whether a string is null or blank.
     *
     * @param value string value
     * @return true when null or blank
     */
    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }

    /**
     * Rolls back a transaction without replacing
     * the original exception.
     *
     * @param connection database connection
     */
    private void rollbackQuietly(
            Connection connection
    ) {

        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Preserve the original exception.
        }
    }
}