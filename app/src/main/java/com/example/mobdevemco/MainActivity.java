package com.example.mobdevemco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    Button registerBtn;
    ImageView landingImg;


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
        landingImg = findViewById(R.id.landingImg);

        loginBtn.setOnClickListener(this::loginBtnOnClick);
        registerBtn.setOnClickListener(this::registerBtnOnClick);
        landingImg.setImageResource(R.drawable.landing_bg);

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

    void handleLoginBtnClick(View v){
        Intent i = new Intent(MainActivity.this, Home.class);
        startActivity(i);
    }

}
