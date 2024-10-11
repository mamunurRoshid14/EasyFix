package com.example.easyfix;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserAccount> userList;

    public UserAdapter(List<UserAccount> userList) {
        this.userList = userList;
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
        holder.tvUserServiceType.setText(user.getTypeofService()); // Populate service type
        holder.tvUserPhone.setText(user.getPhoneNumber());
        holder.tvUserLocation.setText(user.getLocation()); // Populate location
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserAge, tvUserServiceType, tvUserPhone, tvUserLocation;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserAge = itemView.findViewById(R.id.tvUserAge);
            tvUserServiceType = itemView.findViewById(R.id.tvUserServiceType);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvUserLocation = itemView.findViewById(R.id.tvUserLocation);
        }
    }
}
