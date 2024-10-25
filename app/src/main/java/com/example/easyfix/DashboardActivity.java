package com.example.easyfix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyfix.Auth.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private Button btnEditProfile, btnSignOut;
    private TextView tvUserEmail, tvFullName, tvPhoneNumber, tvLocation, tvAge;
    private TextView tvLatitude, tvLongitude, tvTypeOfService; // TextViews for latitude, longitude, and service type
    private ImageView ivProfilePicture;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        btnSignOut = findViewById(R.id.btnSignOut);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvFullName = findViewById(R.id.tvFullName);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvLocation = findViewById(R.id.tvLocation);
        tvAge = findViewById(R.id.tvAge);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        tvTypeOfService = findViewById(R.id.tvTypeOfService);

        if (currentUser != null) {
            tvUserEmail.setText(currentUser.getEmail());

            // Fetch user profile from Firestore
            fetchUserProfile(currentUser.getUid());
        } else {
            Log.e(TAG, "Current user is null");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
        setOnClickListeners();
    }

    private void fetchUserProfile(String userId) {
        Log.d(TAG, "Fetching user profile for userId: " + userId);
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            Log.d(TAG, "Document data: " + document.getData());
                            UserAccount user = document.toObject(UserAccount.class);
                            if (user != null) {
                                Log.d(TAG, "User profile loaded: " + user.getFullName());
                                runOnUiThread(() -> {
                                    tvFullName.setText(user.getFullName());
                                    tvPhoneNumber.setText(user.getPhoneNumber());
                                    tvLocation.setText(user.getLocation());
                                    tvAge.setText(String.valueOf(user.getAge()));
                                    tvTypeOfService.setText(user.getTypeofService()); // New field for service type
                                    if (user.getImageUrl() != null) {
                                        Picasso.get().load(user.getImageUrl()).into(ivProfilePicture);
                                    }
                                });
                            } else {
                                Log.e(TAG, "UserAccount object is null");
                                runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing user document", e);
                            runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Log.e(TAG, "Document does not exist");
                        runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "User profile not found", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e(TAG, "Task failed with exception: ", task.getException());
                    runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void setOnClickListeners() {
        tvUserEmail.setOnClickListener(view -> {
            String email = tvUserEmail.getText().toString();
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });

        tvPhoneNumber.setOnClickListener(view -> {
            String phone = tvPhoneNumber.getText().toString();
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(phoneIntent);
        });
    }
}
