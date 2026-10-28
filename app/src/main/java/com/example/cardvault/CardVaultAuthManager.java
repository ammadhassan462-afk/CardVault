package com.example.cardvault;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Local simulated authentication and session manager for CardVault.
 * Stores demonstration credentials securely using SHA-256 hashed passwords
 * in SharedPreferences. Zero external network calls.
 */
public class CardVaultAuthManager {

    private static final String PREFS_NAME = "cardvault_auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_EMAIL = "current_email";
    private static final String KEY_CURRENT_NAME = "current_name";
    private static final String KEY_CURRENT_PHONE = "current_phone";

    private static final String PREFIX_HASH = "hash_";
    private static final String PREFIX_NAME = "name_";
    private static final String PREFIX_PHONE = "phone_";
    private static final String PREFIX_USER_EMAIL = "user_email_"; // maps username to email

    // Default Seed Demo Account
    public static final String DEFAULT_EMAIL = "user@cardvault.app";
    public static final String DEFAULT_USERNAME = "user";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final String DEFAULT_NAME = "Alex Morgan";
    public static final String DEFAULT_PHONE = "+1 (555) 019-2834";

    private static CardVaultAuthManager instance;
    private final SharedPreferences prefs;

    private CardVaultAuthManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ensureDefaultUserSeeded();
    }

    public static synchronized CardVaultAuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new CardVaultAuthManager(context);
        }
        return instance;
    }

    private void ensureDefaultUserSeeded() {
        String normalizedEmail = normalize(DEFAULT_EMAIL);
        if (!prefs.contains(PREFIX_HASH + normalizedEmail)) {
            String hash = hashPassword(DEFAULT_PASSWORD);
            prefs.edit()
                    .putString(PREFIX_HASH + normalizedEmail, hash)
                    .putString(PREFIX_NAME + normalizedEmail, DEFAULT_NAME)
                    .putString(PREFIX_PHONE + normalizedEmail, DEFAULT_PHONE)
                    .putString(PREFIX_USER_EMAIL + normalize(DEFAULT_USERNAME), normalizedEmail)
                    .apply();
        }
    }

    private String normalize(String input) {
        return (input == null) ? "" : input.trim().toLowerCase();
    }

    /**
     * Hashes password using SHA-256 to ensure plaintext passwords are never stored.
     */
    public static String hashPassword(String password) {
        if (password == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback
            return String.valueOf(password.hashCode());
        }
    }

    /**
     * Resolves an identifier (either email or username) to its normalized email.
     */
    public String resolveToEmail(String identifier) {
        String normalized = normalize(identifier);
        if (normalized.contains("@")) {
            return normalized;
        }
        // Check username alias mapping
        return prefs.getString(PREFIX_USER_EMAIL + normalized, normalized);
    }

    /**
     * Authenticates locally using SHA-256 hash comparison.
     */
    public boolean authenticate(String identifier, String password) {
        String email = resolveToEmail(identifier);
        String storedHash = prefs.getString(PREFIX_HASH + email, null);
        if (storedHash != null) {
            String inputHash = hashPassword(password);
            return storedHash.equals(inputHash);
        }
        return false;
    }

    /**
     * Registers a new local demo user.
     */
    public boolean registerUser(String name, String email, String phone, String password) {
        String normalizedEmail = normalize(email);
        String hash = hashPassword(password);
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFIX_HASH + normalizedEmail, hash);
        editor.putString(PREFIX_NAME + normalizedEmail, name.trim());
        editor.putString(PREFIX_PHONE + normalizedEmail, phone != null ? phone.trim() : "");
        
        // Also map username if email has prefix before @
        if (normalizedEmail.contains("@")) {
            String username = normalizedEmail.substring(0, normalizedEmail.indexOf("@"));
            editor.putString(PREFIX_USER_EMAIL + username, normalizedEmail);
        }
        editor.apply();
        return true;
    }

    /**
     * Updates password locally for password recovery.
     */
    public boolean resetPassword(String email, String newPassword) {
        String normalizedEmail = resolveToEmail(email);
        String hash = hashPassword(newPassword);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFIX_HASH + normalizedEmail, hash);
        if (!prefs.contains(PREFIX_NAME + normalizedEmail)) {
            editor.putString(PREFIX_NAME + normalizedEmail, DEFAULT_NAME);
        }
        editor.apply();
        return true;
    }

    public boolean isEmailRegistered(String email) {
        String resolved = resolveToEmail(email);
        return prefs.contains(PREFIX_HASH + resolved);
    }

    public String getUserName(String identifier) {
        String email = resolveToEmail(identifier);
        return prefs.getString(PREFIX_NAME + email, DEFAULT_NAME);
    }

    public String getUserPhone(String identifier) {
        String email = resolveToEmail(identifier);
        return prefs.getString(PREFIX_PHONE + email, DEFAULT_PHONE);
    }

    public void setCurrentSession(String email, String name, String phone) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_CURRENT_EMAIL, resolveToEmail(email))
                .putString(KEY_CURRENT_NAME, name)
                .putString(KEY_CURRENT_PHONE, phone != null ? phone : "")
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getCurrentEmail() {
        return prefs.getString(KEY_CURRENT_EMAIL, DEFAULT_EMAIL);
    }

    public String getCurrentName() {
        return prefs.getString(KEY_CURRENT_NAME, DEFAULT_NAME);
    }

    public String getCurrentPhone() {
        return prefs.getString(KEY_CURRENT_PHONE, DEFAULT_PHONE);
    }

    public void logout() {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_EMAIL)
                .remove(KEY_CURRENT_NAME)
                .remove(KEY_CURRENT_PHONE)
                .apply();
    }
}
