package com.example.smartanticurruptionbeuroplatform;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

    public class StudentDatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "StudentRecords.db";
        private static final int DATABASE_VERSION = 1;

        public StudentDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE students (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "course TEXT NOT NULL, " +
                            "marks REAL NOT NULL)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            // Preserve existing records during upgrades.
            // Add explicit migrations here if the schema changes.
        }

        public boolean addStudent(String name, String course, double marks) {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("course", course);
            values.put("marks", marks);

            long result = db.insert("students", null, values);
            return result != -1;
        }

        public Cursor getAllStudents() {
            SQLiteDatabase db = getReadableDatabase();

            return db.query(
                    "students",
                    new String[]{"id", "name", "course", "marks"},
                    null, null, null, null,
                    "id DESC"
            );
        }

        public boolean updateStudent(int id, String name,
                                     String course, double marks) {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("course", course);
            values.put("marks", marks);

            int rows = db.update(
                    "students",
                    values,
                    "id = ?",
                    new String[]{String.valueOf(id)}
            );

            return rows > 0;
        }

        public boolean deleteStudent(int id) {
            SQLiteDatabase db = getWritableDatabase();

            int rows = db.delete(
                    "students",
                    "id = ?",
                    new String[]{String.valueOf(id)}
            );

            return rows > 0;
        }
    }

