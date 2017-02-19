package com.timen4.ronnny.timemovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by :luore
 * Date: 2017/2/17
 */

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.detail_container,new MovieDetailFragment()).commit();

        }
    }
}
