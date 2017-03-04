package com.timen4.ronnny.timemovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.timen4.ronnny.timemovies.Helper.DataHelper;
import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.db.AppDatabase;
import com.timen4.ronnny.timemovies.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ronny on 2017/3/4.
 */

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    private DataHelper mDataHelper;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private MainActivity.MyAdapter mAdapter;
    private int mColumNum =2;
    private boolean isPopular=true;//判断是获取热门信息还是高分信息
    private int MOVIES_LOADER =0;


    public MoviesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDataHelper=new DataHelper(getContext());
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, null);
        mRecyclerView = (RecyclerView)rootview.findViewById(R.id.rv_movies);
        //创建默认的线性LayoutManager
//            mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), mColumNum);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//            mRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        mAdapter = new MainActivity.MyAdapter(FillDataFromDB(),getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MainActivity.MyAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, MovieResult.MovieInfo data) {
                if (!Utility.checkNetIsConnected(getActivity()) && data.getRelease_date()==null){
                    Toast.makeText(getActivity(),"暂无网络连接，建议重新连接网络后重试",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent= new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("movieObject",data);
                startActivity(intent);
            }
        });
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);

    }

    private List<MovieResult.MovieInfo> FillDataFromDB() {
        boolean isFirst = Utility.checkNetIsConnected(getActivity());
        List<MovieResult.MovieInfo> movies= new ArrayList();
        if (isFirst){
            mDataHelper.pullMovieBicInfo(getActivity());
            for (int i=0;i<4;i++){
                movies.add(new MovieResult.MovieInfo("正在加载中。。。",0.0));
            }
        }else{
             movies = ContentUtils.queryList(AppDatabase.MovieProviderModel.CONTENT_URI, MovieResult.MovieInfo.class, null, null,new String[]{});
        }
        return movies;
    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
//        String sort = Utility.getPreferedSort(getActivity());
//
//        // SortResult order:  Ascending, by date.
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());
//
//        return new CursorLoader(getActivity(),
//                weatherForLocationUri,
//                FORECAST_COLUMNS,
//                null,
//                null,
//                sortOrder);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}

