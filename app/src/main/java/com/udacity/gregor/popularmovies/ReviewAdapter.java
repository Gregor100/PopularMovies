package com.udacity.gregor.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Gregor on 15.03.2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private String[] mReviews;

    ReviewAdapter(String[] reviews) {
        mReviews = reviews;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int layoutId = R.layout.review_item_view;
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        String review = mReviews[position];
        holder.mReview.setText(review);
    }

    @Override
    public int getItemCount() {
        Log.i("mReviews count", Integer.toString(mReviews.length));
        return mReviews.length;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView mReview;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mReview = itemView.findViewById(R.id.tv_review);
        }
    }

}
