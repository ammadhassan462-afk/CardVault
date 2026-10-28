package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Home screen: displays summary statistics, recent cards, and quick actions.
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvTotalCount;
    private View layoutEmptyRecent;
    private RecyclerView rvRecentCards;
    private CardAdapter cardAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verify active session before displaying sensitive vault content
        if (!CardVaultAuthManager.getInstance(this).isLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Ensure database and sample cards are safely initialized
        dbHelper = DatabaseHelper.getInstance(this);

        setContentView(R.layout.activity_main);

        tvTotalCount = findViewById(R.id.tvTotalCardCount);
        layoutEmptyRecent = findViewById(R.id.layoutEmptyRecent);
        rvRecentCards = findViewById(R.id.rvRecentCards);
        Button btnQuickAdd = findViewById(R.id.btnQuickAddCard);
        TextView tvViewAll = findViewById(R.id.tvViewAllCards);

        // RecyclerView setup
        rvRecentCards.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(this, new CardAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(Card card) {
                Intent intent = new Intent(MainActivity.this, CardDetailsActivity.class);
                intent.putExtra(CardDetailsActivity.EXTRA_CARD_ID, card.getId());
                startActivity(intent);
            }
        });
        rvRecentCards.setAdapter(cardAdapter);

        // Quick Add Card
        btnQuickAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        });

        // View All Cards
        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyCardsActivity.class);
                startActivity(intent);
            }
        });

        // Setup Bottom Navigation
        NavigationHelper.setupBottomNavigation(this, NavigationHelper.Screen.HOME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        int totalCards = dbHelper.getCardCount();
        tvTotalCount.setText(String.valueOf(totalCards));

        List<Card> recentCards = dbHelper.getRecentCards(4);
        if (recentCards.isEmpty()) {
            rvRecentCards.setVisibility(View.GONE);
            layoutEmptyRecent.setVisibility(View.VISIBLE);
        } else {
            rvRecentCards.setVisibility(View.VISIBLE);
            layoutEmptyRecent.setVisibility(View.GONE);
            cardAdapter.setCards(recentCards);
        }
    }
}
