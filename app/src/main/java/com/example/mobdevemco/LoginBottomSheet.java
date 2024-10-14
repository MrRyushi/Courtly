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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LoginBottomSheet extends BottomSheetDialogFragment {

    TextView signUpTxt;
    TextView forgotPasswordTxt;
    Button loginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the bottom sheet
        View view = inflater.inflate(R.layout.bottom_sheet_login, container, false);

        signUpTxt = view.findViewById(R.id.signUpTxt);
        forgotPasswordTxt = view.findViewById(R.id.forgotTxt);

        // Set OnClickListener for the TextView
        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpOnClick(v);
            }
        });

        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement forgot password
            }
        });

        loginBtn = view.findViewById(R.id.loginB);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginBtnClick(v);  // Call the method to handle login
            }
        });

        return view; // Return the inflated view
    }

    void signUpOnClick(View v) {
        dismiss();
        new RegisterBottomSheet().show(getParentFragmentManager(), "RegisterBottomSheet");
    }

    void handleLoginBtnClick(View v) {
        // Start the Home activity when the login button is clicked
        Intent i = new Intent(getActivity(), Home.class);
        startActivity(i);
        dismiss();  // Close the bottom sheet after login
    }

}
