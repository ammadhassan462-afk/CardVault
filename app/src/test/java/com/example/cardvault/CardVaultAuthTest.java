package com.example.cardvault;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests verifying simulated authentication logic, SHA-256 password hashing,
 * and input validation constraints for CardVault.
 */
public class CardVaultAuthTest {

    @Test
    public void testPasswordHashingNeverPlaintext() {
        String plainPassword = "password123";
        String hashed = CardVaultAuthManager.hashPassword(plainPassword);

        assertNotNull(hashed);
        assertNotEquals(plainPassword, hashed);
        // SHA-256 produces a 64-character hexadecimal representation
        assertEquals(64, hashed.length());
    }

    @Test
    public void testPasswordHashingIsDeterministic() {
        String pass = "SecurePass2026!";
        String hash1 = CardVaultAuthManager.hashPassword(pass);
        String hash2 = CardVaultAuthManager.hashPassword(pass);

        assertEquals(hash1, hash2);
    }

    @Test
    public void testDifferentPasswordsProduceDistinctHashes() {
        String passA = "password123";
        String passB = "password124";

        String hashA = CardVaultAuthManager.hashPassword(passA);
        String hashB = CardVaultAuthManager.hashPassword(passB);

        assertNotEquals(hashA, hashB);
    }

    @Test
    public void testDefaultDemoAccountCredentialsConfigured() {
        assertEquals("user@cardvault.app", CardVaultAuthManager.DEFAULT_EMAIL);
        assertEquals("user", CardVaultAuthManager.DEFAULT_USERNAME);
        assertEquals("password123", CardVaultAuthManager.DEFAULT_PASSWORD);
        assertEquals("Alex Morgan", CardVaultAuthManager.DEFAULT_NAME);
    }

    @Test
    public void testNullPasswordSafety() {
        String hash = CardVaultAuthManager.hashPassword(null);
        assertEquals("", hash);
    }
}
