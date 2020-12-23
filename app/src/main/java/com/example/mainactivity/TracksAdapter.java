package com.example.mainactivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.ViewHolder> {

    List<Track> allTracks;
    final String TAG = "TAG";
    TrackAdapterInterface trackAdapterInterface;
    int playingPosition = -1;

    MediaPlayer mediaPlayer = new MediaPlayer();
    public TracksAdapter(List<Track> allTracks, TrackAdapterInterface trackAdapterInterface) {
        this.allTracks = allTracks;
        this.trackAdapterInterface = trackAdapterInterface;
    }

    public interface TrackAdapterInterface{
        void trackHistory(Track track);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_cardview, parent, false);
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        return new TracksAdapter.ViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Track track = allTracks.get(position);
        holder.trackCardviewTrackDuration.setText(track.duration1);
        holder.trackCardviewTrackTitle.setText(track.title);


        holder.trackCardviewPlayPause.setOnClickListener(new View.OnClickListener() {
            int buttonpos = 0;
            @Override
            public void onClick(View v) {


                if(holder.trackCardviewPlayPause.getDrawable().getConstantState() == holder.itemView.getContext().getResources().getDrawable(R.drawable.play_button).getConstantState()){
                    holder.trackCardviewPlayPause.setImageResource(R.drawable.pause_button);
                    trackAdapterInterface.trackHistory(track);
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(track.preview);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    holder.trackCardviewPlayPause.setImageResource(R.drawable.play_button);
                    mediaPlayer.pause();

                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
                Log.d("released", "released");
                holder.trackCardviewPlayPause.setImageResource(R.drawable.play_button);
            }
        });


    }


    @Override
    public int getItemCount() {
        return allTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView trackCardviewTrackTitle, trackCardviewTrackDuration;
        ImageView trackCardviewPlayPause;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackCardviewPlayPause = itemView.findViewById(R.id.trackCardviewPlayPause);
            trackCardviewTrackTitle = itemView.findViewById(R.id.trackCardviewTrackTitle);
            trackCardviewTrackDuration = itemView.findViewById(R.id.trackCardviewTrackDuration);
        }

    }


}
