package com.example.musicvault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvault.R;
import com.example.musicvault.model.SavedAlbum;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavedAlbumAdapter extends RecyclerView.Adapter<SavedAlbumAdapter.SavedAlbumViewHolder> {

    public interface OnSavedAlbumClickListener {
        void onSavedAlbumClick(SavedAlbum album);
    }

    private List<SavedAlbum> albums = new ArrayList<>();
    private final OnSavedAlbumClickListener listener;

    public SavedAlbumAdapter(OnSavedAlbumClickListener listener) {
        this.listener = listener;
    }

    public void setAlbums(List<SavedAlbum> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @Override
    public SavedAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_saved_album,
                parent,
                false
        );

        return new SavedAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SavedAlbumViewHolder holder, int position) {
        SavedAlbum album = albums.get(position);

        holder.txtAlbumName.setText(album.getCollectionName());
        holder.txtArtistName.setText(album.getArtistName());
        holder.txtStatus.setText(statusParaExibir(album.getStatus()));

        boolean isWantToListen = "Quero ouvir".equalsIgnoreCase(album.getStatus());

        if (isWantToListen) {
            holder.txtRatingBadge.setVisibility(View.GONE);
            holder.txtRating.setVisibility(View.GONE);
            holder.txtReviewPreview.setVisibility(View.GONE);
        } else {
            String ratingText = String.format(Locale.US, "%.1f", album.getRating());

            holder.txtRatingBadge.setVisibility(View.VISIBLE);
            holder.txtRating.setVisibility(View.GONE);
            holder.txtRatingBadge.setText(ratingText);
            holder.txtRating.setText("Nota: " + ratingText);

            String review = album.getReview();
            holder.txtReviewPreview.setVisibility(View.VISIBLE);

            if (review == null || review.trim().isEmpty()) {
                holder.txtReviewPreview.setText("Sem review escrita ainda.");
            } else {
                holder.txtReviewPreview.setText("\u201c" + review.trim() + "\u201d");
            }
        }

        Picasso.get()
                .load(album.getArtworkUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> listener.onSavedAlbumClick(album));
    }

    private String statusParaExibir(String status) {
        if ("Ouvido".equalsIgnoreCase(status)) {
            return "Já ouvi";
        }

        return status;
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class SavedAlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtAlbumName;
        TextView txtArtistName;
        TextView txtStatus;
        TextView txtRating;
        TextView txtRatingBadge;
        TextView txtReviewPreview;

        public SavedAlbumViewHolder(View itemView) {
            super(itemView);

            imgCover = itemView.findViewById(R.id.imgCover);
            txtAlbumName = itemView.findViewById(R.id.txtAlbumName);
            txtArtistName = itemView.findViewById(R.id.txtArtistName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtRating = itemView.findViewById(R.id.txtRating);
            txtRatingBadge = itemView.findViewById(R.id.txtRatingBadge);
            txtReviewPreview = itemView.findViewById(R.id.txtReviewPreview);
        }
    }
}
