package com.timen4.ronnny.timemovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.squareup.picasso.Picasso;
import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.bean.MovieReview_Table;
import com.timen4.ronnny.timemovies.bean.MovieTrailer_Table;
import com.timen4.ronnny.timemovies.bean.ReviewResult;
import com.timen4.ronnny.timemovies.bean.TrailerResult;
import com.timen4.ronnny.timemovies.db.AppDatabase;
import com.timen4.ronnny.timemovies.utils.Utility;
import com.timen4.ronnny.timemovies.view.NestedListView;

import java.util.List;

/**
 * Created by :luore
 * Date: 2017/2/17
 */

public class MovieDetailFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    //https://image.tmdb.org/t/p/w185/hxGAYQDrvOH1kTlCGKlP0NE4Gar.jpg
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";
    private ImageButton mFavorite;
    private MovieResult.MovieInfo mMovieItem;

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
        View rootView =inflater.inflate(R.layout.fragment_detail,container,false);
        mMovieItem = getActivity().getIntent().getParcelableExtra("movieObject");

        //给页面设置工具栏
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar_layout);
        ImageView movie_pic = (ImageView) rootView.findViewById(R.id.movie_pic);
        TextView tv_score = (TextView) rootView.findViewById(R.id.movie_score);
        TextView tv_describe = (TextView) rootView.findViewById(R.id.movie_describe);
        TextView tv_year = (TextView) rootView.findViewById(R.id.movie_year);
        ImageView toolbar_image = (ImageView) rootView.findViewById(R.id.toolbar_image);
        TextView movie_time = (TextView) rootView.findViewById(R.id.movie_time);
        mFavorite = (ImageButton) rootView.findViewById(R.id.favorite);
        NestedListView lv_comment = (NestedListView) rootView.findViewById(R.id.lv_comment);
        NestedListView lv_trailer = (NestedListView) rootView.findViewById(R.id.lv_trailer);
        NestedScrollView scrollView = (NestedScrollView) rootView.findViewById(R.id.scrollView);


        if (mMovieItem !=null){

            mFavorite.setOnClickListener(this);
            collapsingToolbar.setTitle(mMovieItem.getTitle());
            tv_year.setText("上映："+ mMovieItem.getRelease_date().substring(0,4));
            tv_score.setText("评分："+ mMovieItem.getVote_average()+"/10");
            movie_time.setText("时长："+ mMovieItem.getTime()+"min");
            tv_describe.setText(mMovieItem.getOverview());
            if (mMovieItem.isFavorite()){
                mFavorite.setBackgroundResource(R.drawable.favorite_select);
            }else {
                mFavorite.setBackgroundResource(R.drawable.favorite_normal);
            }
            TrailerAdapter trailerAdapter =new TrailerAdapter(getTrailersFromDB(mMovieItem.getId()),getActivity());
            lv_trailer.setAdapter(trailerAdapter);
            lv_trailer.setOnItemClickListener(this);

            ReviewAdapter reviewAdapter=new ReviewAdapter(getReviewsFromDB(mMovieItem.getId()),getActivity());
            lv_comment.setAdapter(reviewAdapter);

            if (!Utility.checkNetIsConnected(getActivity())){
                Toast.makeText(getActivity(),"当前网络不可用，请链接网络后重试",Toast.LENGTH_SHORT).show();
            }
            loadPic(mMovieItem, movie_pic, toolbar_image);
        }
        return rootView;
    }


    private List<ReviewResult.MovieReview> getReviewsFromDB(int movieId) {
        List<ReviewResult.MovieReview> movieReviews = ContentUtils.queryList(getActivity().getContentResolver(),
                AppDatabase.ReviewProviderModel.CONTENT_URI,
                ReviewResult.MovieReview.class,
                ConditionGroup.clause().and(MovieReview_Table.movie_id.is(movieId)), null);
        if (movieReviews.size()==0){
            ReviewResult.MovieReview movieReview = new ReviewResult.MovieReview();
            movieReview.setContent(getString(R.string.no_comment_tip));
            movieReviews.add(movieReview);
        }
        return movieReviews;
    }

    private List<TrailerResult.MovieTrailer> getTrailersFromDB(int movieId) {
        List<TrailerResult.MovieTrailer> movieTrailers = ContentUtils.queryList(getActivity().getContentResolver(),
                AppDatabase.TrailerProvidermodel.CONTENT_URI,
                TrailerResult.MovieTrailer.class,
                ConditionGroup.clause().and(MovieTrailer_Table.movie_id.is(movieId)), null);
        if (movieTrailers.size()==0){
            TrailerResult.MovieTrailer movieReview = new TrailerResult.MovieTrailer();
            movieReview.setName(getString(R.string.no_trailer_tip));
            movieTrailers.add(movieReview);
        }
        return movieTrailers;
    }

    private void loadPic(MovieResult.MovieInfo mMovieItem, ImageView movie_pic, ImageView toolbar_image) {
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.favorite:
                if (mMovieItem.isFavorite()){
                    mFavorite.setBackgroundResource(R.drawable.favorite_normal);
                }else {
                    mFavorite.setBackgroundResource(R.drawable.favorite_select);
                }
                mMovieItem.setFavorite(!mMovieItem.isFavorite());
                ContentUtils.update(getActivity().getContentResolver(), AppDatabase.MovieProviderModel.CONTENT_URI,mMovieItem);
                break;
        }
    }
    //https://www.youtube.com/watch?v=N8vevpRyicY
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TrailerResult.MovieTrailer item = (TrailerResult.MovieTrailer) parent.getAdapter().getItem(position);
        if (item.getKey()==null){
            return;
        }
        String youtobe_key = item.getKey();
        Uri uri = Uri.parse("https://www.youtube.com/watch?v="+youtobe_key);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);


    }
}
