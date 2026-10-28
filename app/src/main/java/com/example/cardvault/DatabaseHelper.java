package com.example.cardvault;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite helper managing local persistent storage for CardVault.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "cardvault.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_EXPIRY = "expiry_date";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_CARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    COLUMN_IDENTIFIER + " TEXT, " +
                    COLUMN_EXPIRY + " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_IMAGE_PATH + " TEXT, " +
                    COLUMN_CREATED_AT + " INTEGER, " +
                    COLUMN_UPDATED_AT + " INTEGER" +
                    ");";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        seedSampleCards(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 for now; handle migrations here if updated in the future
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }

    /**
     * Seeds clearly fictional sample cards on initial database creation only.
     */
    private void seedSampleCards(SQLiteDatabase db) {
        long now = System.currentTimeMillis();

        insertSampleCard(db, "Campus Student ID", "Student ID", "STU-9482", "08/27",
                "Main campus library, laboratories, and residence hall access.", now - 3600000 * 3);

        insertSampleCard(db, "City Central Library", "Library Card", "LIB-4091", "12/28",
                "Borrowing limit: 10 books, full access to online journals and study rooms.", now - 3600000 * 2);

        insertSampleCard(db, "Metro Express Transit", "Transit Pass", "METRO-8820", "06/27",
                "Tap on turnstiles and regional bus card readers.", now - 3600000);

        insertSampleCard(db, "FitClub Gym Membership", "Membership", "FIT-3104", "10/26",
                "Scan at front reception. Includes pool, fitness floor, and locker facilities.", now);
    }

    private void insertSampleCard(SQLiteDatabase db, String name, String category,
                                  String identifier, String expiry, String notes, long timestamp) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_IDENTIFIER, identifier);
        values.put(COLUMN_EXPIRY, expiry);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_IMAGE_PATH, (String) null);
        values.put(COLUMN_CREATED_AT, timestamp);
        values.put(COLUMN_UPDATED_AT, timestamp);
        db.insert(TABLE_CARDS, null, values);
    }

    /**
     * Inserts a new card record.
     */
    public long insertCard(Card card) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, card.getName());
        values.put(COLUMN_CATEGORY, card.getCategory());
        values.put(COLUMN_IDENTIFIER, card.getIdentifier());
        values.put(COLUMN_EXPIRY, card.getExpiryDate());
        values.put(COLUMN_NOTES, card.getNotes());
        values.put(COLUMN_IMAGE_PATH, card.getImagePath());
        long now = System.currentTimeMillis();
        values.put(COLUMN_CREATED_AT, card.getCreatedAt() > 0 ? card.getCreatedAt() : now);
        values.put(COLUMN_UPDATED_AT, now);

        long id = db.insert(TABLE_CARDS, null, values);
        if (id != -1) {
            card.setId(id);
        }
        return id;
    }

    /**
     * Updates an existing card record.
     */
    public int updateCard(Card card) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, card.getName());
        values.put(COLUMN_CATEGORY, card.getCategory());
        values.put(COLUMN_IDENTIFIER, card.getIdentifier());
        values.put(COLUMN_EXPIRY, card.getExpiryDate());
        values.put(COLUMN_NOTES, card.getNotes());
        values.put(COLUMN_IMAGE_PATH, card.getImagePath());
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());

        return db.update(TABLE_CARDS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(card.getId())});
    }

    /**
     * Deletes a card by ID.
     */
    public int deleteCard(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_CARDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Fetches a single card by ID.
     */
    public Card getCardById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, null,
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        Card card = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    card = cursorToCard(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return card;
    }

    /**
     * Returns total count of cards stored.
     */
    public int getCardCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CARDS, null);
        int count = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * Returns all cards ordered by most recently updated/added first.
     */
    public List<Card> getAllCards() {
        return searchAndFilterCards(null, null);
    }

    /**
     * Returns recent cards up to limit.
     */
    public List<Card> getRecentCards(int limit) {
        SQLiteDatabase db = getReadableDatabase();
        List<Card> list = new ArrayList<>();
        String limitClause = limit > 0 ? String.valueOf(limit) : "5";

        Cursor cursor = db.query(TABLE_CARDS, null, null, null, null, null,
                COLUMN_UPDATED_AT + " DESC", limitClause);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursorToCard(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Combined search by name and filter by category.
     */
    public List<Card> searchAndFilterCards(String query, String category) {
        SQLiteDatabase db = getReadableDatabase();
        List<Card> list = new ArrayList<>();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All");

        if (hasQuery) {
            selection.append(COLUMN_NAME).append(" LIKE ?");
            selectionArgs.add("%" + query.trim() + "%");
        }

        if (hasCategory) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(COLUMN_CATEGORY).append(" = ?");
            selectionArgs.add(category.trim());
        }

        String sel = selection.length() > 0 ? selection.toString() : null;
        String[] args = selectionArgs.isEmpty() ? null : selectionArgs.toArray(new String[0]);

        Cursor cursor = db.query(TABLE_CARDS, null, sel, args, null, null,
                COLUMN_UPDATED_AT + " DESC");

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursorToCard(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Deletes all cards in the database.
     */
    public void deleteAllCards() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CARDS, null, null);
    }

    private Card cursorToCard(Cursor cursor) {
        Card card = new Card();
        card.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        card.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        card.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
        card.setIdentifier(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IDENTIFIER)));
        card.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPIRY)));
        card.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
        card.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
        card.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
        card.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT)));
        return card;
    }
}
