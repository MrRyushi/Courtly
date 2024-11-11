package com.example.mobdevemco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LoginBottomSheet extends BottomSheetDialogFragment {
    TextView signUpTxt;
    TextView forgotPasswordTxt;
    Button loginBtn;
    EditText editTextEmail, editTextPassword;


    public interface OnLoginListener {
        void onLogin(String email, String password);
    }

    public interface OnResetPasswordListener {
        void onResetPassword(String email);
    }

    private OnLoginListener listener;
    private OnResetPasswordListener resetListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginBottomSheet.OnLoginListener) {
            listener = (LoginBottomSheet.OnLoginListener) context;
        }
        if (context instanceof LoginBottomSheet.OnResetPasswordListener) {
            resetListener = (LoginBottomSheet.OnResetPasswordListener) context;
        }
        if (listener == null || resetListener == null) {
            throw new RuntimeException(context.toString() + " must implement both OnLoginListener and OnResetPasswordListener");
        }
    }

    
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
                handleResetPasswordClick(v);
            }
        });

        loginBtn = view.findViewById(R.id.loginB);

        // full name, email, and password edit text fields
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

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
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save login status in SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        // Pass data back to MainActivity via listener
        listener.onLogin(email, password);

        dismiss();
    }

    void handleResetPasswordClick(View v){
        String email = editTextEmail.getText().toString();
        if(email.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        resetListener.onResetPassword(email);
    }

}
