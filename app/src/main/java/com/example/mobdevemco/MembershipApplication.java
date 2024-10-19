package com.example.mobdevemco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MembershipApplication extends AppCompatActivity {

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
    }

    public void handleBackButton(View view) {
        finish();
    }

    public void handleApplyButton(View view) {
        //Intent i = new Intent(this, MembershipPending.class);
        //startActivity(i);
        
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Application Sent");
        builder.setMessage("Your membership application has been sent successfully.");

        // Add a button to dismiss the dialog
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();  // Close the dialog
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
    }
}
