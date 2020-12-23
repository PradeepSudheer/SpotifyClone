package com.example.mainactivity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LikesFragment extends Fragment {

    List<Album> likedAlbums = new ArrayList<>();
    final String TAG = "TAG";
    RecyclerView likesFragmentRecyclerView;
    AlbumAdapter albumAdapter;
    LikesFragmentInterface likesFragmentInterface;
    private FirebaseAuth mAuth;


    public LikesFragment() {
        // Required empty public constructor
    }

    public interface LikesFragmentInterface{
        void goToAlbumActivity1(Album album);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        likesFragmentInterface = (LikesFragmentInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getLikes();
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        likesFragmentRecyclerView = view.findViewById(R.id.likesFragmentRecyclerView);
        likesFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        albumAdapter = new AlbumAdapter(likedAlbums, likedAlbums, new AlbumAdapter.AlbumCardViewInterface() {
            @Override
            public void callAlbumActivity(Album album) {
                likesFragmentInterface.goToAlbumActivity1(album);
            }

            @Override
            public void changeLikeDisLike(Album currAlbum) {
                mAuth = FirebaseAuth.getInstance();
                for(Album album: likedAlbums){
                    if(album.id == currAlbum.id){
                        Log.d(TAG, "changeLikeDisLike: albums matched");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("liked")
                                .document(String.valueOf(currAlbum.id)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getLikes();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: failed to remove like");
                            }
                        });
                        return ;
                    }
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                HashMap<String, Object> map = new HashMap<>();
                map.put("id",currAlbum.id);
                map.put("title", currAlbum.title);
                map.put("artistName", currAlbum.artistName);
                map.put("artistImage",currAlbum.artistImage);
                map.put("image",currAlbum.image);
                map.put("nb_tracks",currAlbum.nb_tracks);

                db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("liked").document(String.valueOf(currAlbum.id)).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getLikes();
                    }
                });
            }
        });

        likesFragmentRecyclerView.setAdapter(albumAdapter);
        return view;
    }

    public void getLikes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("liked")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        likedAlbums.clear();
                        for(QueryDocumentSnapshot ds: value){
                            Album album = new Album();
                            album.id = (Long) ds.getData().get("id");
                            album.title = (String) ds.getData().get("title");
                            album.artistName = (String) ds.getData().get("artistName");
                            album.nb_tracks = (Long) ds.getData().get("nb_tracks");
                            album.image = (String) ds.getData().get("image");
                            album.createdAt = (Timestamp) ds.getData().get("createdAt");
                            likedAlbums.add(album);
                        }
                        Log.d(TAG, "onEvent: "+likedAlbums);
                        albumAdapter.notifyDataSetChanged();
                    }
                });
    }
}