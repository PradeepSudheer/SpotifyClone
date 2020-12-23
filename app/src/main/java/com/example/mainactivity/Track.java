package com.example.mainactivity;

import com.google.firebase.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Track implements Serializable {
    long id, nb_tracks;
    long albumId;
    String title;
    String duration1;
    String preview;
    String albumTitle;
    String artistName;
    Timestamp createdAt;
    String image;
    public Track(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.title = obj.getString("title");
        int duration = obj.getInt("duration");
        StringBuilder sb = new StringBuilder();
        sb.append(duration/60 +":");
        sb.append(duration%60 + " ");
        this.duration1 = sb.toString();
        this.preview = obj.getString("preview");
    }

    public Track() {
    }

    @Override
    public String toString() {
        return "Track{" +
                "title='" + title + '\'' +
                ", duration1='" + duration1 + '\'' +
                '}';
    }
}
