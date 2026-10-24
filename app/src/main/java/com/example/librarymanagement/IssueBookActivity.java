package com.example.librarymanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IssueBookActivity extends AppCompatActivity {
    private Spinner spinnerMembers;
    private Spinner spinnerBooks;
    private TextInputLayout tilDueDate;
    private TextInputEditText etIssueDate;
    private TextInputEditText etDueDate;
    private MaterialButton btnSubmit;

    private DatabaseHelper dbHelper;
    private List<Member> memberList = new ArrayList<>();
    private List<Book> bookList = new ArrayList<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DatabaseHelper.DATE_FORMAT, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_book);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupToolbar();
        setupSpinners();
        setupDatePickers();

        btnSubmit.setOnClickListener(v -> handleIssueBook());
    }

    private void initViews() {
        spinnerMembers = findViewById(R.id.spinnerMembers);
        spinnerBooks = findViewById(R.id.spinnerBooks);
        tilDueDate = findViewById(R.id.tilDueDate);
        etIssueDate = findViewById(R.id.etIssueDate);
        etDueDate = findViewById(R.id.etDueDate);
        btnSubmit = findViewById(R.id.btnSubmitIssue);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarIssueBook);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        memberList = dbHelper.getActiveMembers();
        List<String> memberNames = new ArrayList<>();
        if (memberList.isEmpty()) {
            memberNames.add("No active members found");
        } else {
            for (Member m : memberList) {
                memberNames.add(m.getFullName() + " (" + m.getMembershipId() + ")");
            }
        }
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, memberNames);
        spinnerMembers.setAdapter(memberAdapter);

        bookList = dbHelper.getAvailableBooks();
        List<String> bookTitles = new ArrayList<>();
        if (bookList.isEmpty()) {
            bookTitles.add("No books currently available");
        } else {
            for (Book b : bookList) {
                bookTitles.add(b.getTitle() + " [Avail: " + b.getAvailableQuantity() + "]");
            }
        }
        ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, bookTitles);
        spinnerBooks.setAdapter(bookAdapter);
    }

    private void setupDatePickers() {
        Calendar calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());
        etIssueDate.setText(today);

        calendar.add(Calendar.DAY_OF_YEAR, 14);
        String defaultDueDate = dateFormat.format(calendar.getTime());
        etDueDate.setText(defaultDueDate);

        etIssueDate.setOnClickListener(v -> showDatePicker(etIssueDate));
        etDueDate.setOnClickListener(v -> showDatePicker(etDueDate));
    }

    private void showDatePicker(TextInputEditText targetField) {
        Calendar cal = Calendar.getInstance();
        try {
            String currentVal = targetField.getText() != null ? targetField.getText().toString() : "";
            if (!currentVal.isEmpty()) {
                cal.setTime(dateFormat.parse(currentVal));
            }
        } catch (ParseException ignored) {}

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth);
                    targetField.setText(dateFormat.format(picked.getTime()));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void handleIssueBook() {
        tilDueDate.setError(null);

        if (memberList.isEmpty()) {
            Toast.makeText(this, "Please add an active member first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bookList.isEmpty()) {
            Toast.makeText(this, R.string.err_no_copies_available, Toast.LENGTH_SHORT).show();
            return;
        }

        int memberPos = spinnerMembers.getSelectedItemPosition();
        int bookPos = spinnerBooks.getSelectedItemPosition();

        if (memberPos < 0 || memberPos >= memberList.size()) {
            Toast.makeText(this, R.string.err_select_member, Toast.LENGTH_SHORT).show();
            return;
        }

        if (bookPos < 0 || bookPos >= bookList.size()) {
            Toast.makeText(this, R.string.err_select_book, Toast.LENGTH_SHORT).show();
            return;
        }

        Member member = memberList.get(memberPos);
        Book book = bookList.get(bookPos);

        if (book.getAvailableQuantity() <= 0) {
            Toast.makeText(this, R.string.err_no_copies_available, Toast.LENGTH_SHORT).show();
            return;
        }

        String issueDate = etIssueDate.getText() != null ? etIssueDate.getText().toString().trim() : "";
        String dueDate = etDueDate.getText() != null ? etDueDate.getText().toString().trim() : "";

        try {
            Date issue = dateFormat.parse(issueDate);
            Date due = dateFormat.parse(dueDate);
            if (issue != null && due != null && due.before(issue)) {
                tilDueDate.setError(getString(R.string.err_invalid_due_date));
                return;
            }
        } catch (ParseException e) {
            tilDueDate.setError(getString(R.string.err_invalid_due_date));
            return;
        }

        btnSubmit.setEnabled(false);

        boolean success = dbHelper.issueBook(book.getId(), member.getId(), issueDate, dueDate);
        if (success) {
            Toast.makeText(this, R.string.msg_book_issued, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            btnSubmit.setEnabled(true);
            Toast.makeText(this, "Failed to issue book. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
