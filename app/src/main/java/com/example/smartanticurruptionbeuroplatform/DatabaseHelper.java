package com.example.smartanticurruptionbeuroplatform;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Local prototype storage. Use a secured cloud backend for real multi-device cases. */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ACBDatabase.db";
    private static final int DB_VERSION = 4;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE complaints (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, category TEXT NOT NULL, department TEXT NOT NULL, " +
                "district TEXT NOT NULL, description TEXT NOT NULL, anonymous INTEGER NOT NULL DEFAULT 0, " +
                "status TEXT NOT NULL DEFAULT 'Submitted', created_at INTEGER NOT NULL, " +
                "latitude REAL, longitude REAL, evidence_path TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, password TEXT NOT NULL)");
        createComplaintEventsTable(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE complaints ADD COLUMN status TEXT NOT NULL DEFAULT 'Submitted'");
            db.execSQL("ALTER TABLE complaints ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0");
            db.execSQL("UPDATE complaints SET created_at = " + System.currentTimeMillis() + " WHERE created_at = 0");
        }
        if (oldVersion < 3) {
            createComplaintEventsTable(db);
            db.execSQL("INSERT INTO complaint_events (complaint_id, old_status, new_status, actor, note, event_at) " +
                    "SELECT id, '', status, 'System', 'History initialized during app update', created_at FROM complaints");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE complaints ADD COLUMN latitude REAL");
            db.execSQL("ALTER TABLE complaints ADD COLUMN longitude REAL");
            db.execSQL("ALTER TABLE complaints ADD COLUMN evidence_path TEXT");
        }
    }

    private void createComplaintEventsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS complaint_events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, complaint_id INTEGER NOT NULL, " +
                "old_status TEXT, new_status TEXT NOT NULL, actor TEXT NOT NULL, " +
                "note TEXT NOT NULL DEFAULT '', event_at INTEGER NOT NULL)");
    }

    public long addComplaint(String title, String category, String department,
                             String district, String description, boolean anonymous,
                             Double latitude, Double longitude, String evidencePath) {
        ContentValues values = new ContentValues();
        values.put("title", title.trim()); values.put("category", category);
        values.put("department", department.trim()); values.put("district", district.trim());
        values.put("description", description.trim()); values.put("anonymous", anonymous ? 1 : 0);
        long now = System.currentTimeMillis();
        values.put("status", "Submitted"); values.put("created_at", now);
        if (latitude != null) values.put("latitude", latitude);
        if (longitude != null) values.put("longitude", longitude);
        if (evidencePath != null && !evidencePath.trim().isEmpty()) values.put("evidence_path", evidencePath);
        SQLiteDatabase writable = getWritableDatabase();
        long id = writable.insert("complaints", null, values);
        if (id > 0) {
            ContentValues event = new ContentValues();
            event.put("complaint_id", id); event.put("old_status", "");
            event.put("new_status", "Submitted"); event.put("actor", "Citizen");
            event.put("note", "Complaint registered"); event.put("event_at", now);
            writable.insert("complaint_events", null, event);
        }
        return id;
    }

    public Cursor getAllComplaints() {
        return getReadableDatabase().rawQuery(
                "SELECT id, title, category, district, status, created_at FROM complaints ORDER BY id DESC", null);
    }

    public Cursor getComplaintById(int id) {
        return getReadableDatabase().query("complaints",
                new String[]{"id", "title", "category", "department", "district", "description", "anonymous", "status", "created_at", "latitude", "longitude", "evidence_path"},
                "id = ?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public Cursor getComplaintDetailsForAdmin() {
        return getReadableDatabase().rawQuery("SELECT id, title, category, district, status, created_at FROM complaints ORDER BY id DESC", null);
    }

    public boolean updateComplaintStatus(int id, String status) {
        if (!"Submitted".equals(status) && !"Under Review".equals(status) &&
                !"In Progress".equals(status) && !"Resolved".equals(status) && !"Rejected".equals(status)) return false;
        SQLiteDatabase writable = getWritableDatabase();
        String oldStatus = "";
        try (Cursor c = writable.query("complaints", new String[]{"status"}, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (!c.moveToFirst()) return false;
            oldStatus = c.getString(0);
        }
        if (status.equals(oldStatus)) return true;
        ContentValues values = new ContentValues(); values.put("status", status);
        boolean updated = writable.update("complaints", values, "id = ?", new String[]{String.valueOf(id)}) > 0;
        if (updated) {
            ContentValues event = new ContentValues();
            event.put("complaint_id", id); event.put("old_status", oldStatus);
            event.put("new_status", status);
            event.put("actor", "Staff (local demo)");
            event.put("note", "Status changed from " + oldStatus + " to " + status);
            event.put("event_at", System.currentTimeMillis());
            writable.insert("complaint_events", null, event);
        }
        return updated;
    }

    public Cursor getComplaintEvents(int complaintId) {
        return getReadableDatabase().query("complaint_events",
                new String[]{"old_status", "new_status", "actor", "note", "event_at"},
                "complaint_id = ?", new String[]{String.valueOf(complaintId)},
                null, null, "event_at DESC");
    }

    public int getComplaintCountForStatus(String status) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM complaints WHERE status = ?", new String[]{status})) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public int getComplaintCount() {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM complaints", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    // Kept for legacy practical screens. Do not use this local table for real credentials.
    public boolean registerUser(String name, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim()); values.put("email", email.trim().toLowerCase());
        values.put("password", password);
        return getWritableDatabase().insert("users", null, values) != -1;
    }

    public boolean userExists(String email) {
        try (Cursor cursor = getReadableDatabase().query("users", new String[]{"email"},
                "email = ?", new String[]{email.trim().toLowerCase()}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }
}
