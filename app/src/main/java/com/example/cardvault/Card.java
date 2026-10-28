package com.example.cardvault;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model class representing a physical card saved in CardVault.
 */
public class Card implements Serializable {
    private long id;
    private String name;
    private String category;
    private String identifier;
    private String expiryDate;
    private String notes;
    private String imagePath;
    private long createdAt;
    private long updatedAt;

    public Card() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Card(long id, String name, String category, String identifier, String expiryDate,
                String notes, String imagePath, long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.identifier = identifier;
        this.expiryDate = expiryDate;
        this.notes = notes;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Card(String name, String category, String identifier, String expiryDate,
                String notes, String imagePath) {
        this.name = name;
        this.category = category;
        this.identifier = identifier;
        this.expiryDate = expiryDate;
        this.notes = notes;
        this.imagePath = imagePath;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category != null ? category : "Other";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIdentifier() {
        return identifier != null ? identifier : "";
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getExpiryDate() {
        return expiryDate != null ? expiryDate : "";
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a formatted creation date string.
     */
    public String getFormattedCreatedDate() {
        if (createdAt <= 0) return "Unknown";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        return sdf.format(new Date(createdAt));
    }

    /**
     * Returns a masked representation of the card identifier if long enough.
     */
    public String getDisplayIdentifier() {
        if (identifier == null || identifier.trim().isEmpty()) {
            return "No identifier";
        }
        String clean = identifier.trim();
        if (clean.length() > 6) {
            String start = clean.substring(0, 3);
            String end = clean.substring(clean.length() - 3);
            return start + " •••• " + end;
        }
        return clean;
    }
}
