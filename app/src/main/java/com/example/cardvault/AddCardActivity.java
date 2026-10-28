package com.example.cardvault;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Add / Edit Card screen: Form for manual card entry and photo attachment.
 */
public class AddCardActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "extra_card_id";

    private ImageView ivPreview;
    private TextView tvPhotoActionText;
    private TextView tvRemovePhoto;
    private EditText etName;
    private Spinner spCategory;
    private EditText etIdentifier;
    private EditText etExpiry;
    private EditText etNotes;
    private TextView tvScreenTitle;
    private Button btnSave;

    private DatabaseHelper dbHelper;
    private Card editingCard;
    private boolean isEditMode = false;

    private Uri pendingImageUri = null;
    private String currentImagePath = null;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        pendingImageUri = uri;
                        ivPreview.setImageURI(uri);
                        tvPhotoActionText.setText(R.string.change_photo);
                        tvRemovePhoto.setVisibility(View.VISIBLE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        dbHelper = DatabaseHelper.getInstance(this);

        ivPreview = findViewById(R.id.ivCardPreview);
        View layoutPhotoAction = findViewById(R.id.layoutPhotoAction);
        tvPhotoActionText = findViewById(R.id.tvPhotoActionText);
        tvRemovePhoto = findViewById(R.id.tvRemovePhoto);
        etName = findViewById(R.id.etCardName);
        spCategory = findViewById(R.id.spCategory);
        etIdentifier = findViewById(R.id.etCardIdentifier);
        etExpiry = findViewById(R.id.etCardExpiry);
        etNotes = findViewById(R.id.etCardNotes);
        tvScreenTitle = findViewById(R.id.tvScreenTitle);
        btnSave = findViewById(R.id.btnSaveCard);
        Button btnCancel = findViewById(R.id.btnCancel);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Setup Category Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.card_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        // Photo Picker trigger
        layoutPhotoAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        // Remove Photo
        tvRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingImageUri = null;
                currentImagePath = null;
                ivPreview.setImageResource(R.drawable.ic_card_placeholder);
                tvPhotoActionText.setText(R.string.choose_photo);
                tvRemovePhoto.setVisibility(View.GONE);
            }
        });

        // Cancel / Back actions
        View.OnClickListener closeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        btnBack.setOnClickListener(closeListener);
        btnCancel.setOnClickListener(closeListener);

        // Check if in Edit Mode
        long cardId = getIntent().getLongExtra(EXTRA_CARD_ID, -1);
        if (cardId != -1) {
            editingCard = dbHelper.getCardById(cardId);
            if (editingCard != null) {
                isEditMode = true;
                populateExistingCard(editingCard);
            }
        }

        // Save Action
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
            }
        });
    }

    private void populateExistingCard(Card card) {
        tvScreenTitle.setText(R.string.edit_card_title);
        btnSave.setText(R.string.edit_card_title);
        etName.setText(card.getName());
        etIdentifier.setText(card.getIdentifier());
        etExpiry.setText(card.getExpiryDate());
        etNotes.setText(card.getNotes());

        // Category selection
        String[] categories = getResources().getStringArray(R.array.card_categories);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(card.getCategory())) {
                spCategory.setSelection(i);
                break;
            }
        }

        // Load existing image
        currentImagePath = card.getImagePath();
        if (currentImagePath != null && !currentImagePath.trim().isEmpty()) {
            Bitmap bmp = ImageUtils.loadBitmap(this, currentImagePath, 600, 360);
            if (bmp != null) {
                ivPreview.setImageBitmap(bmp);
                tvPhotoActionText.setText(R.string.change_photo);
                tvRemovePhoto.setVisibility(View.VISIBLE);
            }
        }
    }

    private void saveCard() {
        String name = etName.getText().toString().trim();
        String category = spCategory.getSelectedItem() != null
                ? spCategory.getSelectedItem().toString() : "Other";
        String identifier = etIdentifier.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_name_required));
            etName.requestFocus();
            return;
        }

        String finalImagePath = currentImagePath;

        // If a new image was selected via picker, save it to private app storage
        if (pendingImageUri != null) {
            String savedPath = ImageUtils.copyUriToInternalStorage(this, pendingImageUri);
            if (savedPath != null) {
                // If replacing an existing image in edit mode, delete old file
                if (isEditMode && currentImagePath != null && !currentImagePath.equals(savedPath)) {
                    ImageUtils.deleteImage(this, currentImagePath);
                }
                finalImagePath = savedPath;
            }
        } else if (isEditMode && currentImagePath == null && editingCard != null && editingCard.getImagePath() != null) {
            // Photo was removed in edit mode
            ImageUtils.deleteImage(this, editingCard.getImagePath());
        }

        if (isEditMode && editingCard != null) {
            editingCard.setName(name);
            editingCard.setCategory(category);
            editingCard.setIdentifier(identifier);
            editingCard.setExpiryDate(expiry);
            editingCard.setNotes(notes);
            editingCard.setImagePath(finalImagePath);

            dbHelper.updateCard(editingCard);
            Toast.makeText(this, R.string.card_updated_success, Toast.LENGTH_SHORT).show();
        } else {
            Card newCard = new Card(name, category, identifier, expiry, notes, finalImagePath);
            dbHelper.insertCard(newCard);
            Toast.makeText(this, R.string.card_saved_success, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}
