package com.example.librarymanagement;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditMemberActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextInputLayout tilName, tilMembershipId, tilEmail, tilPhone, tilAddress;
    private TextInputEditText etName, etMembershipId, etEmail, etPhone, etAddress;
    private RadioGroup rgStatus;
    private RadioButton rbActive, rbInactive;
    private MaterialButton btnSave;

    private DatabaseHelper dbHelper;
    private Member existingMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_member);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupToolbar();

        if (getIntent().hasExtra(MembersActivity.EXTRA_MEMBER)) {
            existingMember = (Member) getIntent().getSerializableExtra(MembersActivity.EXTRA_MEMBER);
        }

        if (existingMember != null) {
            setupEditMode();
        } else {
            toolbar.setTitle(R.string.title_add_member);
        }

        btnSave.setOnClickListener(v -> saveMember());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarAddEditMember);
        tilName = findViewById(R.id.tilMemberName);
        tilMembershipId = findViewById(R.id.tilMembershipId);
        tilEmail = findViewById(R.id.tilMemberEmail);
        tilPhone = findViewById(R.id.tilMemberPhone);
        tilAddress = findViewById(R.id.tilMemberAddress);

        etName = findViewById(R.id.etMemberName);
        etMembershipId = findViewById(R.id.etMembershipId);
        etEmail = findViewById(R.id.etMemberEmail);
        etPhone = findViewById(R.id.etMemberPhone);
        etAddress = findViewById(R.id.etMemberAddress);

        rgStatus = findViewById(R.id.rgMemberStatus);
        rbActive = findViewById(R.id.rbStatusActive);
        rbInactive = findViewById(R.id.rbStatusInactive);
        btnSave = findViewById(R.id.btnSaveMember);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupEditMode() {
        toolbar.setTitle(R.string.title_edit_member);

        etName.setText(existingMember.getFullName());
        etMembershipId.setText(existingMember.getMembershipId());
        etEmail.setText(existingMember.getEmail());
        etPhone.setText(existingMember.getPhone());
        etAddress.setText(existingMember.getAddress());

        if (existingMember.isActive()) {
            rbActive.setChecked(true);
        } else {
            rbInactive.setChecked(true);
        }
    }

    private void saveMember() {
        tilName.setError(null);
        tilMembershipId.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String membershipId = etMembershipId.getText() != null ? etMembershipId.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String status = rbActive.isChecked() ? "Active" : "Inactive";

        boolean hasError = false;

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.err_required));
            hasError = true;
        }

        if (membershipId.isEmpty()) {
            tilMembershipId.setError(getString(R.string.err_required));
            hasError = true;
        } else {
            long excludeId = existingMember != null ? existingMember.getId() : -1;
            if (dbHelper.isMembershipIdExists(membershipId, excludeId)) {
                tilMembershipId.setError(getString(R.string.err_duplicate_member_id));
                hasError = true;
            }
        }

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            hasError = true;
        }

        if (!phone.isEmpty()) {
            String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
            if (!cleanPhone.matches("^(\\+91|0)?[6-9]\\d{9}$")) {
                tilPhone.setError(getString(R.string.err_invalid_phone));
                hasError = true;
            }
        }

        if (hasError) {
            return;
        }

        if (existingMember != null) {
            existingMember.setFullName(name);
            existingMember.setMembershipId(membershipId);
            existingMember.setEmail(email);
            existingMember.setPhone(phone);
            existingMember.setAddress(address);
            existingMember.setStatus(status);

            int rows = dbHelper.updateMember(existingMember);
            if (rows > 0) {
                Toast.makeText(this, R.string.msg_member_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update member", Toast.LENGTH_SHORT).show();
            }
        } else {
            String today = DatabaseHelper.getTodayDate();
            Member newMember = new Member(name, membershipId, email, phone, address, today, status);
            long id = dbHelper.insertMember(newMember);
            if (id != -1) {
                Toast.makeText(this, R.string.msg_member_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to register member", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
