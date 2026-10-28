package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * ForgotPasswordActivity provides a simulated password-recovery interface.
 * It demonstrates local recovery workflows without claiming real-world email delivery.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String EXTRA_RESET_EMAIL = "extra_reset_email";

    private ImageButton btnBack;
    private TextInputLayout tilForgotEmail;
    private TextInputEditText etForgotEmail;
    private Button btnSendRecovery;
    private CardView cardRecoveryNotice;
    private Button btnProceedToReset;
    private TextView tvBackToLogin;

    private CardVaultAuthManager authManager;
    private String validatedEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authManager = CardVaultAuthManager.getInstance(this);

        initViews();
        handleIntentData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tilForgotEmail = findViewById(R.id.tilForgotEmail);
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendRecovery = findViewById(R.id.btnSendRecovery);
        cardRecoveryNotice = findViewById(R.id.cardRecoveryNotice);
        btnProceedToReset = findViewById(R.id.btnProceedToReset);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(LoginActivity.EXTRA_PREFILL_EMAIL)) {
            String prefill = intent.getStringExtra(LoginActivity.EXTRA_PREFILL_EMAIL);
            if (prefill != null && !prefill.isEmpty()) {
                etForgotEmail.setText(prefill);
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        tvBackToLogin.setOnClickListener(v -> finish());

        btnSendRecovery.setOnClickListener(v -> handleRecoveryRequest());

        btnProceedToReset.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            intent.putExtra(EXTRA_RESET_EMAIL, validatedEmail);
            startActivity(intent);
            finish();
        });
    }

    private void handleRecoveryRequest() {
        tilForgotEmail.setError(null);
        String input = etForgotEmail.getText() != null ? etForgotEmail.getText().toString().trim() : "";

        if (TextUtils.isEmpty(input)) {
            tilForgotEmail.setError(getString(R.string.err_empty_identifier));
            tilForgotEmail.requestFocus();
            return;
        }

        if (input.contains("@") && !Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            tilForgotEmail.setError(getString(R.string.err_invalid_email));
            tilForgotEmail.requestFocus();
            return;
        }

        validatedEmail = authManager.resolveToEmail(input);

        // Display simulated recovery card
        cardRecoveryNotice.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Simulated recovery link verified locally", Toast.LENGTH_SHORT).show();
    }
}
