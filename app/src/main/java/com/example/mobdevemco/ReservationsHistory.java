package com.example.mobdevemco;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationsHistory extends AppCompatActivity {

    private List<ReservationData> reservationData = new ArrayList<>();
    private ReservationAdapter reservationAdapter;

    private TextView accountName;
    private TextView memberSince;
    private DatabaseReference mDatabase;
    private String user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        user_uid = getIntent().getStringExtra("user_uid");
        mDatabase = FirebaseDatabase.getInstance().getReference("reservations");

        if (reservationData == null) {
            setContentView(R.layout.activity_empty_reservations_history);
            String fullNameIntent = getIntent().getStringExtra("fullName");
            String memberSinceIntent = getIntent().getStringExtra("memberSince");

            accountName = findViewById(R.id.accountName);
            accountName.setText(fullNameIntent);
            memberSince = findViewById(R.id.memberSince);
            memberSince.setText(memberSinceIntent);
            return;
        }

        setContentView(R.layout.activity_reservations_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String fullNameIntent = getIntent().getStringExtra("fullName");
        String memberSinceIntent = getIntent().getStringExtra("memberSince");

        accountName = findViewById(R.id.accountName);
        accountName.setText(fullNameIntent);
        memberSince = findViewById(R.id.memberSince);
        memberSince.setText(memberSinceIntent);

        reservationAdapter = new ReservationAdapter(reservationData, ReservationsHistory.this);
        recyclerView.setAdapter(reservationAdapter);

        // Fetch reservations for the current user
        if (isConnectedToInternet()) {
            fetchReservationsFromFirebase(user_uid);
        } else {
            fetchReservationsFromSQLite(user_uid);
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void fetchReservationsFromFirebase(String userId) {
        mDatabase.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                populateReservations(dataSnapshot, true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReservationsHistory", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void fetchReservationsFromSQLite(String userId) {
        reservationData.clear();

        SQLiteHelper dbHelper = new SQLiteHelper(getApplicationContext());  // Use SQLiteHelper to manage DB
        Cursor cursor = dbHelper.getReservationsByUserId(userId);  // Fetch reservations for the user

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        Date now = new Date();

        while (cursor.moveToNext()) {
            try {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String courtName = cursor.getString(cursor.getColumnIndexOrThrow("courtName"));
                String reservationDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String reservationDateTime = cursor.getString(cursor.getColumnIndexOrThrow("reservationDateTime"));
                String timeSlotsString = cursor.getString(cursor.getColumnIndexOrThrow("timeSlots"));

                // Split multiple time slots
                String[] timeSlotsArray = timeSlotsString != null ? timeSlotsString.split(",") : new String[0];
                List<String> reservationTimeSlots = new ArrayList<>();
                boolean isReservationPast = false;

                for (String timeSlot : timeSlotsArray) {
                    timeSlot = timeSlot.trim();
                    String startDateTime = reservationDate + " " + timeSlot.split(" - ")[0];
                    Date slotStartDate = dateFormatter.parse(startDateTime);

                    if (slotStartDate != null && slotStartDate.before(now)) {
                        isReservationPast = true;
                        reservationTimeSlots.add(timeSlot);
                    }
                }

                // Add to list if at least one time slot is in the past
                if (isReservationPast) {
                    ReservationData reservation = new ReservationData(
                            id,
                            courtName,
                            reservationDate,
                            reservationDateTime,
                            reservationTimeSlots,
                            userId
                    );
                    reservationData.add(reservation);
                }
            } catch (ParseException e) {
                Log.e("ReservationsHistory", "Date Parsing Error: " + e.getMessage());
            }
        }

        cursor.close();  // Close cursor when done
        dbHelper.close();  // Close the database helper

        reservationAdapter.notifyDataSetChanged();  // Notify the adapter about data changes
        Log.d("ReservationsHistory", "Loaded from SQLite. Number of Reservations: " + reservationData.size());
    }



    private void populateReservations(DataSnapshot dataSnapshot, boolean isFromFirebase) {
        reservationData.clear();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        Date now = new Date();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            try {
                String id = snapshot.getKey();
                String courtName = snapshot.child("courtName").getValue(String.class);
                String reservationDate = snapshot.child("date").getValue(String.class);
                String reservationDateTime = snapshot.child("reservationDateTime").getValue(String.class);
                List<String> reservationTimeSlots = new ArrayList<>();
                boolean isReservationPast = false;

                for (DataSnapshot timeSlotSnapshot : snapshot.child("timeSlots").getChildren()) {
                    String timeSlot = timeSlotSnapshot.getValue(String.class);
                    if (timeSlot != null && reservationDate != null) {
                        String startDateTime = reservationDate + " " + timeSlot.split(" - ")[0];
                        Date slotStartDate = dateFormatter.parse(startDateTime);

                        if (slotStartDate != null && slotStartDate.before(now)) {
                            isReservationPast = true;
                            reservationTimeSlots.add(timeSlot);
                        }
                    }
                }

                if (isReservationPast) {
                    ReservationData reservation = new ReservationData(
                            id,
                            courtName,
                            reservationDate,
                            reservationDateTime,
                            reservationTimeSlots,
                            user_uid
                    );
                    reservationData.add(reservation);
                }
            } catch (ParseException e) {
                Log.e("ReservationsHistory", "Date Parsing Error: " + e.getMessage());
            }
        }

        reservationAdapter.notifyDataSetChanged();
        String source = isFromFirebase ? "Firebase" : "SQLite";
        Log.d("ReservationsHistory", "Loaded from " + source + ". Number of Reservations: " + reservationData.size());
    }

    public void handleBackButtonClick(View view) {
        finish();
    }
}
