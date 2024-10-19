package com.example.mobdevemco;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CurrentReservations extends AppCompatActivity {
    private ReservationData[] currentReservationData;
    private CurrentReservationsAdapter reservationAdapter;
    private ActivityResultLauncher<Intent> myActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_current_reservations);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

                    }
                });

        currentReservationData = new ReservationData[]{
                new ReservationData(1,"Court Alpha", "2021-10-01", "10:00 AM to 11:00 AM"),
                new ReservationData(2,"Court Bravo", "2021-10-02", "11:00 AM to 12:00 PM"),
        };

        reservationAdapter = new CurrentReservationsAdapter(currentReservationData, CurrentReservations.this);
        recyclerView.setAdapter(reservationAdapter);
    }

    public void handleBackButtonClick(View view) {
        finish();
    }

    public void handleHistoryButtonClick(View view) {
        Intent intent = new Intent(CurrentReservations.this, ReservationsHistory.class);
        myActivityResultLauncher.launch(intent);
    }
}