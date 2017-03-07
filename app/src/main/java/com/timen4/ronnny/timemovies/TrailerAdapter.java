package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.bean.TrailerResult;

import java.util.List;

/**
 * Created by ronny on 2017/3/6.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> implements View.OnClickListener{

    public List<TrailerResult.MovieTrailer> mMovieTrailers = null;
    private Context mContext;
    private TrailerAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public TrailerAdapter(List<TrailerResult.MovieTrailer> movieTrailers, Context mContext) {
        this.mMovieTrailers = movieTrailers;
        this.mContext = mContext;
    }

    public void update(List<TrailerResult.MovieTrailer> movieTrailers){
        this.mMovieTrailers=movieTrailers;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TrailerResult.MovieTrailer movieTrailer = mMovieTrailers.get(position);
        if (movieTrailer.getId()==null){
            viewHolder.mIV_trailer_play.setBackgroundResource(R.drawable.play_pressed);
            viewHolder.mTv_trailer.setText(movieTrailer.getName());
        }else{
            viewHolder.mTv_trailer.setText(mContext.getString(R.string.trailer) + (position+1)+": "+movieTrailer.getName());
        }
        viewHolder.itemView.setTag(movieTrailer);
    }


    @Override
    public int getItemCount() {
        return mMovieTrailers.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(view, (TrailerResult.MovieTrailer) view.getTag());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTv_trailer;
        private ImageView mIV_trailer_play;

        public ViewHolder(View view) {
            super(view);
            mTv_trailer = (TextView) view.findViewById(R.id.movie_trailer);
            mIV_trailer_play = (ImageView) view.findViewById(R.id.movie_play);
        }
    }

    //暴露接口给外面回调
    public void setOnItemClickListener(TrailerAdapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //define interface
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , TrailerResult.MovieTrailer data);
    }

//    private List<TrailerResult.MovieTrailer> mTrailers;
//    private Context mContext;
//
//    public TrailerAdapter(List<TrailerResult.MovieTrailer> trailers, Context context) {
//        this.mTrailers=trailers;
//        this.mContext=context;
//    }
//
//    public void update(List<TrailerResult.MovieTrailer> trailers) {
//        this.mTrailers = trailers;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        return mTrailers.size();
//    }
//
//    @Override
//    public TrailerResult.MovieTrailer getItem(int position) {
//        return mTrailers.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        TrailerResult.MovieTrailer movieTrailer = mTrailers.get(position);
//        if (convertView==null){
//            convertView = View.inflate(mContext, R.layout.trailer_item, null);
//            viewHolder=new ViewHolder();
//            viewHolder.trailer = (TextView) convertView.findViewById(R.id.movie_trailer);
//            viewHolder.trailer_play= (ImageView) convertView.findViewById(R.id.movie_play);
//            convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        if (movieTrailer.getId()==null){
//            viewHolder.trailer_play.setBackgroundResource(R.drawable.play_pressed);
//            viewHolder.trailer.setText(movieTrailer.getName());
//        }else{
//            viewHolder.trailer.setText(mContext.getString(R.string.trailer) + (position+1)+": "+movieTrailer.getName());
//        }
//
//        return convertView;
//    }
//    static class ViewHolder{
//        private TextView trailer;
//        private ImageView trailer_play;
//        public ViewHolder() {
//
//        }
//
//    }



}
