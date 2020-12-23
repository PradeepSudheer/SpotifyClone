package com.example.mainactivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AlbumFragment extends Fragment {
    final String TAG = "TAG";
    Album currAlbum;
    TextView albumFragmentTitle, albumFragmentArtistName;
    ImageView albumFragmentImage, albumFragmentShare, albumFragmentArtistImage;
    RecyclerView albumFragmentTracksRecyclerView;
    TracksAdapter tracksAdapter;
    FirebaseAuth mAuth;

    static OkHttpClient client = new OkHttpClient();

    List<Track> allTracks = new ArrayList<>();

    public AlbumFragment(Album currAlbum) {
        // Required empty public constructor
        this.currAlbum = currAlbum;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        getActivity().setTitle("Album Fragment");
        albumFragmentTitle = view.findViewById(R.id.albumFragmentTitle);
        albumFragmentArtistName = view.findViewById(R.id.albumFragmentArtistName);
        albumFragmentImage = view.findViewById(R.id.albumFragmentImage);
        albumFragmentShare = view.findViewById(R.id.albumFragmentShare);
        albumFragmentArtistImage = view.findViewById(R.id.albumFragmentArtistImage);

        albumFragmentTitle.setText(currAlbum.title);
        albumFragmentArtistName.setText(currAlbum.artistName);
        Picasso.get().load(currAlbum.image).into(albumFragmentImage);
        Picasso.get().load(currAlbum.artistImage).into(albumFragmentArtistImage);


        albumFragmentTracksRecyclerView = view.findViewById(R.id.albumFragmentTracksRecyclerView);
        albumFragmentTracksRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false));
        tracksAdapter = new TracksAdapter(allTracks, new TracksAdapter.TrackAdapterInterface() {
            @Override
            public void trackHistory(Track track) {
                addToHistory(track);
            }
        });
        albumFragmentTracksRecyclerView.setAdapter(tracksAdapter);
        albumFragmentShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call Fragment share screen
                getActivity()
                        .getSupportFragmentManager()
                            .beginTransaction()
                                .replace(R.id.albumActivityContainerView, new AlbumSharingFragment(currAlbum))
                                    .addToBackStack(null).commit();
            }
        });

        //get Tracks
        getTracks();
        return view;
    }

    public void getTracks(){
        HttpUrl httpUrl = HttpUrl.parse("https://api.deezer.com/album").newBuilder()
                .addEncodedPathSegment(String.valueOf(currAlbum.id))
                .addEncodedPathSegment("tracks")
                .build();

        Request request = new Request.Builder().url(httpUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    allTracks.clear();

                    for(int i=0; i<jsonArray.length(); ++i){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Track track = new Track(obj);
                        allTracks.add(track);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tracksAdapter.notifyDataSetChanged();
                        }
                    });

                }catch(Exception e){

                }
            }
        });
    }

    public void addToHistory(Track track){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mapp1 = new HashMap<>();


        mapp1.put("trackID",track.id);
        mapp1.put("trackTitle",track.title);
        mapp1.put("albumID",currAlbum.id);
        mapp1.put("albumTitle",currAlbum.title);
        mapp1.put("nb_tracks",currAlbum.nb_tracks);
        mapp1.put("image",currAlbum.image);
        mapp1.put("artistImage",currAlbum.artistImage);
        mapp1.put("artistName",currAlbum.artistName);
        mapp1.put("sharedTime", new Timestamp(new Date()));

        Log.d(TAG, "addToHistory: "+currAlbum.artistName);

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("history")
                .document(String.valueOf(track.id)).set(mapp1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
            }
        });
    }

}