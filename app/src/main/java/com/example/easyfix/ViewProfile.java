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
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ViewProfile extends AppCompatActivity {
    private static final String TAG = "ViewProfile";
    private TextView tvFullName, tvAge, tvUserEmail, tvPhoneNumber, tvLocation, tvTypeOfService, tvBio, tvNumberOfOrder, tvRating;
    private ImageView ivProfilePicture;
    private Button btnViewReviews, btnPlaceOrder;
    private String toUser, servicetype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        tvFullName = findViewById(R.id.tvFullName);
        tvAge = findViewById(R.id.tvAge);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvLocation = findViewById(R.id.tvLocation);
        tvTypeOfService = findViewById(R.id.tvTypeOfService);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvBio = findViewById(R.id.tvBio);
        tvNumberOfOrder = findViewById(R.id.tvNumberOfOrder);
        tvRating = findViewById(R.id.tvRating);
        btnViewReviews = findViewById(R.id.btnViewReview);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);


        // Get the userId from the Intent
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        toUser = userId;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Get the current user
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null && currentUser.getUid().equals(userId)){
            btnPlaceOrder.setVisibility(View.GONE);
            btnViewReviews.setVisibility(View.GONE);
        }

        // Fetch data from Firestore
        fetchUserData(userId);

        // Set onClick listeners for email and phone number
        setOnClickListeners();
    }

    private void fetchUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Set the data to TextViews
                    tvFullName.setText(document.getString("fullName"));
                    tvAge.setText(String.valueOf(document.getLong("age")));
                    tvUserEmail.setText(document.getString("email"));
                    tvPhoneNumber.setText(document.getString("phoneNumber"));
                    tvLocation.setText(document.getString("location"));
                    tvTypeOfService.setText(document.getString("typeofService"));
                    tvBio.setText(document.getString("bio"));
                    tvNumberOfOrder.setText(String.valueOf(document.getLong("number_of_orders")));
                    tvRating.setText(String.valueOf(document.getDouble("rating")));
                    servicetype = document.getString("typeofService");

                    if(servicetype.equals("Service Taker")){
                        tvRating.setVisibility(View.GONE);
                        tvNumberOfOrder.setVisibility(View.GONE);
                    }
                    // Load profile image using Picasso or any other image loading library
                    String imageUrl = document.getString("imageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(ivProfilePicture);
                    } else {
                        ivProfilePicture.setImageResource(R.drawable.default_profile_image);
                    }
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(ViewProfile.this, "No such document", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(ViewProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
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

        btnPlaceOrder.setOnClickListener(view -> {
            // Initialize Firestore and Auth
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            // Get the current user
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                // Show error if no user is logged in
                Toast.makeText(getApplicationContext(), "User not logged in!", Toast.LENGTH_LONG).show();
                return;
            }

            // Create an Order object
            Order order = new Order();
            order.setOrderFrom(currentUser.getUid()); // Set UID of the current user
            order.setOrderTo(toUser); // Example value, replace with real data
            order.setConfirmed(false);
            order.setReview("Not Given Yet"); // Default review
            order.setRating(5.0); // Default rating
            order.setServiceType(servicetype); // Example value, replace with real data

            // Push to Firestore
            db.collection("orders")
                    .document()
                    .set(order)
                    .addOnSuccessListener(aVoid -> {
                        // Success message
                        Toast.makeText(getApplicationContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Error message
                        Toast.makeText(getApplicationContext(), "Error placing order: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
