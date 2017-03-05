package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.timen4.ronnny.timemovies.bean.MovieResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MoviesFragment()).commit();
        }

    }



}
