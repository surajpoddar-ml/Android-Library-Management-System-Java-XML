package com.example.librarymanagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageView ivClearSearch;
    private ChipGroup chipGroupFilter;
    private RecyclerView rvHistory;
    private LinearLayout layoutEmpty;

    private TransactionAdapter adapter;
    private DatabaseHelper dbHelper;
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupFilters();
        setupSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory(etSearch.getText().toString());
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearchHistory);
        ivClearSearch = findViewById(R.id.ivClearSearchHistory);
        chipGroupFilter = findViewById(R.id.chipGroupHistoryFilter);
        rvHistory = findViewById(R.id.rvHistory);
        layoutEmpty = findViewById(R.id.layoutEmptyHistory);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarHistory);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(this, false, null);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void setupFilters() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipFilterIssued) {
                currentFilter = "ISSUED";
            } else if (checkedId == R.id.chipFilterReturned) {
                currentFilter = "RETURNED";
            } else if (checkedId == R.id.chipFilterOverdue) {
                currentFilter = "OVERDUE";
            } else {
                currentFilter = "ALL";
            }
            loadHistory(etSearch.getText().toString());
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                ivClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                loadHistory(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void loadHistory(String query) {
        List<LibraryTransaction> list = dbHelper.searchTransactions(query, currentFilter);
        adapter.setTransactions(list);
        if (list.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }
    }
}
