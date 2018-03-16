package com.udacity.gregor.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gregor on 16.03.2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{
    private String[] mTrailers = null;

    TrailerAdapter(String trailers[]){
        mTrailers = trailers;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        int layoutId = R.layout.trailer_item_view;
        inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);

        View view = inflater.inflate(R.layout.trailer_item_view, parent, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        String trailerText = "Trailer" + Integer.toString(position+1);
        holder.trailerName.setText(trailerText);
        holder.youtubeSign.setImageResource(R.drawable.youtubesign);
    }

    @Override
    public int getItemCount() {
        return mTrailers.length;
    }


    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder{

        ImageView youtubeSign;
        TextView trailerName;
        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            youtubeSign = itemView.findViewById(R.id.iv_youtube_sign);
            trailerName = itemView.findViewById(R.id.tv_trailer_name);
        }
    }
}
