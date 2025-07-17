package com.example.foodtrucktrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tvEmail;
    Button btnLogout;
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure the XML is named properly

        // Initialize views
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        backArrow = findViewById(R.id.backArrow); // 👈 Added reference to back arrow

        loadUserInfo();

        // Back arrow logic
        backArrow.setOnClickListener(v -> finish()); // 👈 Close activity on back arrow press

        // Logout logic
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email != null && !email.isEmpty()) {
            tvEmail.setText("Email: " + email);
        } else {
            tvEmail.setText("Email not available");
        }
    }
}
