package com.example.mobdevemco;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class RegisterBottomSheet extends BottomSheetDialogFragment {
    TextView signInTxt;
    EditText editTextFullBName, editTextEmail, editTextPassword;
    Button registerBtn;
    ProgressBar progressBar;

    public interface OnRegisterListener {
        void onRegister(String fullName, String email, String password);
    }

    private OnRegisterListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterListener) {
            listener = (OnRegisterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterListener");
        }
    }

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

        // full name, email, and password edit text fields
        editTextFullBName = view.findViewById(R.id.editTextFullName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        progressBar = view.findViewById(R.id.registerProgressBar);

        registerBtn = view.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                handleRegisterBtnClick(v);  // Call the method to handle login
            }
        });

        return view; // Return the inflated view
    }

    void signInOnClick(View v) {
        dismiss();
        new LoginBottomSheet().show(getParentFragmentManager(), "LoginBottomSheet");
    }

    // Call this method when the register button is clicked
    void handleRegisterBtnClick(View v) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String fullName = editTextFullBName.getText().toString();

        if(email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        dismiss();

        // Pass data back to MainActivity via listener
        listener.onRegister(fullName, email, password);
        progressBar.setVisibility(View.GONE);

    }

}