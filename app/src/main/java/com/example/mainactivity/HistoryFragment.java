package com.example.mainactivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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


public class HistoryFragment extends Fragment {

    RecyclerView historyRecyclerView;
    Button clearHistoryButton;
    HistoryFragmentAdapter historyFragmentAdapter;
    List<Track> tracksHistory = new ArrayList<>();
    FirebaseAuth mAuth;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        historyFragmentAdapter = new HistoryFragmentAdapter(tracksHistory);
        historyRecyclerView.setAdapter(historyFragmentAdapter);
        getTrackHistory();
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });
        return view;
    }

    public void clearHistory(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(Track track:tracksHistory){
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .collection("history").document(String.valueOf(track.id)).delete();
        }
        tracksHistory.clear();
    }

    public void getTrackHistory(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("history").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                tracksHistory.clear();
                for (QueryDocumentSnapshot documentSnapshot: value) {
                    Track track = new Track();
                    track.id = (long) documentSnapshot.getData().get("trackID");
                    track.albumId = (long) documentSnapshot.getData().get("albumID");
                    track.albumTitle = (String) documentSnapshot.getData().get("albumTitle");
                    track.title = (String) documentSnapshot.getData().get("trackTitle");
                    track.nb_tracks = (long) documentSnapshot.getData().get("nb_tracks");
                    track.createdAt = (Timestamp) documentSnapshot.getData().get("sharedTime");
                    track.image = (String) documentSnapshot.getData().get("image");
                    track.artistName = (String) documentSnapshot.getData().get("artistName");

                    tracksHistory.add(track);
                }
                historyFragmentAdapter.notifyDataSetChanged();
            }

        });
    }
}