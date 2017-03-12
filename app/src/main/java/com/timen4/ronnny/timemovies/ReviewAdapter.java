package com.timen4.ronnny.timemovies;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timen4.ronnny.timemovies.bean.ReviewResult;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by ronny on 2017/3/6.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> implements View.OnClickListener {
    private List<ReviewResult.MovieReview> mReviews;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClikListener;
    private boolean isTwoPanel;

    public ReviewAdapter(List<ReviewResult.MovieReview> mReviews, Context mContext) {
        this.mReviews = mReviews;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent,false);

        ViewHolder viewHolder=new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ReviewResult.MovieReview movieReview = mReviews.get(position);
        viewHolder.tv_content.setText(movieReview.getContent());
        viewHolder.tv_author.setText(movieReview.getAuthor());
        viewHolder.itemView.setTag(movieReview);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    @Override
    public void onClick(View v) {

    }

    public void update(List<ReviewResult.MovieReview> reviews) {
        mReviews=reviews;
        notifyDataSetChanged();
    }

    public void setIsTwoPanel(boolean isTwoPanel) {
        this.isTwoPanel=isTwoPanel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_content;
        private TextView tv_author;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_author= (TextView) itemView.findViewById(R.id.tv_author);
            tv_content= (TextView) itemView.findViewById(R.id.tv_comment);
        }
    }
    public void setOnRecyclerViewItemClikListener(OnRecyclerViewItemClickListener onRecyclerViewItemClikListener){
        this.mOnRecyclerViewItemClikListener=onRecyclerViewItemClikListener;
    }

    interface OnRecyclerViewItemClickListener {
        void onItemClik(View view, ReviewResult.MovieReview data);
    }


//    private List<ReviewResult.MovieReview> mReviews;
//    private Context mContext;
//
//    public ReviewAdapter(List<ReviewResult.MovieReview> reviews, Context mContext) {
//        this.mReviews = reviews;
//        this.mContext = mContext;
//    }
//
//    public void update(List<ReviewResult.MovieReview> trailers) {
//        this.mReviews = trailers;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        return mReviews ==null?0: mReviews.size();
//    }
//
//    @Override
//    public ReviewResult.MovieReview getItem(int position) {
//        return mReviews ==null?null: mReviews.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ReviewResult.MovieReview movieReview = getItem(position);
//        ViewHolder viewHolder = null;
//        if (convertView==null){
//            viewHolder=new ViewHolder();
//            convertView = View.inflate(mContext, R.layout.review_item, null);
//            viewHolder.tv_author= (TextView) convertView.findViewById(R.id.tv_author);
//            viewHolder.tv_content= (TextView) convertView.findViewById(R.id.tv_comment);
//            convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        viewHolder.tv_content.setText(movieReview.getContent());
//        viewHolder.tv_author.setText(movieReview.getAuthor()==null?"":"——by "+movieReview.getAuthor());
//        return convertView;
//    }
//    private static class ViewHolder{
//        private TextView tv_content;
//        private TextView tv_author;
//        public ViewHolder() {
//
//        }
//    }
}
