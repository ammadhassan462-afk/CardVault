package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * ResetPasswordActivity allows resetting the password for a locally recovered account.
 * Replaces old hash with a new SHA-256 password hash.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private ImageButton btnResetBack;
    private TextView tvTargetEmail;
    private TextInputLayout tilNewPassword;
    private TextInputEditText etNewPassword;
    private TextInputLayout tilConfirmNewPassword;
    private TextInputEditText etConfirmNewPassword;
    private Button btnResetSubmit;
    private TextView tvResetToLogin;

    private CardVaultAuthManager authManager;
    private String targetEmail = CardVaultAuthManager.DEFAULT_EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        authManager = CardVaultAuthManager.getInstance(this);

        initViews();
        handleIntentData();
        setupListeners();
    }

    private void initViews() {
        btnResetBack = findViewById(R.id.btnResetBack);
        tvTargetEmail = findViewById(R.id.tvTargetEmail);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        tilConfirmNewPassword = findViewById(R.id.tilConfirmNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnResetSubmit = findViewById(R.id.btnResetSubmit);
        tvResetToLogin = findViewById(R.id.tvResetToLogin);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ForgotPasswordActivity.EXTRA_RESET_EMAIL)) {
            String email = intent.getStringExtra(ForgotPasswordActivity.EXTRA_RESET_EMAIL);
            if (email != null && !email.isEmpty()) {
                targetEmail = email;
            }
        }
        tvTargetEmail.setText(getString(R.string.settings_logged_in_as) + " " + targetEmail);
    }

    private void setupListeners() {
        btnResetBack.setOnClickListener(v -> finish());
        tvResetToLogin.setOnClickListener(v -> finish());

        btnResetSubmit.setOnClickListener(v -> attemptReset());
    }

    private void attemptReset() {
        tilNewPassword.setError(null);
        tilConfirmNewPassword.setError(null);

        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";
        String confirmPassword = etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString() : "";

        boolean cancel = false;

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmNewPassword.setError(getString(R.string.err_empty_password));
            tilConfirmNewPassword.requestFocus();
            cancel = true;
        } else if (!confirmPassword.equals(newPassword)) {
            tilConfirmNewPassword.setError(getString(R.string.err_password_mismatch));
            tilConfirmNewPassword.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError(getString(R.string.err_empty_password));
            tilNewPassword.requestFocus();
            cancel = true;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError(getString(R.string.err_short_password));
            tilNewPassword.requestFocus();
            cancel = true;
        }

        if (cancel) {
            return;
        }

        // Perform local SHA-256 password reset
        authManager.resetPassword(targetEmail, newPassword);

        Toast.makeText(this, getString(R.string.reset_password_success), Toast.LENGTH_LONG).show();

        // Return to Login with prefilled email
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_PREFILL_EMAIL, targetEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
