package com.example.quickcommercedemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.User;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onToggleBan(User user);
    }

    public UserManagementAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void updateList(List<User> newList) {
        this.users = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvEmail, tvBannedBadge;
        private MaterialButton btnEdit, btnBan;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminUserName);
            tvEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            tvBannedBadge = itemView.findViewById(R.id.tvBannedBadge);
            btnEdit = itemView.findViewById(R.id.btnAdminEditUser);
            btnBan = itemView.findViewById(R.id.btnAdminBanUser);
        }

        public void bind(User user, OnUserActionListener listener) {
            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail());
            
            tvBannedBadge.setVisibility(user.isBanned() ? View.VISIBLE : View.GONE);
            btnBan.setText(user.isBanned() ? "Unban" : "Ban");
            
            btnEdit.setOnClickListener(v -> listener.onEditUser(user));
            btnBan.setOnClickListener(v -> listener.onToggleBan(user));
        }
    }
}
