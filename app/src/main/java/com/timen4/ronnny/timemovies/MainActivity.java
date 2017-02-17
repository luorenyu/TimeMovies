package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
        private boolean isPopular=true;//判断是获取热门信息还是高分信息

        private static final String BASE_URL = "http://api.themoviedb.org";


        private static final String API_KEY = "c10d7f437c43bf19190ac874c33541bc";

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
                    query();
//                    updateMoies();
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
            mAdapter = new MyAdapter(getMoiesFromNet(),getContext());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new MyAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, MovieResults.ResultsBean data) {
                    Intent intent= new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra("movieObject",data);
                    startActivity(intent);
                }
            });
            return rootview;

        }

        private ArrayList<MovieResults.ResultsBean> getMoiesFromNet() {
            query();
            ArrayList<MovieResults.ResultsBean> moies= new ArrayList<>();
            for (int i=0;i<4;i++){
                moies.add(new MovieResults.ResultsBean("正在加载中。。。" + i,0.0));
            }
            return moies;
        }

//deal request internet data logic
//key:c10d7f437c43bf19190ac874c33541bc
//            http://api.themoviedb.org/3/movie/popular?language=zh&api_key=[YOUR_API_KEY]
//            http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=[YOUR_API_KEY]
        private void query(){
            //1.创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())//解析方法
                    .baseUrl(BASE_URL)//主机地址
                    .build();

            //2.创建访问API的请求
            PhoneService service = retrofit.create(PhoneService.class);
            String mLanguage="zh";
            Call<MovieResults> call = service.getResult(mLanguage,API_KEY);

            //3.发送请求
            call.enqueue(new Callback<MovieResults>() {
                @Override
                public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                    //4.处理结果
                    if (response.isSuccessful()){
                        ArrayList result= (ArrayList) response.body().getResults();
                        mAdapter.setDatas(result);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<MovieResults> call, Throwable t) {
                    Log.e("失败",t.toString());
                }
            });

        }
        public interface PhoneService {
            @GET("/3/movie/popular")
            Call<MovieResults> getResult(@Query("language") String language, @Query("api_key") String api_key);
        }
    }



    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
        public ArrayList<MovieResults.ResultsBean> movies = null;
        private Context mContext;
        private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
        private OnRecyclerViewItemClickListener mOnItemClickListener = null;

        public MyAdapter(ArrayList<MovieResults.ResultsBean> movies, Context context) {
            this.mContext=context;
            this.movies = movies;
        }

        public void setDatas(ArrayList<MovieResults.ResultsBean> movies){
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
            final MovieResults.ResultsBean movie = movies.get(position);
            viewHolder.mTv_score.setText("评分："+ movie.getVote_average()+"");
            viewHolder.mTv_title.setText("影片："+ movie.getTitle()+"/10");
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
                            Toast.makeText(mContext,"信息暂时无法加载",Toast.LENGTH_SHORT).show();
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
                mOnItemClickListener.onItemClick(view,(MovieResults.ResultsBean)view.getTag());
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
            void onItemClick(View view , MovieResults.ResultsBean data);
        }
    }


}
