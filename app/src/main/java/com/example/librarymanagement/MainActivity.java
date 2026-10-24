package com.example.librarymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private TextView tvStatTotalTitles;
    private TextView tvStatTotalCopies;
    private TextView tvStatAvailableCopies;
    private TextView tvStatIssuedCopies;
    private TextView tvStatTotalMembers;
    private TextView tvStatOverdueLoans;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatistics();
    }

    private void initViews() {
        tvStatTotalTitles = findViewById(R.id.tvStatTotalTitles);
        tvStatTotalCopies = findViewById(R.id.tvStatTotalCopies);
        tvStatAvailableCopies = findViewById(R.id.tvStatAvailableCopies);
        tvStatIssuedCopies = findViewById(R.id.tvStatIssuedCopies);
        tvStatTotalMembers = findViewById(R.id.tvStatTotalMembers);
        tvStatOverdueLoans = findViewById(R.id.tvStatOverdueLoans);
    }

    private void setupListeners() {
        MaterialButton btnManageBooks = findViewById(R.id.btnNavManageBooks);
        MaterialButton btnAddBook = findViewById(R.id.btnNavAddBook);
        MaterialButton btnManageMembers = findViewById(R.id.btnNavManageMembers);
        MaterialButton btnAddMember = findViewById(R.id.btnNavAddMember);
        MaterialButton btnIssueBook = findViewById(R.id.btnNavIssueBook);
        MaterialButton btnReturnBook = findViewById(R.id.btnNavReturnBook);
        MaterialButton btnHistory = findViewById(R.id.btnNavHistory);

        btnManageBooks.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BooksActivity.class)));

        btnAddBook.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditBookActivity.class)));

        btnManageMembers.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MembersActivity.class)));

        btnAddMember.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditMemberActivity.class)));

        btnIssueBook.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, IssueBookActivity.class)));

        btnReturnBook.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReturnBookActivity.class)));

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
    }

    private void refreshStatistics() {
        int totalTitles = dbHelper.getTotalBookTitles();
        int totalCopies = dbHelper.getTotalBookCopies();
        int availableCopies = dbHelper.getAvailableCopies();
        int issuedCopies = dbHelper.getIssuedCopies();
        int totalMembers = dbHelper.getTotalMembers();
        int overdueLoans = dbHelper.getOverdueLoansCount();

        tvStatTotalTitles.setText(String.valueOf(totalTitles));
        tvStatTotalCopies.setText(String.valueOf(totalCopies));
        tvStatAvailableCopies.setText(String.valueOf(availableCopies));
        tvStatIssuedCopies.setText(String.valueOf(issuedCopies));
        tvStatTotalMembers.setText(String.valueOf(totalMembers));
        tvStatOverdueLoans.setText(String.valueOf(overdueLoans));
    }
}
