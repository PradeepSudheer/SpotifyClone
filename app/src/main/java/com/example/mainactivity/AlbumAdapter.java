package com.example.mainactivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    final String TAG = "TAG";
    List<Album> allAlbums;
    List<Album> likedAlbums;

    public interface AlbumCardViewInterface{
        void callAlbumActivity(Album album);
        void changeLikeDisLike(Album album);
    }

    AlbumCardViewInterface albumCardViewInterface;
    public AlbumAdapter(List<Album> allAlbums, List<Album> likedAlbums, AlbumCardViewInterface albumCardViewInterface) {
        this.allAlbums = allAlbums;
        this.likedAlbums = likedAlbums;
        this.albumCardViewInterface = albumCardViewInterface;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album currAlbum = allAlbums.get(position);
        holder.artistName.setText(currAlbum.artistName);
        holder.albumNbTracks.setText(currAlbum.nb_tracks+"");
        holder.albumTitle.setText(currAlbum.title);
        Picasso.get().load(currAlbum.image).into(holder.albumIcon);

        //like feature
        boolean flag = false;
        for(Album album: likedAlbums){
            if(album.id == currAlbum.id){
                holder.albumLikeIV.setImageResource(R.drawable.like_favorite);
                flag = true;
                break;
            }
        }
        if(!flag) holder.albumLikeIV.setImageResource(R.drawable.like_not_favorite);

        holder.albumLikeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                albumCardViewInterface.changeLikeDisLike(currAlbum);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumCardViewInterface.callAlbumActivity(currAlbum);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allAlbums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumIcon, albumLikeIV;
        TextView albumTitle, artistName, albumNbTracks;
        final String TAG = "TAG";
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumIcon = itemView.findViewById(R.id.albumIcon);
            albumLikeIV = itemView.findViewById(R.id.albumLikeIV);

            albumTitle = itemView.findViewById(R.id.albumTitle);
            artistName = itemView.findViewById(R.id.artistName);
            albumNbTracks = itemView.findViewById(R.id.albumNbTracks);
        }


    }
}
