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

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
        public List<MovieResult.MovieInfo> movies = null;
        private Context mContext;
        private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
        private OnRecyclerViewItemClickListener mOnItemClickListener = null;

        public MyAdapter(List<MovieResult.MovieInfo> movies, Context context) {
            this.mContext=context;
            this.movies = movies;
        }

        public void setDatas(ArrayList<MovieResult.MovieInfo> movies){
            this.movies=movies;
        }

        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            //将创建的View注册点击事件
            view.setOnClickListener( this);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final MovieResult.MovieInfo movie = movies.get(position);
            viewHolder.mTv_score.setText("评分："+ movie.getVote_average()+"/10");
            viewHolder.mTv_title.setText("影片："+ movie.getTitle());
            //https://image.tmdb.org/t/p/w185/fMlSFgIB4Kr7YmuqNLHEWN2dkBG.jpg
            viewHolder.mIv_pic.setImageResource(R.mipmap.ic_launcher);
            Picasso.with(mContext)
                    .load(IMAGE_BASE_URL+movie.getPoster_path())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(viewHolder.mIv_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
//                            Toast.makeText(mContext,"加载成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(){
//                            Toast.makeText(mContext,"信息暂时无法加载",Toast.LENGTH_SHORT).show();
                        }
                    });
            //将数据保存在itemView的Tag中，以便点击时进行获取
            viewHolder.itemView.setTag(movie);
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return movies.size();
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取数据
                mOnItemClickListener.onItemClick(view,(MovieResult.MovieInfo)view.getTag());
            }
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
        //暴露接口给外面回调
        public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        //define interface
        public static interface OnRecyclerViewItemClickListener {
            void onItemClick(View view , MovieResult.MovieInfo data);
        }
    }


}
