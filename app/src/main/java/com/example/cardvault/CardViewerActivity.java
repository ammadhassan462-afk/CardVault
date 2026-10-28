package com.example.cardvault;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * Zoomed Card Viewer screen: displays card full-screen with interactive 3D horizontal and vertical flip.
 */
public class CardViewerActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "extra_card_id";

    private CardView cardFrontView;
    private CardView cardBackView;
    private FrameLayout cardContainer;

    private ImageView ivCardFrontPhoto;
    private TextView tvCardFrontName;
    private TextView tvCardBackIdentifier;
    private TextView tvCardBackExpiry;
    private TextView tvViewerTitle;
    private TextView tvViewerCategory;

    private boolean isShowingBack = false;
    private boolean isAnimating = false;

    private DatabaseHelper dbHelper;
    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);

        dbHelper = DatabaseHelper.getInstance(this);
        long cardId = getIntent().getLongExtra(EXTRA_CARD_ID, -1);

        if (cardId == -1) {
            Toast.makeText(this, "Card not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        card = dbHelper.getCardById(cardId);
        if (card == null) {
            finish();
            return;
        }

        // Initialize Views
        cardContainer = findViewById(R.id.cardContainer);
        cardFrontView = findViewById(R.id.cardFrontView);
        cardBackView = findViewById(R.id.cardBackView);

        ivCardFrontPhoto = findViewById(R.id.ivCardFrontPhoto);
        tvCardFrontName = findViewById(R.id.tvCardFrontName);
        tvCardBackIdentifier = findViewById(R.id.tvCardBackIdentifier);
        tvCardBackExpiry = findViewById(R.id.tvCardBackExpiry);
        tvViewerTitle = findViewById(R.id.tvViewerTitle);
        tvViewerCategory = findViewById(R.id.tvViewerCategory);

        ImageView btnClose = findViewById(R.id.btnViewerClose);
        Button btnFlipHorizontal = findViewById(R.id.btnFlipHorizontal);
        Button btnFlipVertical = findViewById(R.id.btnFlipVertical);

        // Adjust camera distance for realistic 3D perspective depth
        float scale = getResources().getDisplayMetrics().density;
        cardFrontView.setCameraDistance(8000 * scale);
        cardBackView.setCameraDistance(8000 * scale);

        // Populate Card Data
        populateCardData();

        // Close Action
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Tap Card anywhere to flip
        cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipHorizontal();
            }
        });

        // Button: Flip Horizontal
        btnFlipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipHorizontal();
            }
        });

        // Button: Flip Vertical
        btnFlipVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipVertical();
            }
        });
    }

    private void populateCardData() {
        tvViewerTitle.setText(card.getName());
        tvViewerCategory.setText(card.getCategory());
        tvCardFrontName.setText(card.getName());

        // Front Face Photo
        if (card.getImagePath() != null && !card.getImagePath().trim().isEmpty()) {
            Bitmap bmp = ImageUtils.loadBitmap(this, card.getImagePath(), 900, 600);
            if (bmp != null) {
                ivCardFrontPhoto.setImageBitmap(bmp);
                tvCardFrontName.setVisibility(View.GONE); // Real photo has its own text
            } else {
                ivCardFrontPhoto.setImageResource(R.drawable.ic_card_placeholder);
                tvCardFrontName.setVisibility(View.VISIBLE);
            }
        } else {
            ivCardFrontPhoto.setImageResource(R.drawable.ic_card_placeholder);
            tvCardFrontName.setVisibility(View.VISIBLE);
        }

        // Back Face Metadata
        if (card.getIdentifier() != null && !card.getIdentifier().trim().isEmpty()) {
            tvCardBackIdentifier.setText(card.getIdentifier());
        } else {
            tvCardBackIdentifier.setText("LOCAL-ID-" + card.getId());
        }

        if (card.getExpiryDate() != null && !card.getExpiryDate().trim().isEmpty()) {
            tvCardBackExpiry.setText("Exp: " + card.getExpiryDate());
        } else {
            tvCardBackExpiry.setText("CardVault Edition");
        }
    }

    /**
     * Performs a 3D horizontal flip along the Y-axis.
     */
    private void flipHorizontal() {
        if (isAnimating) return;
        isAnimating = true;

        final View currentVisible = isShowingBack ? cardBackView : cardFrontView;
        final View nextVisible = isShowingBack ? cardFrontView : cardBackView;

        // Reset rotation axes
        currentVisible.setRotationX(0f);
        nextVisible.setRotationX(0f);

        // Phase 1: Rotate currently visible card 90 degrees away
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(currentVisible, "rotationY", 0f, 90f);
        anim1.setDuration(250);
        anim1.setInterpolator(new AccelerateDecelerateInterpolator());

        // Phase 2: Rotate next card from 270 to 360 degrees into view
        final ObjectAnimator anim2 = ObjectAnimator.ofFloat(nextVisible, "rotationY", 270f, 360f);
        anim2.setDuration(250);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());

        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentVisible.setVisibility(View.GONE);
                nextVisible.setVisibility(View.VISIBLE);
                nextVisible.setRotationY(270f);
                anim2.start();
            }
        });

        anim2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isShowingBack = !isShowingBack;
                isAnimating = false;
            }
        });

        anim1.start();
    }

    /**
     * Performs a 3D vertical flip along the X-axis.
     */
    private void flipVertical() {
        if (isAnimating) return;
        isAnimating = true;

        final View currentVisible = isShowingBack ? cardBackView : cardFrontView;
        final View nextVisible = isShowingBack ? cardFrontView : cardBackView;

        // Reset rotation axes
        currentVisible.setRotationY(0f);
        nextVisible.setRotationY(0f);

        // Phase 1: Rotate currently visible card 90 degrees vertically
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(currentVisible, "rotationX", 0f, 90f);
        anim1.setDuration(250);
        anim1.setInterpolator(new AccelerateDecelerateInterpolator());

        // Phase 2: Rotate next card from 270 to 360 degrees vertically into view
        final ObjectAnimator anim2 = ObjectAnimator.ofFloat(nextVisible, "rotationX", 270f, 360f);
        anim2.setDuration(250);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());

        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentVisible.setVisibility(View.GONE);
                nextVisible.setVisibility(View.VISIBLE);
                nextVisible.setRotationX(270f);
                anim2.start();
            }
        });

        anim2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isShowingBack = !isShowingBack;
                isAnimating = false;
            }
        });

        anim1.start();
    }
}
