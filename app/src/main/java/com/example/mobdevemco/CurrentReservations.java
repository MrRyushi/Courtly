package com.example.mobdevemco;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CurrentReservations extends AppCompatActivity {
    private List<ReservationData> currentReservationData = new ArrayList<>();
    private CurrentReservationsAdapter reservationAdapter;
    private ActivityResultLauncher<Intent> myActivityResultLauncher;
    private DatabaseReference mDatabase;
    String user_uid;

    private TextView accountName;
    private TextView memberSince;

    private LinearLayout bookLinear;

    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_current_reservations);

        dbHelper = new SQLiteHelper(this);
        user_uid = getIntent().getStringExtra("user_uid");
        mDatabase = FirebaseDatabase.getInstance().getReference("reservations");
        bookLinear = findViewById(R.id.bookLinear);
//        if (currentReservationData == null || currentReservationData.isEmpty()) {
//            bookLinear.setVisibility(View.VISIBLE);
//        } else {
//            bookLinear.setVisibility(View.GONE);
//        }

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

        // Fetch user name asynchronously and update UI with the callback
        accountName = findViewById(R.id.accountName);
        fetchUserName(user_uid, new FetchUserNameCallback() {
            @Override
            public void onUserNameFetched(String fullName) {
                // Set the fetched name in the TextView
                if (fullName != null && !fullName.isEmpty()) {
                    accountName.setText(fullName);
                } else {
                    accountName.setText("User name not available");
                }
            }
        });

        // Fetch member since asynchronously and update UI with the callback
        memberSince = findViewById(R.id.memberSince);
        fetchMemberSince(user_uid, new FetchMemberSinceCallback() {
            @Override
            public void onMemberSinceFetched(String memberSinceValue) {
                // Set the fetched member since in the TextView
                if (memberSinceValue != null && !memberSinceValue.isEmpty()) {
                    memberSince.setText("Member since: " + memberSinceValue);
                } else {
                    memberSince.setText("Not yet a member!");
                }
            }
        });

        if (currentReservationData.isEmpty()) {
            bookLinear.setVisibility(View.VISIBLE);  // Show the booking option if no reservations
        } else {
            bookLinear.setVisibility(View.GONE);  // Hide the booking option if there are reservations
        }

        // Check internet connectivity and fetch data
        if (NetworkUtils.isInternetAvailable(this)) {
            // Fetch reservations for the current user from FIREBASE
            fetchReservations(user_uid);
        } else {
            // Fetch user details and reservations from SQLite
            fetchReservationsFromSQLite(user_uid);
        }

        // Check internet connectivity and fetch user details
        if (NetworkUtils.isInternetAvailable(this)) {
            // Fetch user details from Firebase
            fetchUserName(user_uid, new FetchUserNameCallback() {
                @Override
                public void onUserNameFetched(String fullName) {
                    accountName.setText(fullName != null ? fullName : "User name not available");
                }
            });

            fetchMemberSince(user_uid, new FetchMemberSinceCallback() {
                @Override
                public void onMemberSinceFetched(String memberSinceValue) {
                    memberSince.setText(memberSinceValue != null ? "Member since: " + memberSinceValue : "Not yet a member!");
                }
            });
        } else {
            // Fetch user details from SQLite
            fetchUserDetailsFromSQLite(user_uid);
        }
    }

    // Add this method to fetch user details from SQLite
    private void fetchUserDetailsFromSQLite(String userId) {
        Cursor cursor = dbHelper.getUserDetails(userId); // Assume this method returns a cursor with user details

        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            String memberSinceDate = cursor.getString(cursor.getColumnIndexOrThrow("memberSince"));

            // Update the UI with fetched data
            accountName.setText(username != null ? username : "User name not available");
            memberSince.setText(memberSinceDate != null ? "Member since: " + memberSinceDate : "Not yet a member!");

            cursor.close(); // Close the cursor to release resources
        } else {
            accountName.setText("User name not available");
            memberSince.setText("Not yet a member!");
        }
    }

    public interface FetchMemberSinceCallback {
        void onMemberSinceFetched(String memberSinceValue);
    }

    private void fetchMemberSince(String userId, FetchMemberSinceCallback callback) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract the member since from the user node
                    String memberSinceValue = dataSnapshot.child("memberSince").getValue(String.class);
                    // Invoke the callback with the fetched member since
                    callback.onMemberSinceFetched(memberSinceValue);
                } else {
                    callback.onMemberSinceFetched(null); // In case the user doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CurrentReservations.this, "Error fetching member since.", Toast.LENGTH_SHORT).show();
                callback.onMemberSinceFetched(null); // Handle the error case
            }
        });
    }

    public interface FetchUserNameCallback {
        void onUserNameFetched(String fullName);
    }

    private void fetchUserName(String userId, FetchUserNameCallback callback) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract the full name from the user node
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    // Invoke the callback with the fetched name
                    callback.onUserNameFetched(fullName);
                } else {
                    callback.onUserNameFetched(null); // In case the user doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CurrentReservations.this, "Error fetching user name.", Toast.LENGTH_SHORT).show();
                callback.onUserNameFetched(null); // Handle the error case
            }
        });
    }

    private void fetchReservations(String userId) {
        mDatabase.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentReservationData.clear();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
                Date now = new Date(); // Current date and time

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // Extract general reservation fields
                        String id = snapshot.getKey(); // Firebase unique key
                        String courtName = snapshot.child("courtName").getValue(String.class);
                        String reservationDate = snapshot.child("date").getValue(String.class); // Format: dd/MM/yyyy
                        String reservationDateTime = snapshot.child("reservationDateTime").getValue(String.class);

                        // Extract and filter time slots
                        List<String> upcomingTimeSlots = new ArrayList<>();
                        for (DataSnapshot timeSlotSnapshot : snapshot.child("timeSlots").getChildren()) {
                            String timeSlot = timeSlotSnapshot.getValue(String.class); // Format: h:mm - h:mm a
                            if (timeSlot != null && reservationDate != null) {
                                // Extract the start time from the time slot
                                String[] timeRange = timeSlot.split(" - ");
                                if (timeRange.length > 0) {
                                    String startDateTime = reservationDate + " " + timeRange[0];
                                    Date slotStartDate = dateFormatter.parse(startDateTime);

                                    if (slotStartDate != null && !slotStartDate.before(now)) {
                                        // Only add future time slots
                                        upcomingTimeSlots.add(timeSlot);
                                    }
                                }
                            }
                        }

                        // Only add reservation if it has at least one upcoming time slot
                        if (!upcomingTimeSlots.isEmpty()) {
                            ReservationData reservation = new ReservationData(
                                    id,
                                    courtName,
                                    reservationDate,
                                    reservationDateTime,
                                    upcomingTimeSlots,
                                    userId
                            );
                            currentReservationData.add(reservation);
                        }
                    } catch (Exception e) {
                        Log.e("CurrentReservations", "Error parsing reservation data: " + e.getMessage());
                    }
                }

                currentReservationData.sort((r1, r2) -> {
                    try {
                        Date date1 = dateFormatter.parse(r1.getReservationDate() + " " + r1.getReservationTimeSlot().get(0).split(" - ")[0]);
                        Date date2 = dateFormatter.parse(r2.getReservationDate() + " " + r2.getReservationTimeSlot().get(0).split(" - ")[0]);

                        if (date1 == null) return 1; // Push null `date1` to the end
                        if (date2 == null) return -1; // Push null `date2` to the end

                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0; // Keep original order if parsing fails
                    }
                });

                // Notify adapter of data change
                reservationAdapter.notifyDataSetChanged();

                // Update visibility of bookLinear after data is fetched
                if (currentReservationData.isEmpty()) {
                    bookLinear.setVisibility(View.VISIBLE);  // Show booking option if no reservations
                } else {
                    bookLinear.setVisibility(View.GONE);  // Hide booking option if there are reservations
                }
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
        intent.putExtra("fullName", accountName.getText().toString());
        intent.putExtra("memberSince", memberSince.getText().toString());
        intent.putExtra("user_uid", user_uid);
        myActivityResultLauncher.launch(intent);
    }

    private void fetchReservationsFromSQLite(String userId) {
        currentReservationData.clear(); // Clear the current list to avoid duplicate entries

        Cursor cursor = dbHelper.getReservationsByUserId(userId); // Assume this method fetches reservations for the given userId
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        Date now = new Date(); // Current date and time

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                    String courtName = cursor.getString(cursor.getColumnIndexOrThrow("courtName"));
                    String reservationDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    String reservationDateTime = cursor.getString(cursor.getColumnIndexOrThrow("reservationDateTime"));
                    String timeSlots = cursor.getString(cursor.getColumnIndexOrThrow("timeSlots"));
                    String userIdFromDb = cursor.getString(cursor.getColumnIndexOrThrow("userId"));

                    // Split the timeSlots string into a List
                    List<String> timeSlotsList = new ArrayList<>();
                    if (timeSlots != null) {
                        String[] slotsArray = timeSlots.split(",\\s*"); // Split by commas and trim whitespace
                        for (String slot : slotsArray) {
                            // Parse to check if the slot is upcoming
                            String[] timeRange = slot.split(" - ");
                            if (timeRange.length > 0 && reservationDate != null) {
                                String startDateTime = reservationDate + " " + timeRange[0];
                                Date slotStartDate = dateFormatter.parse(startDateTime);

                                if (slotStartDate != null && !slotStartDate.before(now)) {
                                    timeSlotsList.add(slot);
                                }
                            }
                        }
                    }

                    // Only add the reservation if there are upcoming time slots
                    if (!timeSlotsList.isEmpty()) {
                        ReservationData reservation = new ReservationData(
                                id,
                                courtName,
                                reservationDate,
                                reservationDateTime,
                                timeSlotsList,
                                userIdFromDb
                        );
                        currentReservationData.add(reservation);
                    }
                } catch (Exception e) {
                    Log.e("CurrentReservations", "Error parsing reservation data from SQLite: " + e.getMessage());
                }
            } while (cursor.moveToNext());

            cursor.close(); // Close the cursor to release resources
        }

        // Sort reservations by date and time
        currentReservationData.sort((r1, r2) -> {
            try {
                Date date1 = dateFormatter.parse(r1.getReservationDate() + " " + r1.getReservationTimeSlot().get(0).split(" - ")[0]);
                Date date2 = dateFormatter.parse(r2.getReservationDate() + " " + r2.getReservationTimeSlot().get(0).split(" - ")[0]);

                if (date1 == null) return 1; // Push null `date1` to the end
                if (date2 == null) return -1; // Push null `date2` to the end

                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0; // Keep original order if parsing fails
            }
        });

        // Notify adapter of data change
        reservationAdapter.notifyDataSetChanged();

        // Update visibility of bookLinear
        if (currentReservationData.isEmpty()) {
            bookLinear.setVisibility(View.VISIBLE);  // Show booking option if no reservations
        } else {
            bookLinear.setVisibility(View.GONE);  // Hide booking option if there are reservations
        }
    }



}