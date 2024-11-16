package com.example.mobdevemco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Home extends AppCompatActivity {

    public String courtName;
    private ActivityResultLauncher<Intent> myActivityResultLauncher;
    private CourtData[] courtData;
    private CourtAdapter courtAdapter;
    public boolean isUserAMember = false;
    private DatabaseReference mDatabase;
    private Object reservations;
    String user_uid;


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
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            if(data != null){
                                String membershipApplication = data.getStringExtra("membershipApplication");
                               if(membershipApplication != null && membershipApplication.equals("success")){
                                   isUserAMember = true;
                               }
                            }
                        }
                    }
                });

        courtData = new CourtData[]{
                new CourtData("Court Alpha", "First Court to the left" ,R.drawable.court),
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user_uid = getIntent().getStringExtra("user_uid");
        getLatestReservation(user_uid);
    }

    public void handleReservationsBtnClick(View view) {
        Intent intent = new Intent(Home.this, CurrentReservations.class);
        myActivityResultLauncher.launch(intent);
    }

    public void handleMembershipButtonClick(View v){
        Intent intent = isUserAMember ? new Intent(Home.this, MembershipPending.class) : new Intent(Home.this, MembershipApplication.class);
        myActivityResultLauncher.launch(intent);
    }

    public void getLatestReservation(String userId) {
        // Retrieve data from Firebase
        mDatabase.child("reservations").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                return;
            }

            Map<String, Object> reservations = (Map<String, Object>) task.getResult().getValue();
            if (reservations == null) {
                Log.i("firebase", "No reservations found.");
                return;
            }

            String latestReservationKey = null;
            Date latestDateTime = null;

            // Date format expected in reservationDateTime
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));

            try {
                for (Map.Entry<String, Object> entry : reservations.entrySet()) {
                    Map<String, Object> reservation = (Map<String, Object>) entry.getValue();

                    // Check if reservation belongs to the given user
                    if (isValidReservation(reservation, userId)) {
                        // Get reservationDateTime from the reservation
                        String reservationDateTimeString = (String) reservation.get("reservationDateTime");
                        if (reservationDateTimeString != null) {
                            Date reservationDateTime = dateTimeFormat.parse(reservationDateTimeString);

                            // Check if this is the most recent reservation
                            if (latestDateTime == null || (reservationDateTime != null && reservationDateTime.after(latestDateTime))) {
                                latestDateTime = reservationDateTime;
                                latestReservationKey = entry.getKey();
                            }
                        }
                    }
                }

                if (latestReservationKey != null) {
                    Log.i("firebase", "Most recent reservation: " + latestReservationKey + " on " + dateTimeFormat.format(latestDateTime));
                } else {
                    Log.i("firebase", "No matching reservations found.");
                }
            } catch (Exception e) {
                Log.e("firebase", "Error parsing dates", e);
            }
        });
    }


    private boolean isValidReservation(Map<String, Object> reservation, String userId) {
        return reservation != null
                && reservation.containsKey("userId")
                && reservation.containsKey("date")
                && reservation.containsKey("timeSlots")
                && userId.equals(reservation.get("userId"));
    }



}