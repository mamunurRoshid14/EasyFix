package com.example.easyfix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewProfile extends AppCompatActivity {
    private static final String TAG = "ViewProfile";
    private TextView tvFullName, tvAge, tvUserEmail, tvPhoneNumber, tvLocation, tvTypeOfService, tvBio, tvNumberOfOrder, tvRating;
    private ImageView ivProfilePicture;

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

        // Get the userId from the Intent
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

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
    }
}
