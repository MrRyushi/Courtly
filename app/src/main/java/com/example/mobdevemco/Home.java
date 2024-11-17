package com.example.mobdevemco;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {

    public String courtName;
    private ActivityResultLauncher<Intent> myActivityResultLauncher;
    private CourtData[] courtData;
    private CourtAdapter courtAdapter;
    public boolean isUserAMember = false;
    private ImageView logoutBtn;

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
    }

    public void handleReservationsBtnClick(View view) {
        Intent intent = new Intent(Home.this, CurrentReservations.class);
        myActivityResultLauncher.launch(intent);
    }

//    public void handleMembershipButtonClick(View v){
//        Intent intent = isUserAMember ? new Intent(Home.this, MembershipPending.class) : new Intent(Home.this, MembershipApplication.class);
//        myActivityResultLauncher.launch(intent);
//    }

    public void handleMembershipButtonClick(View v) {
        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the user's membership status in Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        // Fetch membership status from the database
        userRef.child("membershipStatus").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the membership status from Firebase
                String membershipStatus = task.getResult().getValue(String.class);

                // Get SharedPreferences instance
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Home.this);
                boolean hasSeenMembershipSuccess = prefs.getBoolean("hasSeenMembershipSuccess", false);

                if (membershipStatus != null) {
                    // Initialize the intent to launch based on membership status
                    Intent intent = null;
                    if ("No application".equals(membershipStatus)) {
                        // If no membership application has been made, go to MembershipApplication
                        intent = new Intent(Home.this, MembershipApplication.class);
                    } else if ("Requested".equals(membershipStatus)) {
                        // If the membership request has been sent, go to MembershipPending
                        intent = new Intent(Home.this, MembershipPending.class);
                    } else if ("Approved".equals(membershipStatus) && !hasSeenMembershipSuccess) {
                        // If the membership is completed and this is the first time the user opens the screen, show MembershipSuccess
                        intent = new Intent(Home.this, MembershipSuccess.class);

                        // Update the SharedPreferences to indicate that the user has seen the MembershipSuccess screen
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("hasSeenMembershipSuccess", true);
                        editor.apply();
                    } else if ("Approved".equals(membershipStatus) && hasSeenMembershipSuccess) {
                        // If the membership is completed and the user has already seen the MembershipSuccess screen, go to MembershipSuccess
                        intent = new Intent(Home.this, MembershipPage.class);
                    }

                    // Launch the appropriate activity using the result launcher
                    startActivity(intent);
                }
            } else {
                // Handle failure to fetch membership status
                Log.e("HomeActivity", "Failed to fetch membership status");
            }
        });
    }

    public void handleLogoutBtnClick(View v) {
        // sqlite

        // Redirect to the login screen
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}