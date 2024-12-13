package com.example.easyfix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ReviewViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderActionListener orderActionListener;

    public interface OnOrderActionListener {
        void onAccept(Order order, int position);
        void onDecline(Order order, int position);
    }

    public PendingOrderAdapter(Context context, List<Order> orderList, OnOrderActionListener orderActionListener) {
        this.context = context;
        this.orderList = orderList;
        this.orderActionListener = orderActionListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_order, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Fetch name and location from Firestore users collection
        FirebaseFirestore.getInstance().collection("users")
                .document(order.getOrderFrom())  // Assuming 'orderFrom' holds the user document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        holder.tvName.setText(name != null ? name : "Name not available");
                        String location = documentSnapshot.getString("location");
                        holder.tvLocation.setText(location != null ? location : "Not Available");
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        holder.tvMobile.setText(phoneNumber != null ? phoneNumber : "Not Available");

                        double latitude = documentSnapshot.getDouble("latitude");
                        double longitude = documentSnapshot.getDouble("longitude");

                        // Get Directions button
                        holder.btnGetDirection.setOnClickListener(v -> {
                            String uri = String.format("google.navigation:q=%f,%f", latitude, longitude);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            context.startActivity(intent);
                        });
                    } else {
                        holder.tvName.setText("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.tvName.setText("Error fetching user");
                });

        // Pass click events to the activity via callback
        holder.btnAccept.setOnClickListener(v -> {
            if (orderActionListener != null) {
                orderActionListener.onAccept(order, position);
            }
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (orderActionListener != null) {
                orderActionListener.onDecline(order, position);
            }
        });

        holder.tvMobile.setOnClickListener(view -> {
            String phone = holder.tvMobile.getText().toString();
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            context.startActivity(phoneIntent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvMobile;
        Button btnAccept, btnDecline, btnGetDirection;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvMobile = itemView.findViewById(R.id.tv_mobile);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnDecline = itemView.findViewById(R.id.btn_decline);
            btnGetDirection = itemView.findViewById(R.id.btn_get_direction);
        }
    }
}