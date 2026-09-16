package com.sasafashions.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Securely hashes and verifies system-user passwords.
 *
 * <p>The class uses PBKDF2 with HMAC-SHA256, a random salt
 * and repeated hashing. Plain-text passwords are never
 * saved in the database.</p>
 *
 * @author Joshua Muhwezi
 * @version 1.0
 */
public final class PasswordHasher {

    private static final String ALGORITHM =
            "PBKDF2WithHmacSHA256";

    private static final String STORAGE_NAME =
            "PBKDF2-SHA256";

    private static final int ITERATIONS = 210_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    /**
     * Prevents creation of PasswordHasher objects.
     */
    private PasswordHasher() {
    }

    /**
     * Creates a secure password hash.
     *
     * @param password password to hash
     * @return encoded algorithm, iterations, salt and hash
     * @throws IllegalArgumentException when the password is empty
     */
    public static String hashPassword(
            char[] password
    ) {

        validatePasswordPresence(password);

        byte[] salt = new byte[SALT_LENGTH];

        new SecureRandom().nextBytes(salt);

        byte[] hash = createHash(
                password,
                salt,
                ITERATIONS
        );

        return STORAGE_NAME
                + "$"
                + ITERATIONS
                + "$"
                + Base64.getEncoder()
                        .encodeToString(salt)
                + "$"
                + Base64.getEncoder()
                        .encodeToString(hash);
    }

    /**
     * Verifies a password against a stored password hash.
     *
     * @param password password entered during login
     * @param storedHash password hash from the database
     * @return true when the password is correct
     */
    public static boolean verifyPassword(
            char[] password,
            String storedHash
    ) {

        if (password == null
                || storedHash == null
                || storedHash.isBlank()) {
            return false;
        }

        try {
            String[] parts =
                    storedHash.split("\\$");

            if (parts.length != 4
                    || !STORAGE_NAME.equals(
                            parts[0]
                    )) {
                return false;
            }

            int iterations =
                    Integer.parseInt(parts[1]);

            byte[] salt =
                    Base64.getDecoder()
                            .decode(parts[2]);

            byte[] expectedHash =
                    Base64.getDecoder()
                            .decode(parts[3]);

            byte[] actualHash =
                    createHash(
                            password,
                            salt,
                            iterations
                    );

            return MessageDigest.isEqual(
                    expectedHash,
                    actualHash
            );

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * Applies PBKDF2 to a password.
     *
     * @param password password characters
     * @param salt random salt
     * @param iterations hashing iterations
     * @return generated password hash bytes
     */
    private static byte[] createHash(
            char[] password,
            byte[] salt,
            int iterations
    ) {

        PBEKeySpec specification =
                new PBEKeySpec(
                        password,
                        salt,
                        iterations,
                        KEY_LENGTH
                );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            ALGORITHM
                    );

            return factory.generateSecret(
                    specification
            ).getEncoded();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Password hashing is unavailable.",
                    exception
            );

        } finally {

            specification.clearPassword();
        }
    }

    /**
     * Confirms that a password was supplied.
     *
     * @param password password characters
     */
    private static void validatePasswordPresence(
            char[] password
    ) {

        if (password == null
                || password.length == 0) {

            throw new IllegalArgumentException(
                    "Password is required."
            );
        }
    }
}