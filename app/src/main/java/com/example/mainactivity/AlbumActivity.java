package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Intent intent = getIntent();
        Album currAlbum = (Album) intent.getSerializableExtra("Album");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.albumActivityContainerView,new AlbumFragment(currAlbum)).commit();

    }
}