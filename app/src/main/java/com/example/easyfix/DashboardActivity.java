package com.example.easyfix;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyfix.Auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private Button btnSignout, btnEditProfile, btnViewProfile, btnFindService, btnViewRating, btnPendingOrder,btnViewReviewHistory,btnGiveRating;
    private FirebaseAuth mAuth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btnSignout=findViewById(R.id.button_sign_out);
        btnEditProfile=findViewById(R.id.button_edit_profile);
        btnFindService=findViewById(R.id.button_find_service);
        btnViewProfile=findViewById(R.id.button_view_profile);
        btnViewRating=findViewById(R.id.button_rating);
        btnPendingOrder=findViewById(R.id.button_pending_order);
        btnViewReviewHistory=findViewById(R.id.button_view_review_history);
        btnGiveRating=findViewById(R.id.button_give_rating);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(mAuth.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String servicetype = document.getString("typeofService");
                        if(servicetype.equals("Service Taker")){
                            btnPendingOrder.setVisibility(View.GONE);
                            btnViewRating.setVisibility(View.GONE);
                        }
                        else{
                            btnGiveRating.setVisibility(GONE);
                            btnViewReviewHistory.setVisibility(GONE);
                        }

                    } else {
                        btnViewProfile.setVisibility(GONE);
                        btnPendingOrder.setVisibility(View.GONE);
                        btnViewRating.setVisibility(View.GONE);
                        btnGiveRating.setVisibility(GONE);
                        btnViewReviewHistory.setVisibility(GONE);
                        btnFindService.setVisibility(View.GONE);
                        btnEditProfile.setText("Complete Profile");

                        Log.d(TAG, "No such document");
                        Toast.makeText(DashboardActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(DashboardActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

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

        btnViewReviewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });

        btnGiveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, GiveReviewActivity.class);
                startActivity(intent);
            }
        });

        btnViewRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, RatingActivity.class);
                intent.putExtra("userId",mAuth.getUid());
                startActivity(intent);
            }
        });
    }
}
