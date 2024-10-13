package com.example.easyfix;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_service_provider);

        etRadius = findViewById(R.id.etRadius);
        btnFind = findViewById(R.id.btnFind);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        progressBar = findViewById(R.id.progressBar);
        spinnerTypeOfService = findViewById(R.id.spinnerTypeOfService); // Initialize Spinner

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        userAccounts = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(FindServiceProvider.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FindServiceProvider.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    fetchUsersFromFirestore(); // Fetch users only when button is clicked
                }
            }
        });
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
            radiusKm = 10; // Default value
        }

        String selectedServiceType = spinnerTypeOfService.getSelectedItem().toString(); // Get selected service type
        List<UserAccount> nearbyUsers = new ArrayList<>();

        for (UserAccount user : userAccounts) {
            double distance = DistanceCalculator.getDistance(currentLat, currentLon, user.getLatitude(), user.getLongitude());

            if (distance <= radiusKm && user.getTypeofService().equals(selectedServiceType)) {
                nearbyUsers.add(user);
            }
        }

        // Update the RecyclerView with the filtered user list
        userAdapter = new UserAdapter(nearbyUsers, new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserAccount user) {
                // Handle item click here
                Toast.makeText(FindServiceProvider.this, "Clicked: " + user.getUserId(), Toast.LENGTH_SHORT).show();
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
