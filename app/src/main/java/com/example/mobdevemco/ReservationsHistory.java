package com.example.mobdevemco;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReservationsHistory extends AppCompatActivity {

    private ReservationData[] reservationData;
    private ReservationAdapter reservationAdapter;

    private TextView accountName;
    private TextView memberSince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if(reservationData == null) {
            setContentView(R.layout.activity_empty_reservations_history);
            String fullNameIntent = getIntent().getStringExtra("fullName");
            String memberSinceIntent = getIntent().getStringExtra("memberSince");

            accountName = findViewById(R.id.accountName);
            accountName.setText(fullNameIntent);
            memberSince = findViewById(R.id.memberSince);
            memberSince.setText(memberSinceIntent);
            return;
        }
        setContentView(R.layout.activity_reservations_history);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String fullNameIntent = getIntent().getStringExtra("fullName");
        String memberSinceIntent = getIntent().getStringExtra("memberSince");

        accountName = findViewById(R.id.accountName);
        accountName.setText(fullNameIntent);
        memberSince = findViewById(R.id.memberSince);
        memberSince.setText(memberSinceIntent);

        reservationAdapter = new ReservationAdapter(reservationData, ReservationsHistory.this);
        recyclerView.setAdapter(reservationAdapter);
    }

    public void handleBackButtonClick(View view) {
        finish();
    }
}