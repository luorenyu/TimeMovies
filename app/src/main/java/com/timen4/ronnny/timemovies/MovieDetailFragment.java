package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by :luore
 * Date: 2017/2/17
 */

public class MovieDetailFragment extends Fragment {
    //https://image.tmdb.org/t/p/w185/hxGAYQDrvOH1kTlCGKlP0NE4Gar.jpg
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";

    public MovieDetailFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview =inflater.inflate(R.layout.fragment_detail,container,false);
        MovieResults.ResultsBean mMovieItem = getActivity().getIntent().getParcelableExtra("movieObject");

        //给页面设置工具栏
        final Toolbar toolbar = (Toolbar) rootview.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) rootview.findViewById(R.id.collapsing_toolbar_layout);

        if (mMovieItem!=null){
            ImageView movie_pic = (ImageView) rootview.findViewById(R.id.movie_pic);
            TextView tv_score = (TextView) rootview.findViewById(R.id.movie_score);
            TextView tv_describle = (TextView) rootview.findViewById(R.id.movie_describle);
            TextView tv_year = (TextView) rootview.findViewById(R.id.movie_year);
            ImageView toolbar_image = (ImageView) rootview.findViewById(R.id.toolbar_image);
            collapsingToolbar.setTitle(mMovieItem.getTitle());
            tv_year.setText("上映时间："+mMovieItem.getRelease_date().substring(0,4));
            tv_score.setText("评分："+mMovieItem.getVote_average()+"/10");
            tv_describle.setText(mMovieItem.getOverview());
            if (!isOnline()){
                Toast.makeText(getActivity(),"当前网络不可用，请链接网络后重试",Toast.LENGTH_SHORT).show();
            }

            Picasso.with(getActivity())
                    .load(IMAGE_BASE_URL+"/w780"+ mMovieItem.getBackdrop_path())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(toolbar_image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
//                            Toast.makeText(getActivity(),"加载成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(){
                            Toast.makeText(getActivity(),"信息暂时无法加载",Toast.LENGTH_SHORT).show();
                        }
                    });
            Picasso.with(getActivity())
                    .load(IMAGE_BASE_URL +"/w342"+ mMovieItem.getPoster_path())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(movie_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
//                            Toast.makeText(getActivity(),"加载成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(){
                            Toast.makeText(getActivity(),"信息暂时无法加载",Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return rootview;
    }
    /**
     * 判断当前网络是否可用
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
