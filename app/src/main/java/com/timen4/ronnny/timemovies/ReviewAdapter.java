package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timen4.ronnny.timemovies.bean.ReviewResult;

import java.util.List;

/**
 * Created by ronny on 2017/3/6.
 */
public class ReviewAdapter extends BaseAdapter{
    private List<ReviewResult.MovieReview> reviews;
    private Context mContext;

    public ReviewAdapter(List<ReviewResult.MovieReview> reviews, Context mContext) {
        this.reviews = reviews;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return reviews==null?0:reviews.size();
    }

    @Override
    public ReviewResult.MovieReview getItem(int position) {
        return reviews==null?null:reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewResult.MovieReview movieReview = getItem(position);
        ViewHolder viewHolder = null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView = View.inflate(mContext, R.layout.review_item, null);
            viewHolder.tv_author= (TextView) convertView.findViewById(R.id.tv_author);
            viewHolder.tv_content= (TextView) convertView.findViewById(R.id.tv_comment);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_content.setText(movieReview.getContent());
        viewHolder.tv_author.setText(movieReview.getAuthor()==null?"":"——by "+movieReview.getAuthor());
        return convertView;
    }
    private static class ViewHolder{
        private TextView tv_content;
        private TextView tv_author;
        public ViewHolder() {
            
        }
    }
}
