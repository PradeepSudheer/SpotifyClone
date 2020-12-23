package com.example.mainactivity;

import com.google.firebase.Timestamp;

public class LikeAlbumClass {
    Album album;
    Timestamp createdAt;

    public LikeAlbumClass(Album album, Timestamp createdAt) {
        this.album = album;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LikeAlbumClass{" +
                "album=" + album +
                ", createdAt=" + createdAt +
                '}';
    }
}
