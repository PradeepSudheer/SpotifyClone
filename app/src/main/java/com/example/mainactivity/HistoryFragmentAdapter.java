package com.example.mainactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryFragmentAdapter extends RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder> {

    List<Track> tracksHistory;

    public HistoryFragmentAdapter(List<Track> tracksHistory) {
        this.tracksHistory = tracksHistory;
    }

    @NonNull
    @Override
    public HistoryFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_cardview, parent, false);
        return new HistoryFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryFragmentAdapter.ViewHolder holder, int position) {
        Track track = tracksHistory.get(position);
        holder.albumTitle.setText(track.title);
        holder.artistName.setText(track.albumTitle);
        holder.albumNbTracks.setText(track.artistName+"");
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm aa");
        String date = sfd.format(new Date(String.valueOf(tracksHistory.get(position).createdAt.toDate())));
        holder.optionalTextView1.setText(date);
        Picasso.get().load(track.image).into(holder.albumIcon);
        holder.albumLikeIV.setVisibility(holder.itemView.INVISIBLE);
        holder.optionalTextView2.setVisibility(holder.itemView.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return tracksHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumIcon,albumLikeIV;
        TextView albumTitle, artistName, albumNbTracks, optionalTextView1, optionalTextView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumIcon = itemView.findViewById(R.id.albumIcon);
            albumLikeIV = itemView.findViewById(R.id.albumLikeIV);
            albumTitle = itemView.findViewById(R.id.albumTitle);
            artistName = itemView.findViewById(R.id.artistName);
            albumNbTracks = itemView.findViewById(R.id.albumNbTracks);
            optionalTextView1 = itemView.findViewById(R.id.optionalTextView1);
            optionalTextView2 = itemView.findViewById(R.id.optionalTextView2);
        }
    }
}
