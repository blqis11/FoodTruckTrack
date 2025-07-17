package com.example.foodtrucktrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnLocation, btnForm, btnProfile, btnAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI components with IDs from activity_main.xml
        btnLocation = findViewById(R.id.btnLocation);
        btnForm = findViewById(R.id.btnForm);
        btnProfile = findViewById(R.id.btnProfile);
        btnAboutUs = findViewById(R.id.btnAboutUs);

        // Navigate to MapActivity
       btnLocation.setOnClickListener(v -> {
           startActivity(new Intent(MainActivity.this, MapActivity.class));
       });

        // Navigate to FormActivity
        btnForm.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FormActivity.class));

        });

        // Navigate to ProfileActivity
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));

        });

        // Navigate to AboutUsActivity
        btnAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
        });
    }
}
