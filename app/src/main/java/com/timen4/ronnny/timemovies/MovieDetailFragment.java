package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

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
        MovieResults.ResultsBean mMovieItem = (MovieResults.ResultsBean) getActivity().getIntent().getSerializableExtra("movieObject");
        if (mMovieItem!=null){
            ImageView movie_pic = (ImageView) rootview.findViewById(R.id.movie_pic);
            TextView tv_title = (TextView) rootview.findViewById(R.id.movie_title);
            TextView tv_score = (TextView) rootview.findViewById(R.id.movie_score);
            TextView tv_describle = (TextView) rootview.findViewById(R.id.movie_describle);
            TextView tv_year = (TextView) rootview.findViewById(R.id.movie_year);
            tv_title.setText(mMovieItem.getTitle());
            tv_year.setText("上映时间："+mMovieItem.getRelease_date().substring(0,4));
            tv_score.setText("评分："+mMovieItem.getVote_average()+"/10");
            tv_describle.setText(mMovieItem.getOverview());
            if (!isOnline()){
                Toast.makeText(getActivity(),"当前网络不可用，请链接网络后重试",Toast.LENGTH_SHORT).show();
            }
            Picasso.with(getContext())
                    .load(IMAGE_BASE_URL+mMovieItem.getPoster_path())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(movie_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
//                            Toast.makeText(mContext,"加载成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(){
                            Toast.makeText(getContext(),"信息暂时无法加载",Toast.LENGTH_SHORT).show();
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
