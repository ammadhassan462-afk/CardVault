package com.example.cardvault;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Card Details screen: view full card image and metadata with edit/delete actions.
 */
public class CardDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "extra_card_id";

    private ImageView ivFullPhoto;
    private TextView tvCategory;
    private TextView tvName;
    private TextView tvIdentifier;
    private TextView tvExpiry;
    private TextView tvNotes;
    private TextView tvDateAdded;

    private View rowIdentifier;
    private View rowExpiry;
    private View rowNotes;

    private DatabaseHelper dbHelper;
    private long cardId = -1;
    private Card currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        dbHelper = DatabaseHelper.getInstance(this);
        cardId = getIntent().getLongExtra(EXTRA_CARD_ID, -1);

        if (cardId == -1) {
            Toast.makeText(this, "Card not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivFullPhoto = findViewById(R.id.ivFullCardPhoto);
        tvCategory = findViewById(R.id.tvDetailsCategoryBadge);
        tvName = findViewById(R.id.tvDetailsCardName);
        tvIdentifier = findViewById(R.id.tvDetailsIdentifier);
        tvExpiry = findViewById(R.id.tvDetailsExpiry);
        tvNotes = findViewById(R.id.tvDetailsNotes);
        tvDateAdded = findViewById(R.id.tvDetailsDateAdded);

        rowIdentifier = findViewById(R.id.rowIdentifier);
        rowExpiry = findViewById(R.id.rowExpiry);
        rowNotes = findViewById(R.id.rowNotes);

        ImageView btnBack = findViewById(R.id.btnDetailsBack);
        ImageView btnToolbarEdit = findViewById(R.id.btnToolbarEdit);
        Button btnEdit = findViewById(R.id.btnDetailsEdit);
        Button btnDelete = findViewById(R.id.btnDetailsDelete);
        ImageView btnCopy = findViewById(R.id.btnCopyIdentifier);

        // Open Zoomed 3D Flip Viewer
        View.OnClickListener openViewerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailsActivity.this, CardViewerActivity.class);
                intent.putExtra(CardViewerActivity.EXTRA_CARD_ID, cardId);
                startActivity(intent);
            }
        };
        ivFullPhoto.setOnClickListener(openViewerListener);
        View cardPhotoContainer = findViewById(R.id.cardPhotoContainer);
        if (cardPhotoContainer != null) {
            cardPhotoContainer.setOnClickListener(openViewerListener);
        }

        // Back action
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Edit actions
        View.OnClickListener editListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditScreen();
            }
        };
        btnEdit.setOnClickListener(editListener);
        btnToolbarEdit.setOnClickListener(editListener);

        // Delete action with confirmation dialog
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        // Copy Identifier
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCard != null && !currentCard.getIdentifier().isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Card Identifier", currentCard.getIdentifier());
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(CardDetailsActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCardDetails();
    }

    private void loadCardDetails() {
        currentCard = dbHelper.getCardById(cardId);
        if (currentCard == null) {
            finish();
            return;
        }

        tvName.setText(currentCard.getName());
        tvCategory.setText(currentCard.getCategory());
        tvDateAdded.setText(currentCard.getFormattedCreatedDate());

        // Card Image
        if (currentCard.getImagePath() != null && !currentCard.getImagePath().trim().isEmpty()) {
            Bitmap bmp = ImageUtils.loadBitmap(this, currentCard.getImagePath(), 800, 500);
            if (bmp != null) {
                ivFullPhoto.setImageBitmap(bmp);
            } else {
                ivFullPhoto.setImageResource(R.drawable.ic_card_placeholder);
            }
        } else {
            ivFullPhoto.setImageResource(R.drawable.ic_card_placeholder);
        }

        // Identifier
        if (currentCard.getIdentifier() != null && !currentCard.getIdentifier().trim().isEmpty()) {
            rowIdentifier.setVisibility(View.VISIBLE);
            tvIdentifier.setText(currentCard.getIdentifier());
        } else {
            rowIdentifier.setVisibility(View.GONE);
        }

        // Expiry
        if (currentCard.getExpiryDate() != null && !currentCard.getExpiryDate().trim().isEmpty()) {
            rowExpiry.setVisibility(View.VISIBLE);
            tvExpiry.setText(currentCard.getExpiryDate());
        } else {
            rowExpiry.setVisibility(View.GONE);
        }

        // Notes
        if (currentCard.getNotes() != null && !currentCard.getNotes().trim().isEmpty()) {
            rowNotes.setVisibility(View.VISIBLE);
            tvNotes.setText(currentCard.getNotes());
        } else {
            rowNotes.setVisibility(View.GONE);
        }
    }

    private void openEditScreen() {
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra(AddCardActivity.EXTRA_CARD_ID, cardId);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCardAndClose();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void deleteCardAndClose() {
        if (currentCard != null) {
            // Delete local photo file if one exists
            if (currentCard.getImagePath() != null) {
                ImageUtils.deleteImage(this, currentCard.getImagePath());
            }
            // Delete database record
            dbHelper.deleteCard(currentCard.getId());
            Toast.makeText(this, R.string.card_deleted_success, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
