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

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    public interface OnBookClickListener {
        void onEditClick(Book book);
        void onDeleteClick(Book book);
    }

    private final Context context;
    private final List<Book> books = new ArrayList<>();
    private final OnBookClickListener listener;
    private final DatabaseHelper dbHelper;

    public BookAdapter(Context context, OnBookClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public void setBooks(List<Book> newBooks) {
        this.books.clear();
        if (newBooks != null) {
            this.books.addAll(newBooks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvCategory.setText(book.getCategory());

        String authorYear = "by " + book.getAuthor();
        if (book.getPublicationYear() > 0) {
            authorYear += " (" + book.getPublicationYear() + ")";
        }
        holder.tvAuthor.setText(authorYear);
        holder.tvIsbn.setText("ISBN: " + book.getIsbn());

        int available = book.getAvailableQuantity();
        int total = book.getQuantity();
        int onLoan = total - available;

        holder.tvStockStatus.setText("Available: " + available + " / " + total + " copies");
        if (available > 0) {
            holder.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.status_success));
        } else {
            holder.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.status_danger));
        }

        holder.tvActiveLoansInfo.setText(onLoan + " copy(s) currently on loan");

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(book);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvAuthor, tvIsbn, tvStockStatus, tvActiveLoansInfo;
        MaterialButton btnEdit, btnDelete;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvIsbn = itemView.findViewById(R.id.tvIsbn);
            tvStockStatus = itemView.findViewById(R.id.tvStockStatus);
            tvActiveLoansInfo = itemView.findViewById(R.id.tvActiveLoansInfo);
            btnEdit = itemView.findViewById(R.id.btnEditBook);
            btnDelete = itemView.findViewById(R.id.btnDeleteBook);
        }
    }
}
