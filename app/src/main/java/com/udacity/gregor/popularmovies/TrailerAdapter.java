package com.udacity.gregor.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.gregor.popularmovies.utils.JsonUtils;

/**
 * Created by Gregor on 16.03.2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{
    private String[] mTrailers = null;
    private TrailerAdapterOnClickHandler mClickHandler = null;
    private Context mContext;

    TrailerAdapter(Context context, String trailers[], TrailerAdapterOnClickHandler handler){
        mTrailers = trailers;
        mContext = context;
        mClickHandler = handler;
    }

    public interface TrailerAdapterOnClickHandler{
        void onClick(Uri uri);
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
        if(mTrailers != null) {
            return mTrailers.length;
        }else{
            return 0;
        }
    }


    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView youtubeSign;
        TextView trailerName;

        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            youtubeSign = itemView.findViewById(R.id.iv_youtube_sign);
            trailerName = itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Uri uri = JsonUtils.getPosterTrailerUri(JsonUtils.keys[adapterPosition]);
            mClickHandler.onClick(uri);
        }
    }
}
