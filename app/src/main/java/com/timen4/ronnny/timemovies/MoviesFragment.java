package com.timen4.ronnny.timemovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.timen4.ronnny.timemovies.Helper.DataHelper;
import com.timen4.ronnny.timemovies.bean.MovieInfo_Table;
import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.db.AppDatabase;
import com.timen4.ronnny.timemovies.utils.ToastUtil;
import com.timen4.ronnny.timemovies.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ronny on 2017/3/4.
 */

public class MoviesFragment extends Fragment {
    private DataHelper mDataHelper;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private int mColumNum =2;
    private boolean isPopular=true;//判断是获取热门信息还是高分信息
    private int MOVIES_LOADER =0;
    private FlowContentObserver mMovieInfoObserver;


    public MoviesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDataHelper=new DataHelper(getContext());
        mMovieInfoObserver= new FlowContentObserver();
        mMovieInfoObserver.registerForContentChanges(getActivity(), MovieResult.MovieInfo.class);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        switch (itemId){
            case R.id.action_refresh:
                mDataHelper.pullMovieBicInfo(getActivity());
                return true;
            case R.id.action_setting:
                //跳转到设置界面
                Intent intent=new Intent(getContext(),SettingActivity.class);
                getActivity().startActivity(intent);
                return true;
            case R.id.action_favorite:
                List<MovieResult.MovieInfo> movieInfoList = FillDataFromDB(true);
                if (movieInfoList.size()==0){
                    ToastUtil.show(getActivity(),getString(R.string.favorite_tip));
                    return true;
                }
                mAdapter.setData(FillDataFromDB(true));
                mAdapter.notifyDataSetChanged();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setData(FillDataFromDB(false));
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.rv_movies);
        //创建默认的线性LayoutManager
//            mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), mColumNum);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMovieInfoObserver.addModelChangeListener(new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {
                mAdapter.setData(FillDataFromDB(false));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        //创建并设置Adapter
        mAdapter = new MoviesAdapter(FillDataFromDB(false),getContext());

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MoviesAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, MovieResult.MovieInfo data) {
                if (!Utility.checkNetIsConnected(getActivity()) && data.getRelease_date()==null){
                    ToastUtil.show(getActivity(),getString(R.string.no_network_tip));
                    return;
                }
                Intent intent= new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("movieObject",data);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMovieInfoObserver.unregisterForContentChanges(getActivity());
    }

    private List<MovieResult.MovieInfo> FillDataFromDB(boolean isFavorite) {
        List<MovieResult.MovieInfo> movies= new ArrayList();
        String sort = Utility.getPreferedSort(getActivity());
        if (isFavorite){
            if (sort=="popular"){
                movies = ContentUtils.queryList(AppDatabase.MovieProviderModel.CONTENT_URI,
                        MovieResult.MovieInfo.class,
                        ConditionGroup.clause().and(MovieInfo_Table.favorite.is(true)),
                        AppDatabase.MovieProviderModel.MOVIE_POPULAR+" DESC",new String[]{});
            }else{
                movies =  ContentUtils.queryList(AppDatabase.MovieProviderModel.CONTENT_URI,
                        MovieResult.MovieInfo.class,
                        ConditionGroup.clause().and(MovieInfo_Table.favorite.is(true)),
                        AppDatabase.MovieProviderModel.MOVIE_SCORE+" DESC",new String[]{});
            }

        }else{
            boolean isFirst = Utility.isFirstInApp(getActivity());
            if (isFirst){
                mDataHelper.pullMovieBicInfo(getActivity());
                for (int i=0;i<4;i++){
                    movies.add(new MovieResult.MovieInfo(getString(R.string.loading),0.0));
                }
            }else{
                if (sort.equals("popular")){
                    movies = ContentUtils.queryList(AppDatabase.MovieProviderModel.CONTENT_URI,
                            MovieResult.MovieInfo.class, ConditionGroup.clause(),
                            AppDatabase.MovieProviderModel.MOVIE_POPULAR+" DESC",new String[]{});
                }else{
                    movies = ContentUtils.queryList(AppDatabase.MovieProviderModel.CONTENT_URI,
                            MovieResult.MovieInfo.class, ConditionGroup.clause(),
                            AppDatabase.MovieProviderModel.MOVIE_SCORE+" DESC",new String[]{});
                }
            }
        }

        return movies;
    }

}

