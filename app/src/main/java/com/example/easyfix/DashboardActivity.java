package com.example.easyfix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyfix.Auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private Button btnSignout, btnEditProfile, btnViewProfile, btnFindService;
    private FirebaseAuth mAuth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btnSignout=findViewById(R.id.button_sign_out);
        btnEditProfile=findViewById(R.id.button_edit_profile);
        btnFindService=findViewById(R.id.button_find_service);
        btnViewProfile=findViewById(R.id.button_view_profile);
        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


// Sign out user and redirect to login activity
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        btnFindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, FindServiceProvider.class);
                startActivity(intent);
            }
        });

        btnViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the ViewProfile activity
                Intent intent = new Intent(DashboardActivity.this, ViewProfile.class);
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    // Pass only the userId to the ViewProfile activity
                    intent.putExtra("userId", currentUser.getUid());
                    // Start the ViewProfile activity
                    startActivity(intent);
                } else {
                    // Handle user not signed in
                    Toast.makeText(DashboardActivity.this, "User not signed in", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
