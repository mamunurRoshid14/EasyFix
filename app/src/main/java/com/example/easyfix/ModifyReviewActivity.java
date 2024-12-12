package com.example.easyfix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText editReview;
    private Button btnSave;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_review);

        ratingBar = findViewById(R.id.rating_bar_modify);
        editReview = findViewById(R.id.edit_review);
        btnSave = findViewById(R.id.btn_save);

        // Get data from intent
        orderId = getIntent().getStringExtra("orderId");
        float currentRating = getIntent().getFloatExtra("currentRating", 0);
        String currentReview = getIntent().getStringExtra("currentReview");

        // Set current values
        ratingBar.setRating(currentRating);
        editReview.setText(currentReview);

        btnSave.setOnClickListener(v -> saveReview());
    }

    private void saveReview() {
        float newRating = ratingBar.getRating();
        String newReview = editReview.getText().toString().trim(); // Ensure there are no leading/trailing spaces

        // Ensure the review and rating are valid
        if (newReview.isEmpty()) {
            Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newRating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the Firestore document with the new rating, review, and confirmation status
        FirebaseFirestore.getInstance().collection("orders")
                .document(orderId) // Ensure 'orderId' is set
                .update("rating", newRating, "review", newReview, "isReviewed", true)
                .addOnSuccessListener(aVoid -> {
                    // After successfully saving the review, update the user rating
                    updateUserRating();
                    Toast.makeText(this, "Review saved successfully!", Toast.LENGTH_SHORT).show();

                    // Close the current activity and return to the previous screen
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Failure: Handle errors (e.g., show a Toast)
                    Toast.makeText(this, "Failed to save review. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("SaveReviewError", "Error saving review: ", e); // Log the error for debugging
                });
    }

    private void updateUserRating() {
        // Get the 'orderTo' user ID from the intent
        String userIdToUpdate = getIntent().getStringExtra("orderTo");

        if (userIdToUpdate == null || userIdToUpdate.isEmpty()) {
            Toast.makeText(this, "User not found or invalid user ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user's current rating and number of orders from Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(userIdToUpdate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve current rating and number of orders
                        double currentRating = documentSnapshot.getDouble("rating");
                        long numberOfOrders = documentSnapshot.getLong("number_of_orders");

                        // Calculate the new average rating
                        double newRating = (currentRating * numberOfOrders + ratingBar.getRating()) / (numberOfOrders + 1);

                        // Update the user's rating and number of orders
                        FirebaseFirestore.getInstance().collection("users")
                                .document(userIdToUpdate)
                                .update("rating", newRating, "number_of_orders", numberOfOrders + 1)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "User rating updated successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to update user rating. Please try again.", Toast.LENGTH_SHORT).show();
                                    Log.e("UpdateUserRatingError", "Error updating user rating: ", e);
                                });
                    } else {
                        // Handle case where user document doesn't exist
                        Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("FetchUserError", "Error fetching user data: ", e);
                });
    }


}
