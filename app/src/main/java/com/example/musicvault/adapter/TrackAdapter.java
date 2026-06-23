package com.example.musicvault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvault.R;
import com.example.musicvault.model.Track;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private List<Track> tracks = new ArrayList<>();

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_track,
                parent,
                false
        );

        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        int number = track.getTrackNumber() > 0 ? track.getTrackNumber() : position + 1;

        holder.txtTrackNumber.setText(String.valueOf(number));
        holder.txtTrackName.setText(track.getTrackName());
        holder.txtTrackDuration.setText(track.getDurationFormatted());
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView txtTrackNumber;
        TextView txtTrackName;
        TextView txtTrackDuration;

        public TrackViewHolder(View itemView) {
            super(itemView);

            txtTrackNumber = itemView.findViewById(R.id.txtTrackNumber);
            txtTrackName = itemView.findViewById(R.id.txtTrackName);
            txtTrackDuration = itemView.findViewById(R.id.txtTrackDuration);
        }
    }
}
