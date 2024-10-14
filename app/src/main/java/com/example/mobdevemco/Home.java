package com.example.mobdevemco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    public String courtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                public void onActivityResult(ActivityResult result) {

                }
            });

    public void handleAlphaCourtClick(View v){
        courtName = "Court Alpha";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

    public void handleBravoCourtClick(View v){
        courtName = "Court Bravo";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

    public void handleCharlieCourtClick(View v){
        courtName = "Court Charlie";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

    public void handleDeltaCourtClick(View v){
        courtName = "Court Delta";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

    public void handleEchoCourtClick(View v){
        courtName = "Court Echo";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

    public void handleFoxtrotCourtClick(View v){
        courtName = "Court Foxtrot";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

    public void handleGolfCourtClick(View v){
        courtName = "Court Golf";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

    public void handleHotelCourtClick(View v){
        courtName = "Court Hotel";
        Intent i = new Intent(Home.this, CourtReservation.class);
        i.putExtra("courtName", courtName);
        myActivityResultLauncher.launch(i);
    }

}