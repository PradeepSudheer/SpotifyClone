package com.example.mainactivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumSharingFragment extends Fragment {

    Album currAlbum;
    TextView albumSharingAlbumTitle;
    ListView albumSharingListView;
    List<String> usernames = new ArrayList<>();
    final String TAG = "TAG";
    private FirebaseAuth mAuth;
    HashMap<String,String> map;
    ArrayAdapter<String> adapter;


    public AlbumSharingFragment(Album currAlbum) {
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

        View view = inflater.inflate(R.layout.fragment_album_sharing, container, false);
        albumSharingAlbumTitle = view.findViewById(R.id.albumSharingAlbumTitle);
        albumSharingListView = view.findViewById(R.id.albumSharingListView);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, android.R.id.text1, usernames);
        albumSharingListView.setAdapter(adapter);

        albumSharingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sharedUserID = map.get(usernames.get(position));
                addToSharedList(sharedUserID, usernames.get(position));
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        getUserNames();

        return view;
    }

    public void getUserNames(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        map = new HashMap<>();
        usernames.clear();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        if(!documentSnapshot.getId().equals(mAuth.getCurrentUser().getUid())){
                            map.put(String.valueOf(documentSnapshot.getData().get("username")) ,documentSnapshot.getId());
                            usernames.add(String.valueOf(documentSnapshot.getData().get("username")));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else{

                }
            }
        });

    }

    public void addToSharedList(String sharedUserID, String username){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mapp1 = new HashMap<>();

        mapp1.put("albumID",currAlbum.id);
        mapp1.put("albumTitle",currAlbum.title);
        mapp1.put("nb_tracks",currAlbum.nb_tracks);
        mapp1.put("image",currAlbum.image);
        mapp1.put("artistImage",currAlbum.artistImage);
        mapp1.put("artistName",currAlbum.artistName);
        mapp1.put("sharedUsername",username);
        mapp1.put("sharedTime", new Timestamp(new Date()));

        db.collection("users")
                .document(sharedUserID)
                    .collection("shared")
                        .document(String.valueOf(currAlbum.id)).set(mapp1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
            }
        });

    }
}