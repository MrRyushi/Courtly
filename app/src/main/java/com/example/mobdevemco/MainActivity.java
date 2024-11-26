package com.example.mobdevemco;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements RegisterBottomSheet.OnRegisterListener, LoginBottomSheet.OnLoginListener, LoginBottomSheet.OnResetPasswordListener {

    Button loginBtn;
    Button registerBtn;
    ImageView landingImg;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    SPHelper spHelper;

    @Override
    public void onRegister(String fullName, String email, String password) {
        handleRegister(email, password, fullName);
    }

    @Override
    public void onLogin(String email, String password) {
        handleLogin(email, password);
    }

    @Override
    public void onResetPassword(String email) {
        handleResetPassword(email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.landing_ui);

        // Initialize SPHelper
        spHelper = new SPHelper(this);

        // Check if user is already logged in
        if (spHelper.isLoggedIn()) {
            // Navigate to the Home activity if logged in
            navigateToHome(spHelper.getUserUID());
            return;
        }

        // Setup UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase auth and database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        landingImg = findViewById(R.id.landingImg);

        loginBtn.setOnClickListener(this::loginBtnOnClick);
        registerBtn.setOnClickListener(this::registerBtnOnClick);
        landingImg.setImageResource(R.drawable.landing_bg);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and reload.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    void loginBtnOnClick(View v) {
        // Start the LoginBottomSheet
        openLoginBottomSheet();
    }

    void registerBtnOnClick(View v) {
        // Start the RegisterBottomSheet
        RegisterBottomSheet bottomSheet = new RegisterBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), RegisterBottomSheet.class.getSimpleName());
    }

    void openLoginBottomSheet() {
        LoginBottomSheet bottomSheet = new LoginBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), LoginBottomSheet.class.getSimpleName());
    }

    void handleRegister(String email, String password, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registered successfully
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user.getUid(), fullName, email);
                            Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            RegisterBottomSheet registerBottomSheet = (RegisterBottomSheet)
                                    getSupportFragmentManager().findFragmentByTag(RegisterBottomSheet.class.getSimpleName());
                            if (registerBottomSheet != null) {
                                registerBottomSheet.dismiss();
                            }

                            openLoginBottomSheet();
                        } else {
                            Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email, false, "No application", 0, "No reservations");
        mDatabase.child("users").child(userId).setValue(user);
    }

    void handleLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Save session to shared preferences
                            spHelper.saveUserSession(user.getUid(), user.getEmail());

                            // Navigate to Home
                            navigateToHome(user.getUid());

                            LoginBottomSheet bottomSheet = (LoginBottomSheet)
                                    getSupportFragmentManager().findFragmentByTag(LoginBottomSheet.class.getSimpleName());
                            if (bottomSheet != null) {
                                bottomSheet.dismissOnSuccess();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed: Email or password is incorrect",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void handleResetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(MainActivity.this, "Reset password email sent successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to send reset email: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void navigateToHome(String userUID) {
        Intent intent = new Intent(MainActivity.this, Home.class);
        intent.putExtra("user_uid", userUID);
        startActivity(intent);
        finish();
    }
}
