package com.example.easyfix;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;

public class GiveReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GiveReviewAdapter reviewAdapter;
    private List<Order> orderList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_review);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        reviewAdapter = new GiveReviewAdapter(this, orderList);
        recyclerView.setAdapter(reviewAdapter);

        fetchOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data or UI when returning to the activity
        fetchOrders();// Example method to reload data
    }


    private void fetchOrders() {
        progressBar.setVisibility(View.VISIBLE);

        // Get the current user's UID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query Firestore for orders that are not reviewed and belong to the current user
        FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("confirmed", true)
                .whereEqualTo("isReviewed", false)  // Filter by isReviewed = false
                .whereEqualTo("orderFrom", currentUserId)  // Filter by orderFrom = current user's UID
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
                        reviewAdapter.notifyDataSetChanged();
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
