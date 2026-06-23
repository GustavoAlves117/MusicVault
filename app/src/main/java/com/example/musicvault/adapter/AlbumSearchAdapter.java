package com.example.musicvault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvault.R;
import com.example.musicvault.model.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AlbumSearchAdapter extends RecyclerView.Adapter<AlbumSearchAdapter.AlbumViewHolder> {

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    private List<Album> albums = new ArrayList<>();
    private final OnAlbumClickListener listener;

    public AlbumSearchAdapter(OnAlbumClickListener listener) {
        this.listener = listener;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_album_search,
                parent,
                false
        );

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = albums.get(position);

        holder.txtAlbumName.setText(album.getCollectionName());
        holder.txtArtistName.setText(album.getArtistName());

        String genre = album.getPrimaryGenreName() == null ? "Gênero não informado" : album.getPrimaryGenreName();
        holder.txtGenre.setText(genre);

        Picasso.get()
                .load(album.getHighQualityArtworkUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> listener.onAlbumClick(album));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtAlbumName;
        TextView txtArtistName;
        TextView txtGenre;

        public AlbumViewHolder(View itemView) {
            super(itemView);

            imgCover = itemView.findViewById(R.id.imgCover);
            txtAlbumName = itemView.findViewById(R.id.txtAlbumName);
            txtArtistName = itemView.findViewById(R.id.txtArtistName);
            txtGenre = itemView.findViewById(R.id.txtGenre);
        }
    }
}
