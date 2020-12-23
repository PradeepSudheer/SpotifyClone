package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchFragment.SearchFragmentInterface,
        LikesFragment.LikesFragmentInterface, SharedFragment.SharedFragmentInterface {

    private FirebaseAuth mAuth;
    final String TAG = "TAG";

    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;

    List<Album> likedAlbums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
            Intent intent = new Intent(MainActivity.this,AuthActivity.class);
            startActivity(intent);
            finish();
        }


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position){
                    case 0:
                        tab.setText("Search");
                        break;
                    case 1:
                        tab.setText("Likes");
                        break;
                    case 2:
                        tab.setText("History");
                        break;
                    case 3:
                        tab.setText("Shared");
                        break;
                }
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int option_id = item.getItemId();
        if(option_id == R.id.logoutid){
            mAuth.signOut();
            Intent intent = new Intent(this,AuthActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToAlbumActivity(Album album) {
        Intent intent = new Intent(MainActivity.this,AlbumActivity.class);
        intent.putExtra("Album", album);
        startActivity(intent);
    }

    @Override
    public void goToMainActivityForLikeDisLike(Album album) {

    }
    //long id, String title, String artistName, long nb_tracks, String image, String artistImage
    @Override
    public void goToAlbumActivity1(Album album) {
        Intent intent = new Intent(MainActivity.this,AlbumActivity.class);
        Album curralbum = new Album(album.id, album.title,album.artistName,album.nb_tracks,album.image,album.artistImage);
        intent.putExtra("Album", curralbum);
        startActivity(intent);
    }

    @Override
    public void callToAlbumActivity(Album album) {
        Intent intent = new Intent(MainActivity.this,AlbumActivity.class);
        Album curralbum = new Album(album.id, album.title,album.artistName,album.nb_tracks,album.image,album.artistImage);
        intent.putExtra("Album",  curralbum);
        Log.d(TAG, "callToAlbumActivity: "+album);
        startActivity(intent);
    }


    public class ViewPagerAdapter extends FragmentStateAdapter{

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position){
                case 0:
                    Log.d(TAG, "createFragment: 0");
                    return new SearchFragment();
                case 1:
                    Log.d(TAG, "createFragment: 1");
                    return new LikesFragment();

                case 2:
                    Log.d(TAG, "createFragment: 3");
                    return new HistoryFragment();

                case 3:
                    Log.d(TAG, "createFragment: 4");
                    return new SharedFragment();

            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}