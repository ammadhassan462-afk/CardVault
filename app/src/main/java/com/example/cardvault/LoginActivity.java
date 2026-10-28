package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity handles local authentication for CardVault.
 * Accepts either registered email or username, authenticating against SHA-256 hashed credentials.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_PREFILL_EMAIL = "extra_email";

    private TextInputLayout tilEmailOrUser;
    private TextInputEditText etEmailOrUser;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private Button btnSignIn;
    private TextView tvForgotPassword;
    private TextView tvToRegister;

    private CardVaultAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = CardVaultAuthManager.getInstance(this);

        initViews();
        handleIntentData();
        setupListeners();
    }

    private void initViews() {
        tilEmailOrUser = findViewById(R.id.tilEmailOrUser);
        etEmailOrUser = findViewById(R.id.etEmailOrUser);
        tilPassword = findViewById(R.id.tilPassword);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvToRegister = findViewById(R.id.tvToRegister);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PREFILL_EMAIL)) {
            String prefillEmail = intent.getStringExtra(EXTRA_PREFILL_EMAIL);
            if (prefillEmail != null && !prefillEmail.isEmpty()) {
                etEmailOrUser.setText(prefillEmail);
                etPassword.requestFocus();
            }
        }
    }

    private void setupListeners() {
        btnSignIn.setOnClickListener(v -> attemptLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            String input = etEmailOrUser.getText() != null ? etEmailOrUser.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(input)) {
                intent.putExtra(EXTRA_PREFILL_EMAIL, input);
            }
            startActivity(intent);
        });

        tvToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        tilEmailOrUser.setError(null);
        tilPassword.setError(null);

        String emailOrUser = etEmailOrUser.getText() != null ? etEmailOrUser.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        boolean cancel = false;

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_empty_password));
            tilPassword.requestFocus();
            cancel = true;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_short_password));
            tilPassword.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(emailOrUser)) {
            tilEmailOrUser.setError(getString(R.string.err_empty_identifier));
            tilEmailOrUser.requestFocus();
            cancel = true;
        }

        if (cancel) {
            return;
        }

        // Perform local SHA-256 simulated authentication
        if (authManager.authenticate(emailOrUser, password)) {
            String name = authManager.getUserName(emailOrUser);
            String phone = authManager.getUserPhone(emailOrUser);
            authManager.setCurrentSession(emailOrUser, name, phone);

            Toast.makeText(this, getString(R.string.auth_welcome_back, name), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            tilPassword.setError(getString(R.string.err_invalid_credentials));
            tilPassword.requestFocus();
        }
    }
}
