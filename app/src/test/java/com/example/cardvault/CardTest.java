package com.example.cardvault;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests verifying Card model business logic, formatting, and null safety.
 */
public class CardTest {

    @Test
    public void testCardInitialization() {
        Card card = new Card("Campus ID", "Student ID", "STU-9482", "08/27", "Library access", null);
        assertEquals("Campus ID", card.getName());
        assertEquals("Student ID", card.getCategory());
        assertEquals("STU-9482", card.getIdentifier());
        assertEquals("08/27", card.getExpiryDate());
        assertEquals("Library access", card.getNotes());
        assertNull(card.getImagePath());
        assertTrue(card.getCreatedAt() > 0);
    }

    @Test
    public void testDisplayIdentifierMasking() {
        // Short identifier: not masked
        Card cardShort = new Card();
        cardShort.setIdentifier("1234");
        assertEquals("1234", cardShort.getDisplayIdentifier());

        // Long identifier: masked in middle
        Card cardLong = new Card();
        cardLong.setIdentifier("STU-9482");
        assertEquals("STU •••• 482", cardLong.getDisplayIdentifier());

        // Empty identifier
        Card cardEmpty = new Card();
        cardEmpty.setIdentifier("");
        assertEquals("No identifier", cardEmpty.getDisplayIdentifier());

        // Null identifier
        Card cardNull = new Card();
        cardNull.setIdentifier(null);
        assertEquals("No identifier", cardNull.getDisplayIdentifier());
    }

    @Test
    public void testNullSafetyInGetters() {
        Card card = new Card();
        card.setName(null);
        card.setCategory(null);
        card.setIdentifier(null);
        card.setExpiryDate(null);
        card.setNotes(null);

        assertEquals("", card.getName());
        assertEquals("Other", card.getCategory());
        assertEquals("", card.getIdentifier());
        assertEquals("", card.getExpiryDate());
        assertEquals("", card.getNotes());
    }
}
