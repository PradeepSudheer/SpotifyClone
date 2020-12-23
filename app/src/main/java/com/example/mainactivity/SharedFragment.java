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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SharedFragment extends Fragment {

    RecyclerView sharedFragmentRecyclerView;
    SharedFragmentAdapter sharedFragmentAdapter;
    List<Album> allAlbumsShared = new ArrayList<>();
    FirebaseAuth mAuth;
    SharedFragmentInterface sharedFragmentInterface;
    final String TAG = "TAG";

    public SharedFragment() {
        // Required empty public constructor
    }

    public interface SharedFragmentInterface{
        void callToAlbumActivity(Album album);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedFragmentInterface = (SharedFragmentInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shared, container, false);
        sharedFragmentRecyclerView = view.findViewById(R.id.sharedFragmentRecyclerView);
        sharedFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        sharedFragmentAdapter = new SharedFragmentAdapter(allAlbumsShared, new SharedFragmentAdapter.SharedFragmentAdapterInterface() {
            @Override
            public void callAlbumActivity(Album album) {
                sharedFragmentInterface.callToAlbumActivity(album);
            }
        });
        sharedFragmentRecyclerView.setAdapter(sharedFragmentAdapter);
        getAllAlbumsShared();
        return view;
    }

    public void getAllAlbumsShared(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                    .collection("shared")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                allAlbumsShared.clear();
                                for(QueryDocumentSnapshot ds: value){
                                    Album album = new Album();
                                    album.id = (long) ds.getData().get("albumID");
                                    album.title = (String) ds.getData().get("albumTitle");
                                    album.image = (String) ds.getData().get("image");
                                    album.nb_tracks = (Long) ds.getData().get("nb_tracks");
                                    album.artistName = (String) ds.getData().get("artistName");
                                    album.artistImage = (String) ds.getData().get("artistImage");
                                    album.sharedUsername = (String) ds.getData().get("sharedUsername");
                                    album.createdAt = (Timestamp) ds.getData().get("sharedTime");

                                    allAlbumsShared.add(album);
                                }
                                sharedFragmentAdapter.notifyDataSetChanged();
                            }
                        });
    }
}