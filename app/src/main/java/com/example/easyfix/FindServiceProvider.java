package com.example.easyfix;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Auth.LoginActivity;
import com.example.easyfix.Auth.SignupActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FindServiceProvider extends AppCompatActivity {
    private static final String TAG = "FindServiceProvider";
    private EditText etRadius;
    private Button btnFind;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<UserAccount> userAccounts;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Spinner spinnerTypeOfService;
    private ImageView electrician, plumber, painting, insect, airconditioner, gardener, cleaner, animalCare, menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_service_provider);

        etRadius = findViewById(R.id.etRadius);
        btnFind = findViewById(R.id.btnFind);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        progressBar = findViewById(R.id.progressBar);
        spinnerTypeOfService = findViewById(R.id.spinnerTypeOfService); // Initialize Spinner
        electrician = findViewById(R.id.electrician);
        plumber = findViewById(R.id.plumber);
        painting = findViewById(R.id.painting);
        insect = findViewById(R.id.insecticide);
        gardener = findViewById(R.id.gardening);
        airconditioner = findViewById(R.id.airconditioner);
        cleaner = findViewById(R.id.cleaning);
        animalCare = findViewById(R.id.animalcare);
        menuIcon = findViewById(R.id.menu_icon);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        userAccounts = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (ActivityCompat.checkSelfPermission(FindServiceProvider.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FindServiceProvider.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    fetchUsersFromFirestore(); // Fetch users only when button is clicked
                }
            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v); // Show the menu when the icon is clicked
            }
        });

        plumber.setOnClickListener(new ServiceClickListener(0)); // Plumbing
        electrician.setOnClickListener(new ServiceClickListener(1)); // Electrical
        cleaner.setOnClickListener(new ServiceClickListener(2)); // Cleaning
        painting.setOnClickListener(new ServiceClickListener(3)); // Painting
        insect.setOnClickListener(new ServiceClickListener(4)); // Pest Control Services
        airconditioner.setOnClickListener(new ServiceClickListener(6)); // AC Servicing and Repair
        animalCare.setOnClickListener(new ServiceClickListener(7)); // Pet Care Services
        gardener.setOnClickListener(new ServiceClickListener(8)); // Gardening Services

    }
    // Custom OnClickListener class
    private class ServiceClickListener implements View.OnClickListener {
        private final int serviceTypeIndex;

        public ServiceClickListener(int serviceTypeIndex) {
            this.serviceTypeIndex = serviceTypeIndex;
        }

        @Override
        public void onClick(View v) {
            spinnerTypeOfService.setSelection(serviceTypeIndex); // Set spinner selection
            btnFind.performClick(); // Trigger btnFind click to start the search
        }
    }

    // Method to show the popup menu
    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu()); // Inflate the menu

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CharSequence itemTitle = item.getTitle();

                if (itemTitle.equals("Login")) {
                    // Handle Login
                    Intent intent = new Intent(FindServiceProvider.this, LoginActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemTitle.equals("Signup")) {
                    // Handle Signup
                    Intent intent = new Intent(FindServiceProvider.this, SignupActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemTitle.equals("About Us")) {
                    // Handle About Us
                    return true;
                }

                return false;
            }
        });

        popupMenu.show(); // Display the popup menu
    }


    private void fetchUsersFromFirestore() {
        CollectionReference usersRef = db.collection("users");
        progressBar.setVisibility(View.VISIBLE);

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    userAccounts.clear(); // Clear old data

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserAccount user = document.toObject(UserAccount.class);
                        userAccounts.add(user);
                    }

                    getCurrentLocationAndFilterUsers();
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(FindServiceProvider.this, "Failed to load users: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCurrentLocationAndFilterUsers() {
        progressBar.setVisibility(View.VISIBLE);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        progressBar.setVisibility(View.GONE);

                        if (location != null) {
                            double currentLat = location.getLatitude();
                            double currentLon = location.getLongitude();
                            filterUsers(currentLat, currentLon);
                        } else {
                            Log.e(TAG, "Location is null");
                            Toast.makeText(FindServiceProvider.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void filterUsers(double currentLat, double currentLon) {
        int radiusKm;

        try {
            radiusKm = Integer.parseInt(etRadius.getText().toString());
        } catch (NumberFormatException e) {
            radiusKm = 30; // Default value
        }

        String selectedServiceType = spinnerTypeOfService.getSelectedItem().toString(); // Get selected service type
        List<UserAccount> nearbyUsers = new ArrayList<>();

        for (UserAccount user : userAccounts) {
            double distance = DistanceCalculator.getDistance(currentLat, currentLon, user.getLatitude(), user.getLongitude());

            if (distance <= radiusKm && user.getTypeofService().equals(selectedServiceType)) {
                nearbyUsers.add(user);
            }
        }

        Collections.sort(nearbyUsers, new Comparator<UserAccount>() {
            @Override
            public int compare(UserAccount u1, UserAccount u2) {
                return Double.compare(u2.getRating(), u1.getRating()); // Higher rating first
            }
        });

        // Update the RecyclerView with the filtered user list
        userAdapter = new UserAdapter(nearbyUsers, new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserAccount user) {
                // Create an Intent to start the ViewProfile activity
                Intent intent = new Intent(FindServiceProvider.this, ViewProfile.class);

                // Pass only the userId to the ViewProfile activity
                String userid=user.getUserId();
                intent.putExtra("userId", userid);

                // Start the ViewProfile activity
                startActivity(intent);

               // Toast.makeText(FindServiceProvider.this, user.getUserId() + " clikced", Toast.LENGTH_SHORT).show();

            }
        });


        recyclerViewUsers.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUsersFromFirestore(); // Fetch users after permission is granted
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
