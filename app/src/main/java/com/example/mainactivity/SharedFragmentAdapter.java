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

public class SharedFragmentAdapter extends RecyclerView.Adapter<SharedFragmentAdapter.ViewHolder> {

    List<Album> allAlbumsShared;
    SharedFragmentAdapterInterface sharedFragmentAdapterInterface;

    public SharedFragmentAdapter(List<Album> allAlbumsShared, SharedFragmentAdapterInterface sharedFragmentAdapterInterface){
        this.allAlbumsShared = allAlbumsShared;
        this.sharedFragmentAdapterInterface = sharedFragmentAdapterInterface;
    }

    public interface SharedFragmentAdapterInterface{
        void callAlbumActivity(Album album);
    }

    @NonNull
    @Override
    public SharedFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_cardview, parent, false);
        return new SharedFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedFragmentAdapter.ViewHolder holder, int position) {
        Album currAlbum = allAlbumsShared.get(position);
        holder.albumTitle.setText(currAlbum.title);
        holder.artistName.setText(currAlbum.artistName);
        holder.albumNbTracks.setText(currAlbum.nb_tracks+"");
        holder.albumLikeIV.setVisibility(holder.itemView.INVISIBLE);
        holder.optionalTextView1.setText(currAlbum.sharedUsername);
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm aa");
        String date = sfd.format(new Date(String.valueOf(allAlbumsShared.get(position).createdAt.toDate())));

        holder.optionalTextView2.setText(date);
        Picasso.get().load(currAlbum.image).into(holder.albumIcon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedFragmentAdapterInterface.callAlbumActivity(currAlbum);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allAlbumsShared.size();
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
