package com.example.librarymanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditBookActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextInputLayout tilTitle, tilAuthor, tilIsbn, tilCategory, tilPublisher, tilYear, tilQuantity, tilDescription;
    private TextInputEditText etTitle, etAuthor, etIsbn, etCategory, etPublisher, etYear, etQuantity, etDescription;
    private TextView tvLoanConstraintNotice;
    private MaterialButton btnSave;

    private DatabaseHelper dbHelper;
    private Book existingBook;
    private int activeLoans = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupToolbar();

        if (getIntent().hasExtra(BooksActivity.EXTRA_BOOK)) {
            existingBook = (Book) getIntent().getSerializableExtra(BooksActivity.EXTRA_BOOK);
        }

        if (existingBook != null) {
            setupEditMode();
        } else {
            toolbar.setTitle(R.string.title_add_book);
        }

        btnSave.setOnClickListener(v -> saveBook());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarAddEditBook);
        tilTitle = findViewById(R.id.tilBookTitle);
        tilAuthor = findViewById(R.id.tilBookAuthor);
        tilIsbn = findViewById(R.id.tilBookIsbn);
        tilCategory = findViewById(R.id.tilBookCategory);
        tilPublisher = findViewById(R.id.tilBookPublisher);
        tilYear = findViewById(R.id.tilBookYear);
        tilQuantity = findViewById(R.id.tilBookQuantity);
        tilDescription = findViewById(R.id.tilBookDescription);

        etTitle = findViewById(R.id.etBookTitle);
        etAuthor = findViewById(R.id.etBookAuthor);
        etIsbn = findViewById(R.id.etBookIsbn);
        etCategory = findViewById(R.id.etBookCategory);
        etPublisher = findViewById(R.id.etBookPublisher);
        etYear = findViewById(R.id.etBookYear);
        etQuantity = findViewById(R.id.etBookQuantity);
        etDescription = findViewById(R.id.etBookDescription);

        tvLoanConstraintNotice = findViewById(R.id.tvLoanConstraintNotice);
        btnSave = findViewById(R.id.btnSaveBook);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupEditMode() {
        toolbar.setTitle(R.string.title_edit_book);

        etTitle.setText(existingBook.getTitle());
        etAuthor.setText(existingBook.getAuthor());
        etIsbn.setText(existingBook.getIsbn());
        etCategory.setText(existingBook.getCategory());
        etPublisher.setText(existingBook.getPublisher());
        if (existingBook.getPublicationYear() > 0) {
            etYear.setText(String.valueOf(existingBook.getPublicationYear()));
        }
        etQuantity.setText(String.valueOf(existingBook.getQuantity()));
        etDescription.setText(existingBook.getDescription());

        activeLoans = dbHelper.getActiveLoansForBook(existingBook.getId());
        if (activeLoans > 0) {
            tvLoanConstraintNotice.setVisibility(View.VISIBLE);
            tvLoanConstraintNotice.setText(getString(R.string.err_quantity_less_than_issued, activeLoans));
        }
    }

    private void saveBook() {
        tilTitle.setError(null);
        tilAuthor.setError(null);
        tilIsbn.setError(null);
        tilCategory.setError(null);
        tilQuantity.setError(null);

        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String author = etAuthor.getText() != null ? etAuthor.getText().toString().trim() : "";
        String isbn = etIsbn.getText() != null ? etIsbn.getText().toString().trim() : "";
        String category = etCategory.getText() != null ? etCategory.getText().toString().trim() : "";
        String publisher = etPublisher.getText() != null ? etPublisher.getText().toString().trim() : "";
        String yearStr = etYear.getText() != null ? etYear.getText().toString().trim() : "";
        String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        boolean hasError = false;

        if (title.isEmpty()) {
            tilTitle.setError(getString(R.string.err_required));
            hasError = true;
        }

        if (author.isEmpty()) {
            tilAuthor.setError(getString(R.string.err_required));
            hasError = true;
        }

        if (isbn.isEmpty()) {
            tilIsbn.setError(getString(R.string.err_required));
            hasError = true;
        } else {
            long excludeId = existingBook != null ? existingBook.getId() : -1;
            if (dbHelper.isIsbnExists(isbn, excludeId)) {
                tilIsbn.setError(getString(R.string.err_duplicate_isbn));
                hasError = true;
            }
        }

        if (category.isEmpty()) {
            tilCategory.setError(getString(R.string.err_required));
            hasError = true;
        }

        int quantity = 0;
        if (qtyStr.isEmpty()) {
            tilQuantity.setError(getString(R.string.err_required));
            hasError = true;
        } else {
            try {
                quantity = Integer.parseInt(qtyStr);
                if (quantity <= 0) {
                    tilQuantity.setError(getString(R.string.err_invalid_quantity));
                    hasError = true;
                } else if (existingBook != null && quantity < activeLoans) {
                    tilQuantity.setError(getString(R.string.err_quantity_less_than_issued, activeLoans));
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError(getString(R.string.err_invalid_quantity));
                hasError = true;
            }
        }

        int year = 0;
        if (!yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException ignored) {}
        }

        if (hasError) {
            return;
        }

        if (existingBook != null) {
            int newAvailable = quantity - activeLoans;
            existingBook.setTitle(title);
            existingBook.setAuthor(author);
            existingBook.setIsbn(isbn);
            existingBook.setCategory(category);
            existingBook.setPublisher(publisher);
            existingBook.setPublicationYear(year);
            existingBook.setQuantity(quantity);
            existingBook.setAvailableQuantity(newAvailable);
            existingBook.setDescription(description);

            int rows = dbHelper.updateBook(existingBook);
            if (rows > 0) {
                Toast.makeText(this, R.string.msg_book_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update book", Toast.LENGTH_SHORT).show();
            }
        } else {
            Book newBook = new Book(title, author, isbn, category, publisher, year, quantity, quantity, description);
            long id = dbHelper.insertBook(newBook);
            if (id != -1) {
                Toast.makeText(this, R.string.msg_book_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add book", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
