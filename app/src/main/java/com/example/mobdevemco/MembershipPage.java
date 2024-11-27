package com.example.mobdevemco;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MembershipPage extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_membership_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SPHelper spHelper = new SPHelper(this);
        String user_uid = spHelper.getUserUID();
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("users").child(user_uid);
        accountName = findViewById(R.id.accountName);

        // Fetch and set user's full name
        user.child("fullName").get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String name = dataSnapshot.getValue(String.class);
                accountName.setText(name != null ? name : "User");
            } else {
                accountName.setText("User data not found");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            accountName.setText("Error loading name");
        });

    }

    public void handleBackButton(View view) {
        finish();
    }

}