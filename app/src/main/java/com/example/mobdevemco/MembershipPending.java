package com.example.mobdevemco;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class MembershipPending extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.membership_pending);

        // Set up the system bars insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Persistent listening for membership status changes
        listenForMembershipStatus();
    }

    // Set up the listener to check for changes in membership status
    private void listenForMembershipStatus() {
        // Get the current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the user's membership status in Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        // Set up the listener to listen for changes in the membership status
        userRef.child("membershipStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String membershipStatus = dataSnapshot.getValue(String.class);

                if (membershipStatus != null) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MembershipPending.this);
                    boolean hasSeenMembershipSuccess = prefs.getBoolean("hasSeenMembershipSuccess", false);

                    if (membershipStatus.equals("Approved")) {
                        if (!hasSeenMembershipSuccess) {
                            // If the user has not seen the MembershipSuccess screen, redirect them
                            Intent intent = new Intent(MembershipPending.this, MembershipSuccess.class);
                            startActivity(intent);

                            // Update the SharedPreferences to indicate the user has seen the screen
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("hasSeenMembershipSuccess", true);
                            editor.apply();
                        } else {
                            // Redirect to MembershipPage if they've already seen the success screen
                            Intent intent = new Intent(MembershipPending.this, MembershipPage.class);
                            startActivity(intent);
                        }
                        finish(); // Close the current activity
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors here
                Log.e("MembershipPending", "Database error: " + databaseError.getMessage());
            }
        });
    }


    // Handle back button press
    public void handleBackButton(View view) {
        finish();
    }
}
