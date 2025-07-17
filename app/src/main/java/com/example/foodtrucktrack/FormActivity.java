package com.example.foodtrucktrack;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class FormActivity extends AppCompatActivity {

    EditText etName, etType, etDesc, etLat, etLng, etReporter;
    Button btnSubmit;
    LinearLayout llTruckList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Handle back arrow
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        // Initialize views
        etName = findViewById(R.id.etName);
        etType = findViewById(R.id.etType);
        etDesc = findViewById(R.id.etDesc);
        etLat = findViewById(R.id.etLat);
        etLng = findViewById(R.id.etLng);
        etReporter = findViewById(R.id.etReporter);
        btnSubmit = findViewById(R.id.btnSubmitTruck);
        llTruckList = findViewById(R.id.llTruckList);

        // Handle submit button click
        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String lat = etLat.getText().toString().trim();
            String lng = etLng.getText().toString().trim();
            String reporter = etReporter.getText().toString().trim();

            if (name.isEmpty() || type.isEmpty() || desc.isEmpty() ||
                    lat.isEmpty() || lng.isEmpty() || reporter.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubmit.setEnabled(false); // Prevent double submission
            submitToServer(name, type, desc, lat, lng, reporter);
        });
    }

    private void submitToServer(String name, String type, String desc,
                                String lat, String lng, String reporter) {
        String url = "http://10.0.2.2/foodtruck/add_truck.php"; // Adjust for actual API

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(FormActivity.this, "Truck submitted!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    displayTruck(name, type, desc, lat, lng, reporter);
                    btnSubmit.setEnabled(true);
                },
                error -> {
                    String errorMsg = (error.getMessage() != null) ? error.getMessage() : "Unknown error";
                    Toast.makeText(FormActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("FormActivity", "Volley Error: ", error);
                    btnSubmit.setEnabled(true);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("name", name);
                data.put("type", type);
                data.put("description", desc);
                data.put("latitude", lat);
                data.put("longitude", lng);
                data.put("reported_by", reporter);
                return data;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void clearFields() {
        etName.setText("");
        etType.setText("");
        etDesc.setText("");
        etLat.setText("");
        etLng.setText("");
        etReporter.setText("");
    }

    private void displayTruck(String name, String type, String desc, String lat, String lng, String reporter) {
        TextView truckView = new TextView(this);
        truckView.setText("Name: " + name + "\nType: " + type +
                "\nLocation: " + desc + "\nLat: " + lat + ", Lng: " + lng +
                "\nReported by: " + reporter);
        truckView.setTextSize(16f);
        truckView.setPadding(20, 20, 20, 20);
        truckView.setBackgroundResource(R.color.soft_brown_light);
        truckView.setTextColor(getResources().getColor(R.color.soft_brown_dark));
        truckView.setElevation(6f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        truckView.setLayoutParams(params);

        llTruckList.addView(truckView, 0); // Add to top of the list
    }
}
