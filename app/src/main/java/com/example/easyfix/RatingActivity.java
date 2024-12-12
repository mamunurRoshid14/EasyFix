package com.example.easyfix;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Order> orderList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(this, orderList);
        recyclerView.setAdapter(ratingAdapter);

        fetchOrders();
    }

    private void fetchOrders() {
        progressBar.setVisibility(View.VISIBLE);

        String userId= getIntent().getStringExtra("userId");
        // Query Firestore for orders that are not reviewed and belong to the current user
        FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("isReviewed", true)  // Filter by isReviewed = true
                .whereEqualTo("orderTo", userId)  // Filter by orderFrom = userId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        orderList.clear();
                        // Convert Firestore documents to Order objects
                        orderList.addAll(queryDocumentSnapshots.toObjects(Order.class));

                        // Set orderId for each order
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(i).getId();
                            orderList.get(i).setOrderId(documentId);
                        }

                        // Notify the adapter that the data has changed
                        ratingAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Handle error fetching orders
                    progressBar.setVisibility(View.GONE);
                    Log.e("FetchOrdersError", "Error fetching orders: ", e);
                });
    }
}
