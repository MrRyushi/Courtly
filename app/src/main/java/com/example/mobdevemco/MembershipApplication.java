package com.example.mobdevemco;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MembershipApplication extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.membership_application);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void handleBackButton(View view) {
        finish();
    }

    public void handleApplyButton(View view) {
        
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Application Sent");
        builder.setMessage("Your membership application has been sent successfully.");

        // Update the membership status in Firebase database
        updateMembershipStatus("Requested");

        // Add a button to dismiss the dialog
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();  // Close the dialog
//            Intent i = new Intent();
//            i.putExtra("membershipApplication", "success");
//            setResult(RESULT_OK, i);
            finish();
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateMembershipStatus(String status) {
        // Reference to the user's data in Firebase
        DatabaseReference userRef = mDatabase.child("users").child(userId);

        // Update the user's membership status
        userRef.child("membershipStatus").setValue(status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully updated membership status
                        Log.d("MembershipApplication", "Membership status updated to: " + status);
                    } else {
                        // Handle failure to update the status
                        Log.e("MembershipApplication", "Failed to update membership status");
                    }
                });
    }
}
