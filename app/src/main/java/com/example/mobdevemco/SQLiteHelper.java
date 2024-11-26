package com.example.mobdevemco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Courtly";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "Users";
    private static final String TABLE_RESERVATIONS = "Reservations";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS +
                "(id TEXT PRIMARY KEY, dateRequested TEXT, email TEXT, fullName TEXT, member INTEGER, " +
                "memberSince TEXT, membershipStatus TEXT, recentReservation TEXT, totalReservations INTEGER)";

        String createReservationsTable = "CREATE TABLE " + TABLE_RESERVATIONS +
                "(id TEXT PRIMARY KEY, courtName TEXT, date TEXT, reservationDateTime TEXT, " +
                "timeSlots TEXT, userId TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createReservationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        onCreate(db);
    }

    // Insert or update a user
    public void insertUser(String id, String dateRequested, String email, String fullName, boolean member,
                           String memberSince, String membershipStatus, String recentReservation, int totalReservations) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("dateRequested", dateRequested);
        values.put("email", email);
        values.put("fullName", fullName);
        values.put("member", member ? 1 : 0);
        values.put("memberSince", memberSince);
        values.put("membershipStatus", membershipStatus);
        values.put("recentReservation", recentReservation);
        values.put("totalReservations", totalReservations);

        db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Insert or update a reservation
    public void insertReservation(String id, String courtName, String date, String reservationDateTime,
                                  String timeSlots, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("courtName", courtName);
        values.put("date", date);
        values.put("reservationDateTime", reservationDateTime);
        values.put("timeSlots", timeSlots);
        values.put("userId", userId);

        db.insertWithOnConflict(TABLE_RESERVATIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Fetch user data by ID
    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "id = ?", new String[]{userId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow("fullName")), // maps to username
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("member")) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow("membershipStatus")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("totalReservations")),
                    cursor.getString(cursor.getColumnIndexOrThrow("recentReservation")),
                    cursor.getString(cursor.getColumnIndexOrThrow("dateRequested")),
                    cursor.getString(cursor.getColumnIndexOrThrow("memberSince"))
            );
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }




    // Update an existing user
    public void updateUser(String id, String dateRequested, String email, String fullName, boolean member,
                           String memberSince, String membershipStatus, String recentReservation, int totalReservations) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dateRequested", dateRequested);
        values.put("email", email);
        values.put("fullName", fullName);
        values.put("member", member ? 1 : 0);
        values.put("memberSince", memberSince);
        values.put("membershipStatus", membershipStatus);
        values.put("recentReservation", recentReservation);
        values.put("totalReservations", totalReservations);

        db.update(TABLE_USERS, values, "id = ?", new String[]{id});
        db.close();
    }

    // Update an existing reservation
    public void updateReservation(String id, String courtName, String date, String reservationDateTime,
                                  String timeSlots, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("courtName", courtName);
        values.put("date", date);
        values.put("reservationDateTime", reservationDateTime);
        values.put("timeSlots", timeSlots);
        values.put("userId", userId);

        db.update(TABLE_RESERVATIONS, values, "id = ?", new String[]{id});
        db.close();
    }

    // Fetch all reservations
    public List<ReservationData> getAllReservations() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ReservationData> reservations = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RESERVATIONS, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String courtName = cursor.getString(cursor.getColumnIndexOrThrow("courtName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String reservationDateTime = cursor.getString(cursor.getColumnIndexOrThrow("reservationDateTime"));
                String timeSlots = cursor.getString(cursor.getColumnIndexOrThrow("timeSlots"));
                List<String> timeSlotsList = Arrays.asList(timeSlots.split(", "));
                String userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"));

                reservations.add(new ReservationData(id, courtName, date, reservationDateTime, timeSlotsList, userId));
            }
            cursor.close();
        }
        db.close();
        return reservations;
    }

    // Fetch reservation by ID
    public ReservationData getReservationById(String reservationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESERVATIONS, null, "id = ?", new String[]{reservationId}, null, null, null);

        ReservationData reservation = null;
        if (cursor != null && cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String courtName = cursor.getString(cursor.getColumnIndexOrThrow("courtName"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String reservationDateTime = cursor.getString(cursor.getColumnIndexOrThrow("reservationDateTime"));
            String timeSlots = cursor.getString(cursor.getColumnIndexOrThrow("timeSlots"));
            List<String> timeSlotsList = Arrays.asList(timeSlots.split(", "));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"));

            reservation = new ReservationData(id, courtName, date, reservationDateTime, timeSlotsList, userId);
            cursor.close();
        }
        db.close();
        return reservation;
    }

    public void deleteReservation(String reservationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_RESERVATIONS, "id = ?", new String[]{reservationId});
        db.close();

        if (rowsDeleted > 0) {
            Log.d("SQLiteHelper", "Reservation deleted: " + reservationId);
        } else {
            Log.d("SQLiteHelper", "No reservation found to delete with ID: " + reservationId);
        }
    }

    public Cursor getReservationsByUserId(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_RESERVATIONS,
                null, // Select all columns
                "userId = ?",
                new String[]{userId},
                null,
                null,
                "date ASC, timeSlots ASC" // Order by date and time slots
        );
    }

    public Cursor getUserDetails(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                "users",                // Table name
                new String[]{"fullName", "memberSince"}, // Columns to return
                "id = ?",           // WHERE clause
                new String[]{userId},   // WHERE arguments
                null,                   // GROUP BY
                null,                   // HAVING
                null                    // ORDER BY
        );
    }

}
