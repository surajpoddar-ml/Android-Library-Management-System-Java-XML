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
 * Activity for displaying, searching, and managing members.
 */
public class MembersActivity extends AppCompatActivity implements MemberAdapter.OnMemberClickListener {

    public static final String EXTRA_MEMBER = "extra_member";

    private EditText etSearch;
    private ImageView ivClearSearch;
    private RecyclerView rvMembers;
    private LinearLayout layoutEmpty;
    private MemberAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

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
        loadMembers(etSearch.getText().toString());
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearchMembers);
        ivClearSearch = findViewById(R.id.ivClearSearchMembers);
        rvMembers = findViewById(R.id.rvMembers);
        layoutEmpty = findViewById(R.id.layoutEmptyMembers);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarMembers);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new MemberAdapter(this, this);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                ivClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                loadMembers(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void setupFab() {
        ExtendedFloatingActionButton fab = findViewById(R.id.fabAddMember);
        fab.setOnClickListener(v ->
                startActivity(new Intent(MembersActivity.this, AddEditMemberActivity.class)));
    }

    private void loadMembers(String query) {
        List<Member> members = dbHelper.searchMembers(query);
        adapter.setMembers(members);
        if (members.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvMembers.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvMembers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditClick(Member member) {
        Intent intent = new Intent(this, AddEditMemberActivity.class);
        intent.putExtra(EXTRA_MEMBER, member);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Member member) {
        int activeLoans = dbHelper.getActiveLoansForMember(member.getId());
        if (activeLoans > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Member")
                    .setMessage(getString(R.string.err_cannot_delete_member_active_loans, activeLoans))
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_member) + "\n\n\"" + member.getFullName() + "\" (" + member.getMembershipId() + ")")
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    boolean deleted = dbHelper.deleteMember(member.getId());
                    if (deleted) {
                        Toast.makeText(this, R.string.msg_member_deleted, Toast.LENGTH_SHORT).show();
                        loadMembers(etSearch.getText().toString());
                    } else {
                        Toast.makeText(this, "Failed to delete member", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
