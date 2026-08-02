package com.example.librarymanagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/**
 * Activity for displaying active loans and processing book returns with automatic fine calculation.
 */
public class ReturnBookActivity extends AppCompatActivity implements TransactionAdapter.OnTransactionClickListener {

    private EditText etSearch;
    private ImageView ivClearSearch;
    private RecyclerView rvLoans;
    private LinearLayout layoutEmpty;
    private TransactionAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_book);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveLoans(etSearch.getText().toString());
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearchLoans);
        ivClearSearch = findViewById(R.id.ivClearSearchLoans);
        rvLoans = findViewById(R.id.rvActiveLoans);
        layoutEmpty = findViewById(R.id.layoutEmptyLoans);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarReturnBook);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(this, true, this);
        rvLoans.setLayoutManager(new LinearLayoutManager(this));
        rvLoans.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                ivClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                loadActiveLoans(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void loadActiveLoans(String query) {
        List<LibraryTransaction> loans = dbHelper.searchTransactions(query, "ISSUED");
        adapter.setTransactions(loans);
        if (loans.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvLoans.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvLoans.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReturnClick(LibraryTransaction transaction) {
        String today = DatabaseHelper.getTodayDate();
        double fine = DatabaseHelper.calculateFine(transaction.getDueDate(), today);

        String message = getString(R.string.confirm_return_message,
                transaction.getBookTitle(),
                transaction.getMemberFullName(),
                fine);

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_return_title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_confirm, (dialog, which) -> {
                    boolean success = dbHelper.returnBook(transaction.getId(), today);
                    if (success) {
                        String successMsg = getString(R.string.msg_book_returned, fine);
                        Toast.makeText(this, successMsg, Toast.LENGTH_LONG).show();
                        loadActiveLoans(etSearch.getText().toString());
                    } else {
                        Toast.makeText(this, "Failed to process return. Transaction might already be closed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
