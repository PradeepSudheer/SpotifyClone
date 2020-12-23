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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {
    final String TAG = "TAG";
    static OkHttpClient client = new OkHttpClient();
    EditText searchET;
    Button searchButton;
    List<Album> allAlbums = new ArrayList<>();
    List<Album> likedAlbums = new ArrayList<>();
    AlbumAdapter albumAdapter;
    RecyclerView albumRecyclerView;
    private FirebaseAuth mAuth;

    SearchFragmentInterface searchFragmentInterface;
    public SearchFragment() {
        // Required empty public constructor
        Log.d(TAG, "SearchFragment: "+likedAlbums);
    }



    public interface SearchFragmentInterface{
        void goToAlbumActivity(Album album);
        void goToMainActivityForLikeDisLike(Album album);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        searchFragmentInterface = (SearchFragmentInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getLikes();
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchET = view.findViewById(R.id.searchEt);
        searchButton = view.findViewById(R.id.searchButton);
        albumRecyclerView = view.findViewById(R.id.searchRecyclerView);
        albumAdapter = new AlbumAdapter(allAlbums, likedAlbums, new AlbumAdapter.AlbumCardViewInterface() {
            @Override
            public void callAlbumActivity(Album album) {
                searchFragmentInterface.goToAlbumActivity(album);
            }

            @Override
            public void changeLikeDisLike(Album currAlbum) {
                Log.d(TAG, "changeLikeDisLike: "+likedAlbums.size());
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
                mAuth = FirebaseAuth.getInstance();
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

        albumRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        albumRecyclerView.setAdapter(albumAdapter);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchET.getText().toString()
                final String albumName = searchET.getText().toString();
                if(albumName.length() == 0){
                    Toast.makeText(getContext(),"please enter album name to search!!",Toast.LENGTH_LONG).show();
                }else{
                    getAlbums(albumName);
                }
            }
        });


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

    private void getAlbums(String albumName){
        HttpUrl httpUrl = HttpUrl.parse("https://api.deezer.com/search/album").newBuilder()
                .addEncodedQueryParameter("q",String.valueOf(albumName))
                .build();

        Request request = new Request.Builder().url(httpUrl).build();
        Log.d(TAG, "getAlbums: ");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(getContext(),"Failed to fetch albums",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    allAlbums.clear();
                    for(int i=0;i< jsonArray.length();++i){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Album album = new Album(obj);
                        allAlbums.add(album);
                    }
                    
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.d(TAG, "onResponse: "+allAlbums.size());
                            albumAdapter.notifyDataSetChanged();
                            Log.d(TAG, "run: ");
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    //Log.d(TAG, "onResponse: exception"+e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"Failed to compute response",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}