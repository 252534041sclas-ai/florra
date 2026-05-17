package com.example.florra_a.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.florra_a.R;
import com.example.florra_a.models.StaffMember;
import java.util.ArrayList;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    private Context context;
    private List<StaffMember> staffList;
    private List<StaffMember> staffListFull; // For filtering
    private OnStaffActionListener listener;

    public interface OnStaffActionListener {
        void onEdit(StaffMember staff);
        void onDelete(StaffMember staff);
    }

    public StaffAdapter(Context context, List<StaffMember> staffList, OnStaffActionListener listener) {
        this.context = context;
        this.staffList = staffList;
        this.staffListFull = new ArrayList<>(staffList);
        this.listener = listener;
    }

    public void updateData(List<StaffMember> newStaffList) {
        this.staffList = newStaffList;
        this.staffListFull = new ArrayList<>(newStaffList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        List<StaffMember> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        if (lowerQuery.isEmpty()) {
            filteredList.addAll(staffListFull);
        } else {
            for (StaffMember member : staffListFull) {
                if ((member.getFullName() != null && member.getFullName().toLowerCase().contains(lowerQuery)) ||
                    (member.getEmail() != null && member.getEmail().toLowerCase().contains(lowerQuery))) {
                    filteredList.add(member);
                }
            }
        }
        this.staffList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StaffMember staff = staffList.get(position);

        holder.tvStaffName.setText(staff.getFullName());
        holder.tvStaffEmail.setText(staff.getEmail());

        String role = staff.getRole();
        if (role == null) role = "staff";
        
        // Style role badge
        holder.tvStaffRole.setText(role.substring(0, 1).toUpperCase() + role.substring(1));
        if ("admin".equalsIgnoreCase(role)) {
            holder.tvStaffRole.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvStaffRole.setBackgroundResource(R.drawable.bg_admin_badge);
        } else {
            holder.tvStaffRole.setTextColor(context.getResources().getColor(R.color.slate_600));
            holder.tvStaffRole.setBackgroundResource(R.drawable.bg_tag);
        }

        // Dynamic Avatar loading using Glide (initials avatar)
        String avatarUrl = "https://ui-avatars.com/api/?name=" + staff.getFullName() + "&background=random&size=128";
        Glide.with(context)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(holder.ivStaffAvatar);

        holder.btnEditStaff.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(staff);
            }
        });

        holder.btnDeleteStaff.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(staff);
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStaffAvatar;
        TextView tvStaffName;
        TextView tvStaffEmail;
        TextView tvStaffRole;
        ImageButton btnEditStaff;
        ImageButton btnDeleteStaff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStaffAvatar = itemView.findViewById(R.id.ivStaffAvatar);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);
            tvStaffEmail = itemView.findViewById(R.id.tvStaffEmail);
            tvStaffRole = itemView.findViewById(R.id.tvStaffRole);
            btnEditStaff = itemView.findViewById(R.id.btnEditStaff);
            btnDeleteStaff = itemView.findViewById(R.id.btnDeleteStaff);
        }
    }
}
