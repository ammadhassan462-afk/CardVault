package com.example.cardvault;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * Helper to manage consistent bottom navigation behavior across activities.
 */
public class NavigationHelper {

    public enum Screen {
        HOME, CARDS, SETTINGS
    }

    public static void setupBottomNavigation(final Activity activity, Screen currentScreen) {
        View navHome = activity.findViewById(R.id.navItemHome);
        View navCards = activity.findViewById(R.id.navItemCards);
        View navSettings = activity.findViewById(R.id.navItemSettings);

        ImageView ivHome = activity.findViewById(R.id.ivNavHome);
        TextView tvHome = activity.findViewById(R.id.tvNavHome);
        ImageView ivCards = activity.findViewById(R.id.ivNavCards);
        TextView tvCards = activity.findViewById(R.id.tvNavCards);
        ImageView ivSettings = activity.findViewById(R.id.ivNavSettings);
        TextView tvSettings = activity.findViewById(R.id.tvNavSettings);

        if (navHome == null || navCards == null || navSettings == null) {
            return;
        }

        int activeColor = ContextCompat.getColor(activity, R.color.primary_blue);
        int inactiveColor = ContextCompat.getColor(activity, R.color.text_secondary);

        // Reset states
        ivHome.setColorFilter(inactiveColor);
        tvHome.setTextColor(inactiveColor);
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivCards.setColorFilter(inactiveColor);
        tvCards.setTextColor(inactiveColor);
        tvCards.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivSettings.setColorFilter(inactiveColor);
        tvSettings.setTextColor(inactiveColor);
        tvSettings.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Apply active state
        switch (currentScreen) {
            case HOME:
                ivHome.setColorFilter(activeColor);
                tvHome.setTextColor(activeColor);
                tvHome.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case CARDS:
                ivCards.setColorFilter(activeColor);
                tvCards.setTextColor(activeColor);
                tvCards.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case SETTINGS:
                ivSettings.setColorFilter(activeColor);
                tvSettings.setTextColor(activeColor);
                tvSettings.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
        }

        // Navigation click listeners
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(activity instanceof MainActivity)) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    activity.finish();
                }
            }
        });

        navCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(activity instanceof MyCardsActivity)) {
                    Intent intent = new Intent(activity, MyCardsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    if (!(activity instanceof MainActivity)) {
                        activity.finish();
                    }
                }
            }
        });

        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(activity instanceof SettingsActivity)) {
                    Intent intent = new Intent(activity, SettingsActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    if (!(activity instanceof MainActivity)) {
                        activity.finish();
                    }
                }
            }
        });
    }
}
