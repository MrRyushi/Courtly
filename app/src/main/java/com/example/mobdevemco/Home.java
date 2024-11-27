package com.example.mobdevemco;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class Home extends AppCompatActivity {

    private ActivityResultLauncher<Intent> myActivityResultLauncher;
    private CourtData[] courtData;
    private CourtAdapter courtAdapter;
    private ImageView logoutBtn;
    private DatabaseReference mDatabase;
    private Object recentReservation;
    private boolean hasSeenFragment = false;
    private String user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                String membershipApplication = data.getStringExtra("membershipApplication");
                                if ("success".equals(membershipApplication)) {
                                    SPHelper.setBoolean(Home.this, "isUserAMember", true);
                                }
                            }
                        }
                    }
                });

        // Initialize Firebase and user UID
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user_uid = SPHelper.getString(this, "user_uid", null);

        courtData = new CourtData[]{
                new CourtData("Court Alpha", "First Court to the left", R.drawable.court),
                new CourtData("Court Bravo", "Second Court to the left", R.drawable.court),
                new CourtData("Court Charlie", "Third Court to the left", R.drawable.court),
                new CourtData("Court Delta", "Fourth Court to the left", R.drawable.court),
                new CourtData("Court Echo", "First Court to the right", R.drawable.court),
                new CourtData("Court Foxtrot", "Second Court to the right", R.drawable.court),
                new CourtData("Court Golf", "Third Court to the right", R.drawable.court),
                new CourtData("Court Hotel", "Fourth Court to the right", R.drawable.court),
        };

        courtAdapter = new CourtAdapter(courtData, Home.this, myActivityResultLauncher);
        recyclerView.setAdapter(courtAdapter);

        getLatestReservation(() -> {
            if (!SPHelper.getBoolean(this, "hasSeenFragment", false) && recentReservation != null) {
                showWelcomeFragment();
                SPHelper.setBoolean(this, "hasSeenFragment", true);
            }
        });

        // Initialize Firebase and SQLite
        SQLiteHelper dbHelper = new SQLiteHelper(this);

        // Check internet connectivity and fetch data
        if (NetworkUtils.isInternetAvailable(this)) {
            fetchAndStoreData(dbHelper);
        }

    }

    private void fetchAndStoreData(SQLiteHelper dbHelper) {
        // Fetch Users
        mDatabase.child("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot usersSnapshot = task.getResult();
                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    String id = userSnapshot.getKey();
                    String dateRequested = userSnapshot.child("dateRequested").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    String fullName = userSnapshot.child("fullName").getValue(String.class);
                    boolean member = Boolean.TRUE.equals(userSnapshot.child("member").getValue(Boolean.class));
                    String memberSince = userSnapshot.child("memberSince").getValue(String.class);
                    String membershipStatus = userSnapshot.child("membershipStatus").getValue(String.class);
                    String recentReservation = userSnapshot.child("recentReservation").getValue(String.class);
                    Integer totalReservations = userSnapshot.child("totalReservations").getValue(Integer.class);
                    if (totalReservations == null) totalReservations = 0;

                    // Check if the user already exists in the database
                    User existingUser = dbHelper.getUserById(id);
                    if (existingUser != null) {
                        // Update the user if there are changes
                        Log.d("FetchUsers", "User already exists in SQL: " + id);
                        if (!recentReservation.equals(existingUser.getRecentReservation()) ||
                                totalReservations != existingUser.getTotalReservations() ||
                                !membershipStatus.equals(existingUser.getMembershipStatus()) ||
                                member != (existingUser.getMember()) ||
                                !memberSince.equals(existingUser.getMemberSince()) ||
                                !dateRequested.equals(existingUser.getDateRequested())
                        ) {
                            dbHelper.updateUser(id, dateRequested, email, fullName, member, memberSince, membershipStatus, recentReservation, totalReservations);
                            Log.d("FetchUsers", "User updated in SQL: " + id);
                        }
                    } else {
                        // Insert new user
                        dbHelper.insertUser(id, dateRequested, email, fullName, member, memberSince, membershipStatus, recentReservation, totalReservations);
                        Log.d("FetchUsers", "User inserted to SQL: " + id);
                    }
                }
            }
        });

        // Fetch Reservations
        mDatabase.child("reservations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot reservationsSnapshot = task.getResult();

                // Step 1: Collect all Firebase reservation IDs
                Set<String> firebaseReservationIds = new HashSet<>();
                List<ReservationData> firebaseReservations = new ArrayList<>();

                for (DataSnapshot reservationSnapshot : reservationsSnapshot.getChildren()) {
                    String id = reservationSnapshot.getKey();
                    String courtName = reservationSnapshot.child("courtName").getValue(String.class);
                    String date = reservationSnapshot.child("date").getValue(String.class);
                    String reservationDateTime = reservationSnapshot.child("reservationDateTime").getValue(String.class);
                    String userId = reservationSnapshot.child("userId").getValue(String.class);

                    // Build and normalize timeSlots
                    List<String> timeSlotsList = new ArrayList<>();
                    for (DataSnapshot timeSlotSnapshot : reservationSnapshot.child("timeSlots").getChildren()) {
                        String timeSlot = timeSlotSnapshot.getValue(String.class);
                        if (timeSlot != null) {
                            timeSlotsList.add(timeSlot.trim());
                        }
                    }
                    Collections.sort(timeSlotsList); // Sort for consistent comparison

                    firebaseReservationIds.add(id);
                    firebaseReservations.add(new ReservationData(id, courtName, date, reservationDateTime, timeSlotsList, userId));
                }

                // Step 2: Fetch all reservations from SQLite
                List<ReservationData> sqliteReservations = dbHelper.getAllReservations();
                Set<String> sqliteReservationIds = new HashSet<>();
                for (ReservationData reservation : sqliteReservations) {
                    sqliteReservationIds.add(reservation.getId());
                }

                // Step 3: Sync Firebase data with SQLite
                for (ReservationData firebaseReservation : firebaseReservations) {
                    ReservationData existingReservation = dbHelper.getReservationById(firebaseReservation.getId());

                    if (existingReservation != null) {
                        // Compare fields to check if an update is needed
                        boolean needsUpdate =
                                !firebaseReservation.getCourtName().equals(existingReservation.getCourtName()) ||
                                        !firebaseReservation.getReservationDate().equals(existingReservation.getReservationDate()) ||
                                        !firebaseReservation.getReservationDateTime().equals(existingReservation.getReservationDateTime()) ||
                                        !firebaseReservation.getReservationTimeSlot().equals(existingReservation.getReservationTimeSlot());

                        if (needsUpdate) {
                            dbHelper.updateReservation(
                                    firebaseReservation.getId(),
                                    firebaseReservation.getCourtName(),
                                    firebaseReservation.getReservationDate(),
                                    firebaseReservation.getReservationDateTime(),
                                    TextUtils.join(", ", firebaseReservation.getReservationTimeSlot()),
                                    firebaseReservation.getUserId()
                            );
                            Log.d("FetchReservations", "Reservation updated in SQL: " + firebaseReservation.getId());
                        }
                    } else {
                        // Insert new reservation
                        dbHelper.insertReservation(
                                firebaseReservation.getId(),
                                firebaseReservation.getCourtName(),
                                firebaseReservation.getReservationDate(),
                                firebaseReservation.getReservationDateTime(),
                                TextUtils.join(", ", firebaseReservation.getReservationTimeSlot()),
                                firebaseReservation.getUserId()
                        );
                        Log.d("FetchReservations", "Reservation inserted to SQL: " + firebaseReservation.getId());
                    }
                }

                // Step 4: Remove SQLite records not present in Firebase
                for (String sqliteReservationId : sqliteReservationIds) {
                    if (!firebaseReservationIds.contains(sqliteReservationId)) {
                        dbHelper.deleteReservation(sqliteReservationId);
                        Log.d("FetchReservations", "Reservation deleted from SQL: " + sqliteReservationId);
                    }
                }
            } else {
                Log.e("FetchReservations", "Failed to fetch reservations: " + task.getException());
            }
        });



    }

    private void showWelcomeFragment() {
        RecommendFragment fragment = new RecommendFragment();

        if (recentReservation instanceof Map) {
            Map<String, Object> recentReservationMap = (Map<String, Object>) recentReservation;
            String date = (String) recentReservationMap.get("date");
            List<String> timeSlots = (List<String>) recentReservationMap.get("timeSlots");
            String courtName = (String) recentReservationMap.get("courtName");

            Bundle args = new Bundle();
            args.putString("reservationDate", date);
            args.putString("courtName", courtName);
            args.putString("timeSlots", TextUtils.join(",", timeSlots));

            fragment.setArguments(args);
        } else {
            Log.e("Home", "recentReservation is not a valid Map object.");
        }

        fragment.show(getSupportFragmentManager(), "RecommendFragment");
    }

    public void handleReservationsBtnClick(View view) {
        Intent intent = new Intent(Home.this, CurrentReservations.class);
        intent.putExtra("user_uid", user_uid);
        myActivityResultLauncher.launch(intent);
    }

    public void handleMembershipButtonClick(View v) {
        // Check internet connectivity and fetch data
        if (NetworkUtils.isInternetAvailable(this)) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase.child("users").child(userId).child("membershipStatus").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String membershipStatus = task.getResult().getValue(String.class);

                    if (membershipStatus != null) {
                        handleMembershipStatus(membershipStatus);
                    }
                } else {
                    Log.e("Home", "Failed to fetch membership status from Firebase");
                }
            });
        } else {
            // Fetch data from SQLite
            SQLiteHelper dbHelper = new SQLiteHelper(this);
            User user = dbHelper.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid());

            if (user != null && user.getMembershipStatus() != null) {
                handleMembershipStatus(user.getMembershipStatus());
            } else {
                Log.e("Home", "Failed to fetch membership status from SQLite");
            }
        }
    }

    private void handleMembershipStatus(String membershipStatus) {
        Intent intent = null;

        switch (membershipStatus) {
            case "No application":
                intent = new Intent(this, MembershipApplication.class);
                break;
            case "Requested":
                intent = new Intent(this, MembershipPending.class);
                break;
            case "Approved":
                if (!SPHelper.getBoolean(this, "hasSeenMembershipSuccess", false)) {
                    intent = new Intent(this, MembershipSuccess.class);
                    SPHelper.setBoolean(this, "hasSeenMembershipSuccess", true);
                } else {
                    intent = new Intent(this, MembershipPage.class);
                }
                break;
        }

        if (intent != null) {
            myActivityResultLauncher.launch(intent);
        }
    }


    public void handleLogoutBtnClick(View v) {
        SPHelper.clear(this); // Clear all saved preferences
        FirebaseAuth.getInstance().signOut(); // Sign out of Firebase
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void getLatestReservation(Runnable onFetchComplete) {
        mDatabase.child("reservations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> reservations = (Map<String, Object>) task.getResult().getValue();

                if (reservations != null) {
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));

                    String latestReservationKey = null;
                    Date latestDateTime = null;

                    try {
                        for (Map.Entry<String, Object> entry : reservations.entrySet()) {
                            Map<String, Object> reservation = (Map<String, Object>) entry.getValue();
                            if (isValidReservation(reservation)) {
                                String dateTimeString = (String) reservation.get("reservationDateTime");
                                Date reservationDateTime = dateTimeFormat.parse(dateTimeString);

                                if (latestDateTime == null || reservationDateTime.after(latestDateTime)) {
                                    latestDateTime = reservationDateTime;
                                    latestReservationKey = entry.getKey();
                                    recentReservation = entry.getValue();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Home", "Error parsing dates", e);
                    }

                    if (latestReservationKey != null) {
                        Log.i("Home", "Most recent reservation: " + latestReservationKey);
                    }
                }
            } else {
                Log.e("Home", "Failed to fetch reservations", task.getException());
            }

            if (onFetchComplete != null) onFetchComplete.run();
        });
    }

    private boolean isValidReservation(Map<String, Object> reservation) {
        return reservation != null && user_uid.equals(reservation.get("userId"));
    }
}
