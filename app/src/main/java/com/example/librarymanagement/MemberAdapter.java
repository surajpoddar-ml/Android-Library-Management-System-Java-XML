package com.example.librarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    public interface OnMemberClickListener {
        void onEditClick(Member member);
        void onDeleteClick(Member member);
    }

    private final Context context;
    private final List<Member> members = new ArrayList<>();
    private final OnMemberClickListener listener;
    private final DatabaseHelper dbHelper;

    public MemberAdapter(Context context, OnMemberClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public void setMembers(List<Member> newMembers) {
        this.members.clear();
        if (newMembers != null) {
            this.members.addAll(newMembers);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = members.get(position);
        holder.tvName.setText(member.getFullName());
        holder.tvMembershipId.setText("ID: " + member.getMembershipId());

        String name = member.getFullName().trim();
        if (!name.isEmpty()) {
            holder.tvAvatarInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        } else {
            holder.tvAvatarInitial.setText("M");
        }

        if (member.getPhone() != null && !member.getPhone().isEmpty()) {
            holder.tvPhone.setVisibility(View.VISIBLE);
            holder.tvPhone.setText("Phone: " + member.getPhone());
        } else {
            holder.tvPhone.setVisibility(View.GONE);
        }

        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            holder.tvEmail.setVisibility(View.VISIBLE);
            holder.tvEmail.setText("Email: " + member.getEmail());
        } else {
            holder.tvEmail.setVisibility(View.GONE);
        }

        boolean isActive = member.isActive();
        holder.tvStatus.setText(member.getStatus());
        if (isActive) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_success));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_inactive);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }

        int activeLoans = dbHelper.getActiveLoansForMember(member.getId());
        holder.tvActiveLoans.setText("Active loans: " + activeLoans);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(member);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(member);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarInitial, tvName, tvMembershipId, tvPhone, tvEmail, tvStatus, tvActiveLoans;
        MaterialButton btnEdit, btnDelete;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarInitial = itemView.findViewById(R.id.tvAvatarInitial);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvMembershipId = itemView.findViewById(R.id.tvMembershipId);
            tvPhone = itemView.findViewById(R.id.tvMemberPhone);
            tvEmail = itemView.findViewById(R.id.tvMemberEmail);
            tvStatus = itemView.findViewById(R.id.tvMemberStatus);
            tvActiveLoans = itemView.findViewById(R.id.tvActiveLoansMember);
            btnEdit = itemView.findViewById(R.id.btnEditMember);
            btnDelete = itemView.findViewById(R.id.btnDeleteMember);
        }
    }
}
