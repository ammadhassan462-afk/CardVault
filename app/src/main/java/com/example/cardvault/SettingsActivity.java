package com.example.cardvault;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Settings screen: local storage explanation, app version, and data reset options.
 */
public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = DatabaseHelper.getInstance(this);
        CardVaultAuthManager authManager = CardVaultAuthManager.getInstance(this);

        TextView tvLoggedInUser = findViewById(R.id.tvLoggedInUser);
        if (tvLoggedInUser != null) {
            tvLoggedInUser.setText(getString(R.string.settings_logged_in_as) + " " + authManager.getCurrentName() + " (" + authManager.getCurrentEmail() + ")");
        }

        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutConfirmationDialog(authManager);
                }
            });
        }

        Button btnDeleteAll = findViewById(R.id.btnDeleteAllCards);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAllConfirmationDialog();
            }
        });

        // Setup Bottom Navigation
        NavigationHelper.setupBottomNavigation(this, NavigationHelper.Screen.SETTINGS);
    }

    private void showLogoutConfirmationDialog(CardVaultAuthManager authManager) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_msg)
                .setPositiveButton(R.string.action_logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        authManager.logout();
                        Toast.makeText(SettingsActivity.this, "Signed out of CardVault", Toast.LENGTH_SHORT).show();
                        android.content.Intent intent = new android.content.Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void showDeleteAllConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_all_confirm_title)
                .setMessage(R.string.delete_all_confirm_message)
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performDeleteAll();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void performDeleteAll() {
        // Delete all stored card photos
        ImageUtils.deleteAllStoredImages(this);

        // Delete all database rows
        dbHelper.deleteAllCards();

        Toast.makeText(this, R.string.all_cards_deleted_success, Toast.LENGTH_LONG).show();
    }
}
