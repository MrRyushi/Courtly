package com.example.mobdevemco;

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

    private List<ReservationData> reservationData = new ArrayList<>();; // Change to List
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

        if(reservationData == null) {
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
        fetchReservations(user_uid);
    }

    private void fetchReservations(String userId) {
        mDatabase.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reservationData.clear(); // Clear any existing data
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
                Date now = new Date(); // Current date and time

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String id = snapshot.getKey();
                        String courtName = snapshot.child("courtName").getValue(String.class);
                        String reservationDate = snapshot.child("date").getValue(String.class); // Format: dd/MM/yyyy
                        String reservationDateTime = snapshot.child("reservationDateTime").getValue(String.class);
                        List<String> reservationTimeSlots = new ArrayList<>();
                        boolean isReservationPast = false;

                        for (DataSnapshot timeSlotSnapshot : snapshot.child("timeSlots").getChildren()) {
                            String timeSlot = timeSlotSnapshot.getValue(String.class); // Format: h:mm - h:mm a
                            if (timeSlot != null && reservationDate != null) {
                                // Extract the start time from the time slot
                                String[] timeRange = timeSlot.split(" - ");
                                if (timeRange.length > 0) {
                                    // Combine date and start time
                                    String startDateTime = reservationDate + " " + timeRange[0]; // e.g., "24/11/2024 1:00 PM"
                                    Date slotStartDate = dateFormatter.parse(startDateTime);

                                    if (slotStartDate != null) {
                                        if (slotStartDate.before(now)) {
                                            isReservationPast = true; // Mark as past reservation
                                            reservationTimeSlots.add(timeSlot);
                                        }
                                    } else {
                                        Log.e("ReservationsHistory", "Parsed date is null for: " + startDateTime);
                                    }
                                }
                            }
                        }

                        // Add the reservation to the list if any time slot is in the past
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

                // Notify adapter of data change
                reservationAdapter.notifyDataSetChanged();
                Log.d("ReservationsHistory", "Number of Reservations: " + reservationData.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReservationsHistory", "Database Error: " + databaseError.getMessage());
            }
        });
    }


    public void handleBackButtonClick(View view) {
        finish();
    }
}