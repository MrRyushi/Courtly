package com.example.mobdevemco;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class RegisterBottomSheet extends BottomSheetDialogFragment {

    TextView signInTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the bottom sheet
        View view = inflater.inflate(R.layout.bottom_sheet_register, container, false);

        // Initialize the TextView
        signInTxt = view.findViewById(R.id.signUpTxt);

        // Set OnClickListener for the TextView
        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInOnClick(v);
            }
        });

        return view; // Return the inflated view
    }

    void signInOnClick(View v) {
        dismiss();
        new LoginBottomSheet().show(getParentFragmentManager(), "LoginBottomSheet");
    }
}