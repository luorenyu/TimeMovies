package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.timen4.ronnny.timemovies.bean.TrailerResult;

import java.util.List;

/**
 * Created by ronny on 2017/3/6.
 */

public class TrailerAdapter extends BaseAdapter {
    private List<TrailerResult.MovieTrailer> mTrailers;
    private Context mContext;

    public TrailerAdapter(List<TrailerResult.MovieTrailer> trailers, Context context) {
        this.mTrailers=trailers;
        this.mContext=context;
    }

    @Override
    public int getCount() {
        return mTrailers.size();
    }

    @Override
    public TrailerResult.MovieTrailer getItem(int position) {
        return mTrailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        TrailerResult.MovieTrailer movieTrailer = mTrailers.get(position);
        if (convertView==null){
            convertView = View.inflate(mContext, R.layout.trailer_item, null);
            viewHolder=new ViewHolder();
            viewHolder.trailer = (TextView) convertView.findViewById(R.id.movie_trailer);
            viewHolder.trailer_play= (ImageView) convertView.findViewById(R.id.movie_play);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (movieTrailer.getId()==null){
            viewHolder.trailer_play.setBackgroundResource(R.drawable.play_pressed);
            viewHolder.trailer.setText(movieTrailer.getName());
        }else{
            viewHolder.trailer.setText(mContext.getString(R.string.trailer) + (position+1)+": "+movieTrailer.getName());
        }

        return convertView;
    }
    static class ViewHolder{
        private TextView trailer;
        private ImageView trailer_play;
        public ViewHolder() {

        }

    }
}
