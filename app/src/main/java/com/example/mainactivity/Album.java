package com.example.mainactivity;

import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Album implements Serializable {
    long id;
    String title;
    String artistName;
    long nb_tracks;
    String image;
    String artistImage;
    Timestamp createdAt;
    String sharedUsername;

    public Album(JSONObject jsonObject) throws JSONException{
        this.id = jsonObject.getLong("id");
        this.title = jsonObject.getString("title");
        this.nb_tracks = jsonObject.getLong("nb_tracks");
        this.image = jsonObject.getString("cover_medium");
        this.artistName = jsonObject.getJSONObject("artist").getString("name");
        this.artistImage = jsonObject.getJSONObject("artist").getString("picture_small");
    }

    public Album(long id, String title, String artistName, long nb_tracks, String image, String artistImage) {
        this.id = id;
        this.title = title;
        this.artistName = artistName;
        this.nb_tracks = nb_tracks;
        this.image = image;
        this.artistImage = artistImage;
    }

    public Album() {
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artistName='" + artistName + '\'' +
                ", nb_tracks=" + nb_tracks +
                ", image='" + image + '\'' +
                ", artistImage='" + artistImage + '\'' +
                ", createdAt=" + createdAt +
                ", sharedUsername='" + sharedUsername + '\'' +
                '}';
    }
}
