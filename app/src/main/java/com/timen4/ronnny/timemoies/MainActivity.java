package com.timen4.ronnny.timemoies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
    
    
    public static class MoviesFragment extends Fragment {
        private RecyclerView mRecyclerView;
        private GridLayoutManager mLayoutManager;
        private MyAdapter mAdapter;
        private int mColumNum =2;

        public MoviesFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
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
                    updateMoies();
                    return true;
                case R.id.action_setting:
                    //跳转到设置界面
                    Intent intent=new Intent(getContext(),SettingActivity.class);
                    getActivity().startActivity(intent);
                    return true;
            }
            return super.onOptionsItemSelected(item);
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
            mAdapter = new MyAdapter(getMoiesFromNet());
            mRecyclerView.setAdapter(mAdapter);


            return rootview;

        }

        private ArrayList<MovieItem> getMoiesFromNet() {
            ArrayList<MovieItem> moies= new ArrayList<>();
            for (int i=0;i<10;i++){
                moies.add(new MovieItem("lala land"+i,0.4+(double) i));
            }
            return moies;
        }

        //deal request internet data logic
        private void updateMoies() {
        }
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public ArrayList<MovieItem> movies = null;
        public MyAdapter(ArrayList<MovieItem> movies) {
            this.movies = movies;
        }
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            MovieItem movie=movies.get(position);
            viewHolder.mTv_score.setText(movie.getVote_average()+"");
            viewHolder.mTv_title.setText(movie.getTitle());
            viewHolder.mIv_pic.setImageResource(R.mipmap.ic_launcher);
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return movies.size();
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public  class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTv_score;
            private TextView mTv_title;

            private ImageView mIv_pic;
            public ViewHolder(View view){
                super(view);
                mTv_score = (TextView) view.findViewById(R.id.movie_score);
                mTv_title = (TextView) view.findViewById(R.id.movie_title);
                mIv_pic = (ImageView) view.findViewById(R.id.movie_pic);
            }
        }
    }
}
