package com.timen4.ronnny.timemovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesFragment.ItemCallback {
    private final String LOG_TAG=MainActivity.class.getSimpleName();

    public boolean ismTwoPanel() {
        return mTwoPanel;
    }

    private boolean mTwoPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container)!=null){
            mTwoPanel=true;
            if (savedInstanceState==null){
                getSupportFragmentManager().beginTransaction().
                        add(R.id.detail_container,new MovieDetailFragment())
                        .commit();
            }
        }else{
            mTwoPanel=false;
//            getSupportActionBar().setElevation(0f);
        }


        MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.menu_container);

        if (moviesFragment==null){
            moviesFragment=new MoviesFragment(mTwoPanel);
        }
        getSupportFragmentManager().beginTransaction().
                replace(R.id.menu_container,moviesFragment)
                .commit();

        MovieSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelect(MovieResult.MovieInfo data) {
        if (mTwoPanel){
            Bundle bundle=new Bundle();
            bundle.putParcelable("movieObject",data);
            MovieDetailFragment detailFragment=new MovieDetailFragment(mTwoPanel);
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.detail_container,detailFragment)
                    .commit();
        }else{
            Intent intent= new Intent(this,DetailActivity.class);
                intent.putExtra("movieObject",data);
                startActivity(intent);
        }

    }

}
