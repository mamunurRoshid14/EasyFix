package com.example.easyfix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Order> orderList;

    public ReviewAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.ratingBar.setIsIndicator(true);
        holder.ratingBar.setClickable(false);
        holder.ratingBar.setFocusable(false);

        // Fetch name from Firestore users collection
        FirebaseFirestore.getInstance().collection("users")
                .document(order.getOrderTo())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        holder.tvName.setText(name != null ? name : "Name not available");
                    } else {
                        holder.tvName.setText("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.tvName.setText("Error fetching user");
                });

        // Set Service Type, Rating, and Review
        holder.tvServiceType.setText(order.getServiceType() != null ? order.getServiceType() : "Unknown service");
        holder.ratingBar.setRating(order.getRating() > 0 ? (float) order.getRating() : 0);
        holder.tvReview.setText(order.getReview() != null ? order.getReview() : "No review provided");
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvServiceType, tvReview;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvServiceType = itemView.findViewById(R.id.tv_service_type);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            tvReview = itemView.findViewById(R.id.tv_review);
        }
    }
}
