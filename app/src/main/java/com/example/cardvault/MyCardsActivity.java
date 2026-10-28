package com.example.cardvault;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * My Cards screen: Search, filter by category, and browse all cards.
 */
public class MyCardsActivity extends AppCompatActivity {

    private EditText etSearch;
    private LinearLayout layoutCategoryChips;
    private RecyclerView rvCards;
    private View layoutEmptyState;
    private CardAdapter cardAdapter;
    private DatabaseHelper dbHelper;

    private String selectedCategory = "All";
    private String currentQuery = "";
    private final List<TextView> categoryChipViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cards);

        dbHelper = DatabaseHelper.getInstance(this);

        etSearch = findViewById(R.id.etSearchCards);
        layoutCategoryChips = findViewById(R.id.layoutCategoryChips);
        rvCards = findViewById(R.id.rvAllCards);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        Button btnHeaderAdd = findViewById(R.id.btnHeaderAddCard);
        Button btnEmptyAdd = findViewById(R.id.btnEmptyAddCard);

        // RecyclerView setup
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(this, new CardAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(Card card) {
                Intent intent = new Intent(MyCardsActivity.this, CardDetailsActivity.class);
                intent.putExtra(CardDetailsActivity.EXTRA_CARD_ID, card.getId());
                startActivity(intent);
            }
        });
        rvCards.setAdapter(cardAdapter);

        // Setup Categories Filter
        setupCategoryChips();

        // Search TextWatcher for real-time filtering
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                filterAndDisplayCards();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add Card Actions
        View.OnClickListener addCardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCardsActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        };
        btnHeaderAdd.setOnClickListener(addCardClickListener);
        btnEmptyAdd.setOnClickListener(addCardClickListener);

        // Setup Bottom Navigation
        NavigationHelper.setupBottomNavigation(this, NavigationHelper.Screen.CARDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterAndDisplayCards();
    }

    private void setupCategoryChips() {
        layoutCategoryChips.removeAllViews();
        categoryChipViews.clear();

        List<String> categories = new ArrayList<>();
        categories.add("All");
        String[] configCats = getResources().getStringArray(R.array.card_categories);
        for (String c : configCats) {
            categories.add(c);
        }

        for (final String category : categories) {
            final TextView chip = new TextView(this);
            chip.setText(category);
            chip.setTextSize(13);
            chip.setPadding(dpToPx(14), dpToPx(6), dpToPx(14), dpToPx(6));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, dpToPx(8), 0);
            chip.setLayoutParams(params);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCategory = category;
                    updateChipStyles();
                    filterAndDisplayCards();
                }
            });

            layoutCategoryChips.addView(chip);
            categoryChipViews.add(chip);
        }

        updateChipStyles();
    }

    private void updateChipStyles() {
        int primaryBlue = ContextCompat.getColor(this, R.color.primary_blue);
        int softBlue = ContextCompat.getColor(this, R.color.soft_blue_surface);
        int surfaceColor = ContextCompat.getColor(this, R.color.card_surface);
        int textSecondary = ContextCompat.getColor(this, R.color.text_secondary);

        for (TextView chip : categoryChipViews) {
            boolean isSelected = chip.getText().toString().equalsIgnoreCase(selectedCategory);
            if (isSelected) {
                chip.setBackgroundResource(R.drawable.bg_button_primary);
                chip.setTextColor(Color.WHITE);
                chip.setTypeface(null, Typeface.BOLD);
            } else {
                chip.setBackgroundResource(R.drawable.bg_input_field);
                chip.setTextColor(textSecondary);
                chip.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void filterAndDisplayCards() {
        List<Card> cards = dbHelper.searchAndFilterCards(currentQuery, selectedCategory);

        if (cards.isEmpty()) {
            rvCards.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvCards.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            cardAdapter.setCards(cards);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
