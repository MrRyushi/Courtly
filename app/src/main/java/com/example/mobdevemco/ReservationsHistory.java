package com.example.mobdevemco;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReservationsHistory extends AppCompatActivity {

    private ReservationData[] reservationData, reservationData2;
    private ReservationAdapter reservationAdapter;
    private CurrentResAdapter reservationAdapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservations_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);

        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reservationData = new ReservationData[]{
                new ReservationData("Court Alpha", "2021-10-01", "10:00 AM to 11:00 AM"),
                new ReservationData("Court Bravo", "2021-10-02", "11:00 AM to 12:00 PM"),
                new ReservationData("Court Charlie", "2021-10-03", "12:00 PM to 01:00 PM"),
                new ReservationData("Court Delta", "2021-10-04", "01:00 PM to 02:00 PM"),
                new ReservationData("Court Echo", "2021-10-05", "02:00 PM to 03:00 PM"),
                new ReservationData("Court Foxtrot", "2021-10-06", "03:00 PM to 04:00 PM"),
                new ReservationData("Court Golf", "2021-10-07", "04:00 PM to 05:00 PM"),
                new ReservationData("Court Hotel", "2021-10-08", "05:00 PM to 06:00 PM"),
        };

        reservationData2 = new ReservationData[]{
                new ReservationData("Court Alpha", "2021-10-15", "10:00 AM to 11:00 AM"),
                new ReservationData("Court Bravo", "2021-10-16", "11:00 AM to 12:00 PM"),
        };

        reservationAdapter2 = new CurrentResAdapter(reservationData2, ReservationsHistory.this);
        recyclerView2.setAdapter(reservationAdapter2);
        reservationAdapter = new ReservationAdapter(reservationData, ReservationsHistory.this);
        recyclerView.setAdapter(reservationAdapter);

    }

    public void handleBackButtonClick(View view) {
        finish();
    }
}