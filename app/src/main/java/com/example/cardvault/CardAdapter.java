package com.example.cardvault;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying saved cards.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(Card card);
    }

    private final Context context;
    private final List<Card> cardList = new ArrayList<>();
    private final OnCardClickListener listener;

    public CardAdapter(Context context, OnCardClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setCards(List<Card> cards) {
        this.cardList.clear();
        if (cards != null) {
            this.cardList.addAll(cards);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.bind(card, listener);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumbnail;
        private final TextView tvName;
        private final TextView tvCategory;
        private final TextView tvExpiry;
        private final TextView tvIdentifier;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivCardThumbnail);
            tvName = itemView.findViewById(R.id.tvCardName);
            tvCategory = itemView.findViewById(R.id.tvCategoryBadge);
            tvExpiry = itemView.findViewById(R.id.tvCardExpiry);
            tvIdentifier = itemView.findViewById(R.id.tvCardIdentifier);
        }

        public void bind(final Card card, final OnCardClickListener clickListener) {
            tvName.setText(card.getName());
            tvCategory.setText(card.getCategory());

            // Expiry date
            if (card.getExpiryDate() != null && !card.getExpiryDate().trim().isEmpty()) {
                tvExpiry.setVisibility(View.VISIBLE);
                tvExpiry.setText("Exp: " + card.getExpiryDate().trim());
            } else {
                tvExpiry.setVisibility(View.GONE);
            }

            // Identifier
            if (card.getIdentifier() != null && !card.getIdentifier().trim().isEmpty()) {
                tvIdentifier.setVisibility(View.VISIBLE);
                tvIdentifier.setText(card.getDisplayIdentifier());
            } else {
                tvIdentifier.setVisibility(View.GONE);
            }

            // Image Thumbnail
            if (card.getImagePath() != null && !card.getImagePath().trim().isEmpty()) {
                Bitmap bitmap = ImageUtils.loadBitmap(context, card.getImagePath(), 150, 100);
                if (bitmap != null) {
                    ivThumbnail.setImageBitmap(bitmap);
                } else {
                    ivThumbnail.setImageResource(R.drawable.ic_card_placeholder);
                }
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_card_placeholder);
            }

            // Tapping thumbnail directly opens Zoomed 3D Viewer
            ivThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CardViewerActivity.class);
                    intent.putExtra(CardViewerActivity.EXTRA_CARD_ID, card.getId());
                    context.startActivity(intent);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onCardClick(card);
                    }
                }
            });
        }
    }
}
