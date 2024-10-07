package com.example.mobdevemco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.landing_ui);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(this::loginBtnOnClick);
        registerBtn.setOnClickListener(this::registerBtnOnClick);

    }

    void loginBtnOnClick(View v) {
        // Start the LoginBottomSheet
        LoginBottomSheet bottomSheet = new LoginBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    void registerBtnOnClick(View v) {
        // Start the RegisterBottomSheet
        RegisterBottomSheet bottomSheet = new RegisterBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

}
