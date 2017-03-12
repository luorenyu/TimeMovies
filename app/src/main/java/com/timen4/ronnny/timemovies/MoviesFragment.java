package com.timen4.ronnny.timemovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.timen4.ronnny.timemovies.Helper.DataHelper;
import com.timen4.ronnny.timemovies.bean.MovieInfo_Table;
import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.db.AppDatabase;
import com.timen4.ronnny.timemovies.sync.MovieSyncAdapter;
import com.timen4.ronnny.timemovies.utils.SharedPreferencesUtils;
import com.timen4.ronnny.timemovies.utils.ToastUtil;
import com.timen4.ronnny.timemovies.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ronny on 2017/3/4.
 */

public class MoviesFragment extends Fragment {
    private String TAG=MoviesFragment.class.getSimpleName();
    private DataHelper mDataHelper;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private int mColumNum =2;
    private boolean isPopular=true;//判断是获取热门信息还是高分信息
    private int MOVIES_LOADER =0;
    private FlowContentObserver mMovieInfoObserver;
    private boolean mTwoPanel;
    private int mPosition=0;
    public static final String SELECTED_KEY = "selected_position";


    public MoviesFragment() {
    }
    public MoviesFragment(boolean mTwoPanel) {
        this.mTwoPanel=mTwoPanel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG,"onCreate"+"__mPosition:"+mPosition);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
//                mDataHelper.pullMovieBicInfo(getActivity());
                MovieSyncAdapter.syncImmediately(getActivity());
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
                if (mTwoPanel){
                    ((ItemCallback)getActivity()).onItemSelect(FillDataFromDB(true).get(0));
                }
                mAdapter.setData(FillDataFromDB(true));
                mAdapter.notifyDataSetChanged();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.e(TAG,"onResume");
        super.onResume();
        if (mAdapter.getData()==null){
            mAdapter.setData(FillDataFromDB(false));
            mAdapter.notifyDataSetChanged();
        }else {
            mAdapter.setClickItemPosition(mPosition);
            mAdapter.notifyDataSetChanged();
            if (mTwoPanel){
                mRecyclerView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e(TAG,"onActivitycreated"+mPosition);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.e(TAG,"onStart");
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.rv_movies);
        if (getActivity()!=null&&((MainActivity)getActivity()).ismTwoPanel()){
            mLayoutManager = new LinearLayoutManager(getContext());
        }else{
            mLayoutManager = new GridLayoutManager(getContext(), mColumNum);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        //create a adapter and set it to RecyclerView
        mAdapter = new MoviesAdapter(FillDataFromDB(false),getContext());
        mPosition = (int) SharedPreferencesUtils.getParam(getActivity(), SELECTED_KEY, 0);
        if (mTwoPanel&&FillDataFromDB(false)!=null&&FillDataFromDB(false).size()>0){
            ((ItemCallback)getActivity()).onItemSelect(FillDataFromDB(false).get(mPosition));
        }
//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            // The listview probably hasn't even been populated yet.  Actually perform the
//            // swapout in onLoadFinished.
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
//            mAdapter.setClickItemPosition(mPosition);
//        }
        Log.e(TAG,"onCreateview"+mPosition);
        mRecyclerView.setAdapter(mAdapter);
        //listen db
        mMovieInfoObserver.addModelChangeListener(new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {
                mAdapter.setData(FillDataFromDB(false));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Boolean islisten = (Boolean) SharedPreferencesUtils.getParam(getActivity(),
//                                SharedPreferencesUtils.LISTEN_FIRST_ITEM_TDATA, false);
//                        if (mTwoPanel&&islisten){
//                            MovieResult.MovieInfo movieInfo = ContentUtils.queryList(getActivity().getContentResolver()
//                                    , AppDatabase.MovieProviderModel.CONTENT_URI
//                                    , MovieResult.MovieInfo.class, ConditionGroup.clause(), null,new String[]{}).get(0);
//                            ((ItemCallback)getActivity()).onItemSelect(movieInfo);
//                        }
                        mAdapter.notifyDataSetChanged();
                        if (mTwoPanel){
                            ((ItemCallback)getActivity()).onItemSelect(FillDataFromDB(false).get(mPosition));
                        }
                    }
                });
            }
        });
        if (mAdapter!=null){
            mAdapter.setTwoPanel(mTwoPanel);
        }
        mAdapter.setOnItemClickListener(new MoviesAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position, View view, MovieResult.MovieInfo data) {
                if (mTwoPanel){
//                    view.setActivated(true);
//                    view.setBackgroundResource(R.color.colorPrimary);
                    mAdapter.setClickItemPosition(position);
                    mAdapter.notifyDataSetChanged();
                }
                if (!Utility.checkNetIsConnected(getActivity()) && data.getRelease_date()==null){
                    ToastUtil.show(getActivity(),getString(R.string.no_network_tip));
                    return;
                }
                ((ItemCallback)getActivity()).onItemSelect(data);
                mPosition = position;
                SharedPreferencesUtils.setParam(getActivity(),SELECTED_KEY,mPosition);
//                Bundle b = getArguments();
//                if(b != null){
//                    b.putInt(SELECTED_KEY, mPosition);  //更新参数
//                }

            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
//        if (mPosition != ListView.INVALID_POSITION) {
//            outState.putInt(SELECTED_KEY, mPosition);
//        }

        Log.e(TAG,"onSaveInstanceState"+outState.get(SELECTED_KEY));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy");
        super.onDestroy();
        mMovieInfoObserver.unregisterForContentChanges(getActivity());
    }

    private List<MovieResult.MovieInfo> FillDataFromDB(boolean isFavorite) {
        List<MovieResult.MovieInfo> movies= new ArrayList();
        String sort = Utility.getPreferedSort(getActivity());
        if (isFavorite){
            if (sort.equals("popular")){
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
//                mDataHelper.pullMovieBicInfo(getActivity());
//                MovieSyncAdapter.syncImmediately(getActivity());
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

    public void setTwoPanel(boolean mTwoPanel) {
        this.mTwoPanel = mTwoPanel;
        if (mAdapter!=null){
            mAdapter.setTwoPanel(mTwoPanel);
        }
    }
    public interface ItemCallback {
        public void onItemSelect(MovieResult.MovieInfo data);
    }
}

