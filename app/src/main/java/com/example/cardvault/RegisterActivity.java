package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * RegisterActivity allows creating a new simulated user account locally.
 * Credentials and profile information are stored safely using SHA-256 hash digests.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName;
    private TextInputEditText etFullName;
    private TextInputLayout tilRegisterEmail;
    private TextInputEditText etRegisterEmail;
    private TextInputLayout tilPhone;
    private TextInputEditText etPhone;
    private TextInputLayout tilRegisterPassword;
    private TextInputEditText etRegisterPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvToLogin;

    private CardVaultAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = CardVaultAuthManager.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        tilRegisterEmail = findViewById(R.id.tilRegisterEmail);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        tilPhone = findViewById(R.id.tilPhone);
        etPhone = findViewById(R.id.etPhone);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvToLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());

        tvToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        tilFullName.setError(null);
        tilRegisterEmail.setError(null);
        tilPhone.setError(null);
        tilRegisterPassword.setError(null);
        tilConfirmPassword.setError(null);

        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etRegisterEmail.getText() != null ? etRegisterEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etRegisterPassword.getText() != null ? etRegisterPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        boolean cancel = false;

        // Confirm Password validation
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.err_empty_password));
            tilConfirmPassword.requestFocus();
            cancel = true;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError(getString(R.string.err_password_mismatch));
            tilConfirmPassword.requestFocus();
            cancel = true;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            tilRegisterPassword.setError(getString(R.string.err_empty_password));
            tilRegisterPassword.requestFocus();
            cancel = true;
        } else if (password.length() < 6) {
            tilRegisterPassword.setError(getString(R.string.err_short_password));
            tilRegisterPassword.requestFocus();
            cancel = true;
        }

        // Email validation
        if (TextUtils.isEmpty(email)) {
            tilRegisterEmail.setError(getString(R.string.err_empty_identifier));
            tilRegisterEmail.requestFocus();
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilRegisterEmail.setError(getString(R.string.err_invalid_email));
            tilRegisterEmail.requestFocus();
            cancel = true;
        }

        // Full Name validation
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError(getString(R.string.err_empty_name));
            tilFullName.requestFocus();
            cancel = true;
        }

        if (cancel) {
            return;
        }

        // Register user locally using SHA-256
        authManager.registerUser(fullName, email, phone, password);

        Toast.makeText(this, getString(R.string.register_success_msg), Toast.LENGTH_LONG).show();

        // Navigate back to LoginActivity with prefilled email
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_PREFILL_EMAIL, email);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
