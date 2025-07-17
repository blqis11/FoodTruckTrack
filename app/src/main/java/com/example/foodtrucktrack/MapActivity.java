package com.example.foodtrucktrack;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.foodtrucktrack.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner typeSpinner;
    private SearchView searchView;

    private final List<Marker> markerList = new ArrayList<>();
    private JSONArray truckData;

    private String currentType = "All";
    private String currentSearch = "";

    private static final String API_URL = "http://10.0.2.2/foodtruck/api_foodtrucks.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        searchView = findViewById(R.id.searchView);
        typeSpinner = findViewById(R.id.typeSpinner);

        setupSpinner();
        setupSearchView();
        setupMap();
    }

    private void setupSpinner() {
        String[] types = {"All", "Breakfast", "Dessert", "BBQ", "Coffee", "Noodles", "Burgers", "Fusion", "Ice Cream", "Vegan", "Drinks"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentType = types[position];
                filterMarkers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearch = newText.toLowerCase();
                filterMarkers();
                return true;
            }
        });
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map fragment not found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchFoodTrucks();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                String[] snippetParts = marker.getSnippet().split("\\|");

                ((TextView) view.findViewById(R.id.title)).setText(marker.getTitle());
              ((TextView) view.findViewById(R.id.type)).setText(snippetParts[0]);
            ((TextView) view.findViewById(R.id.description)).setText(snippetParts[1]);
                ((TextView) view.findViewById(R.id.reported_by)).setText("Reported by: " + snippetParts[2]);
                ((TextView) view.findViewById(R.id.created_at)).setText("Created at: " + snippetParts[3]);

                return view;
            }
        });

        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(MapActivity.this, "Clicked: " + marker.getTitle(), Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    private void fetchFoodTrucks() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                response -> {
                    truckData = response;
                    filterMarkers();
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.toString());
                    Toast.makeText(MapActivity.this, "Failed to load food truck data", Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void filterMarkers() {
        if (mMap == null || truckData == null) return;

        mMap.clear();
        markerList.clear();
        boolean found = false;

        try {
            for (int i = 0; i < truckData.length(); i++) {
                JSONObject truck = truckData.getJSONObject(i);

                String name = truck.getString("name");
                String type = truck.getString("type");
                String desc = truck.getString("description");
                double lat = truck.getDouble("latitude");
                double lng = truck.getDouble("longitude");
                String reporter = truck.optString("reported_by", "Unknown");
                String created = truck.optString("created_at", "N/A");

                boolean matchesType = currentType.equals("All") || type.equalsIgnoreCase(currentType);
                boolean matchesName = name.toLowerCase().contains(currentSearch);

                if (matchesType && matchesName) {
                    LatLng location = new LatLng(lat, lng);
                    String snippet = type + "|" + desc + "|" + reporter + "|" + created;

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(name)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                    markerList.add(marker);
                    found = true;
                }
            }

            if (found) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(3.1390, 101.6869), 12));
            } else {
                Toast.makeText(MapActivity.this, "No matching food trucks found.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("FilterError", "JSON parsing error: " + e.getMessage());
        }
    }
}
