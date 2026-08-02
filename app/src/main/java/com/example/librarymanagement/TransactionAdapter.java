package com.example.librarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying loan and return transactions.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionClickListener {
        void onReturnClick(LibraryTransaction transaction);
    }

    private final Context context;
    private final List<LibraryTransaction> transactions = new ArrayList<>();
    private final OnTransactionClickListener returnClickListener;
    private final boolean showReturnButton;

    public TransactionAdapter(Context context, boolean showReturnButton, OnTransactionClickListener returnClickListener) {
        this.context = context;
        this.showReturnButton = showReturnButton;
        this.returnClickListener = returnClickListener;
    }

    public void setTransactions(List<LibraryTransaction> newTransactions) {
        this.transactions.clear();
        if (newTransactions != null) {
            this.transactions.addAll(newTransactions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        LibraryTransaction t = transactions.get(position);

        holder.tvTransId.setText("TXN #" + t.getId());
        holder.tvBookTitle.setText(t.getBookTitle());

        String memberDesc = "Borrower: " + t.getMemberFullName();
        if (t.getMemberMembershipId() != null && !t.getMemberMembershipId().isEmpty()) {
            memberDesc += " (" + t.getMemberMembershipId() + ")";
        }
        holder.tvMemberName.setText(memberDesc);

        holder.tvIssueDate.setText(t.getIssueDate());
        holder.tvDueDate.setText(t.getDueDate());

        String today = DatabaseHelper.getTodayDate();
        boolean isReturned = t.isReturned();

        if (isReturned) {
            holder.tvStatus.setText("RETURNED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_returned);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_returned));

            holder.layoutReturnDate.setVisibility(View.VISIBLE);
            holder.tvReturnDate.setText(t.getReturnDate() != null ? t.getReturnDate() : "-");

            if (t.getFine() > 0) {
                holder.layoutFooter.setVisibility(View.VISIBLE);
                holder.tvFine.setVisibility(View.VISIBLE);
                holder.tvFine.setText(String.format(context.getString(R.string.overdue_fine_prefix), t.getFine()));
            } else {
                holder.tvFine.setVisibility(View.GONE);
                holder.layoutFooter.setVisibility(View.GONE);
            }
            holder.btnReturn.setVisibility(View.GONE);
        } else {
            // Not returned yet - check if overdue
            long overdueDays = DatabaseHelper.calculateOverdueDays(t.getDueDate(), today);
            if (overdueDays > 0) {
                holder.tvStatus.setText("OVERDUE (" + overdueDays + "d)");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_overdue);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_overdue));

                double estFine = overdueDays * DatabaseHelper.DAILY_FINE_RATE;
                holder.tvFine.setVisibility(View.VISIBLE);
                holder.tvFine.setText(String.format("Est. Fine: NPR %.2f", estFine));
            } else {
                holder.tvStatus.setText("ISSUED");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_issued);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_issued));
                holder.tvFine.setVisibility(View.GONE);
            }

            holder.layoutReturnDate.setVisibility(View.GONE);

            if (showReturnButton) {
                holder.layoutFooter.setVisibility(View.VISIBLE);
                holder.btnReturn.setVisibility(View.VISIBLE);
                holder.btnReturn.setOnClickListener(v -> {
                    if (returnClickListener != null) {
                        returnClickListener.onReturnClick(t);
                    }
                });
            } else {
                holder.btnReturn.setVisibility(View.GONE);
                holder.layoutFooter.setVisibility(overdueDays > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransId, tvStatus, tvBookTitle, tvMemberName, tvIssueDate, tvDueDate, tvReturnDate, tvFine;
        LinearLayout layoutReturnDate, layoutFooter;
        MaterialButton btnReturn;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransId = itemView.findViewById(R.id.tvTransId);
            tvStatus = itemView.findViewById(R.id.tvTransStatus);
            tvBookTitle = itemView.findViewById(R.id.tvTransBookTitle);
            tvMemberName = itemView.findViewById(R.id.tvTransMemberName);
            tvIssueDate = itemView.findViewById(R.id.tvTransIssueDate);
            tvDueDate = itemView.findViewById(R.id.tvTransDueDate);
            tvReturnDate = itemView.findViewById(R.id.tvTransReturnDate);
            tvFine = itemView.findViewById(R.id.tvTransFine);
            layoutReturnDate = itemView.findViewById(R.id.layoutReturnDate);
            layoutFooter = itemView.findViewById(R.id.layoutTransFooter);
            btnReturn = itemView.findViewById(R.id.btnQuickReturn);
        }
    }
}
