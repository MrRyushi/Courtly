package com.example.mobdevemco;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RecommendFragment extends DialogFragment {
    private String reservationDate;
    private String courtName;
    private String timeSlots;

    // Factory method to create a new instance of the fragment with data
    public static RecommendFragment newInstance(String date, String court, String slots) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putString("reservationDate", date);
        args.putString("courtName", court);
        args.putString("timeSlots", slots);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recommend_fragment, container, false);

        if (getArguments() != null) {
            reservationDate = getArguments().getString("reservationDate");
            courtName = getArguments().getString("courtName");
            timeSlots = getArguments().getString("timeSlots");
        }

        // Set the data to the views
        TextView recommendationTextView = view.findViewById(R.id.recommendationText);
        Button actionButton = view.findViewById(R.id.actionButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        recommendationTextView.setText("Would you like to reserve " + courtName + " again for " + timeSlots + "?");

        actionButton.setOnClickListener(this::openReserveActivity);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    public void openReserveActivity(View v) {
        // Open the ReserveActivity
        Intent i = new Intent(getActivity(), ReserveACourt.class);
        i.putExtra("courtName", courtName);
        timeSlots = timeSlots.replace("[", "").replace("]", "");
        i.putExtra("timeSlots", timeSlots);
        dismiss();
        startActivity(i);
    }
}
