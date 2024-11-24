package com.example.mobdevemco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentReservations extends AppCompatActivity {
    private List<ReservationData> currentReservationData = new ArrayList<>();
    private CurrentReservationsAdapter reservationAdapter;
    private ActivityResultLauncher<Intent> myActivityResultLauncher;
    private DatabaseReference mDatabase;
    String user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        user_uid = getIntent().getStringExtra("user_uid");
        mDatabase = FirebaseDatabase.getInstance().getReference("reservations");

        // Set layout based on whether the reservations list is empty
        setContentView(R.layout.activity_current_reservations);

        // Apply window insets for edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Register an activity result launcher
        myActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    public void onActivityResult(ActivityResult result) {
                        // Handle activity result here (if necessary)
                    }
                });

        // Initialize RecyclerView only if reservations are available
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reservationAdapter = new CurrentReservationsAdapter(currentReservationData, CurrentReservations.this);
        recyclerView.setAdapter(reservationAdapter);

        // Fetch reservations for the current user
        fetchReservations(user_uid);
    }


    private void fetchReservations(String userId) {
        mDatabase.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentReservationData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extract general reservation fields
                    String id = snapshot.getKey(); // Firebase unique key
                    String courtName = snapshot.child("courtName").getValue(String.class);
                    String reservationDate = snapshot.child("date").getValue(String.class);
                    String reservationDateTime = snapshot.child("reservationDateTime").getValue(String.class);

                    // Extract nested time slot field
                    List<String> reservationTimeSlots = new ArrayList<>();
                    for (DataSnapshot timeSlotSnapshot : snapshot.child("timeSlots").getChildren()) {
                        String timeSlot = timeSlotSnapshot.getValue(String.class);
                        reservationTimeSlots.add(timeSlot);
                    }

                    // Construct a ReservationData object
                    ReservationData reservation = new ReservationData(
                            id,
                            courtName,
                            reservationDate,
                            reservationDateTime,
                            reservationTimeSlots,
                            userId
                    );

                    // Add reservation to the list
                    currentReservationData.add(reservation);
                }
                // Notify adapter of data change
                reservationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CurrentReservations.this, "Error fetching reservations.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void handleBackButtonClick(View view) {
        finish();
    }

    public void handleHistoryButtonClick(View view) {
        Intent intent = new Intent(CurrentReservations.this, ReservationsHistory.class);
        myActivityResultLauncher.launch(intent);
    }
}