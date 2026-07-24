package com.example.librarymanagement;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

/**
 * Activity for displaying, searching, and managing books in the library.
 */
public class BooksActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {

    public static final String EXTRA_BOOK = "extra_book";

    private EditText etSearch;
    private ImageView ivClearSearch;
    private RecyclerView rvBooks;
    private LinearLayout layoutEmpty;
    private BookAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks(etSearch.getText().toString());
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearchBooks);
        ivClearSearch = findViewById(R.id.ivClearSearchBooks);
        rvBooks = findViewById(R.id.rvBooks);
        layoutEmpty = findViewById(R.id.layoutEmptyBooks);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarBooks);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(this, this);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                ivClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                loadBooks(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void setupFab() {
        ExtendedFloatingActionButton fab = findViewById(R.id.fabAddBook);
        fab.setOnClickListener(v ->
                startActivity(new Intent(BooksActivity.this, AddEditBookActivity.class)));
    }

    private void loadBooks(String query) {
        List<Book> books = dbHelper.searchBooks(query);
        adapter.setBooks(books);
        if (books.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvBooks.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvBooks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditClick(Book book) {
        Intent intent = new Intent(this, AddEditBookActivity.class);
        intent.putExtra(EXTRA_BOOK, book);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Book book) {
        int activeLoans = dbHelper.getActiveLoansForBook(book.getId());
        if (activeLoans > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Book")
                    .setMessage(getString(R.string.err_cannot_delete_book_active_loans, activeLoans))
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_book) + "\n\n\"" + book.getTitle() + "\"")
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    boolean deleted = dbHelper.deleteBook(book.getId());
                    if (deleted) {
                        Toast.makeText(this, R.string.msg_book_deleted, Toast.LENGTH_SHORT).show();
                        loadBooks(etSearch.getText().toString());
                    } else {
                        Toast.makeText(this, "Failed to delete book", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
