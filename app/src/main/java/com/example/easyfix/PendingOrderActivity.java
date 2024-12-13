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

public class PendingOrderActivity extends AppCompatActivity implements PendingOrderAdapter.OnOrderActionListener {

    private RecyclerView recyclerView;
    private PendingOrderAdapter ratingAdapter;
    private List<Order> orderList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        ratingAdapter = new PendingOrderAdapter(this, orderList, this);
        recyclerView.setAdapter(ratingAdapter);

        fetchOrders();
    }

    private void fetchOrders() {
        progressBar.setVisibility(View.VISIBLE);

        String userId = getIntent().getStringExtra("userId");
        FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("confirmed", false)
                .whereEqualTo("isReviewed", false)
                .whereEqualTo("orderTo", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        orderList.clear();
                        orderList.addAll(queryDocumentSnapshots.toObjects(Order.class));

                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(i).getId();
                            orderList.get(i).setOrderId(documentId);
                        }

                        ratingAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("FetchOrdersError", "Error fetching orders: ", e);
                });
    }

    @Override
    public void onAccept(Order order, int position) {
        FirebaseFirestore.getInstance().collection("orders")
                .document(order.getOrderId())
                .update("confirmed", true)
                .addOnSuccessListener(aVoid -> {
                    orderList.remove(position);
                    ratingAdapter.notifyItemRemoved(position);
                    Log.d("PendingOrderActivity", "Order accepted and confirmed.");
                })
                .addOnFailureListener(e -> {
                    Log.e("PendingOrderActivity", "Error updating order", e);
                });
    }

    @Override
    public void onDecline(Order order, int position) {
        FirebaseFirestore.getInstance().collection("orders")
                .document(order.getOrderId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    orderList.remove(position);
                    ratingAdapter.notifyItemRemoved(position);
                    Log.d("PendingOrderActivity", "Order declined and deleted.");
                })
                .addOnFailureListener(e -> {
                    Log.e("PendingOrderActivity", "Error deleting order", e);
                });
    }
}
