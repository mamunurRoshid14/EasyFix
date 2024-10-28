package com.example.easyfix;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserAccount> userList;
    private OnItemClickListener onItemClickListener;

    public UserAdapter(List<UserAccount> userList, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserAccount user = userList.get(position);
        holder.tvUserName.setText(user.getFullName());
        holder.tvUserAge.setText(String.valueOf(user.getAge()));
        holder.tvUserServiceType.setText(user.getTypeofService());
        holder.tvUserPhone.setText(user.getPhoneNumber());
        holder.tvUserLocation.setText(user.getLocation());
        holder.ratingBar.setRating((float)(user.getRating()/2.0));
        holder.bind(userList.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserAge, tvUserServiceType, tvUserPhone, tvUserLocation;
        RatingBar ratingBar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserAge = itemView.findViewById(R.id.tvUserAge);
            tvUserServiceType = itemView.findViewById(R.id.tvUserServiceType);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvUserLocation = itemView.findViewById(R.id.tvUserLocation);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bind(final UserAccount user, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserAccount user);
    }
}
