package com.udacity.gregor.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.gregor.popularmovies.data.DatabaseUtils;
import com.udacity.gregor.popularmovies.model.Movie;
import com.udacity.gregor.popularmovies.utils.JsonUtils;

import org.json.JSONObject;

/**
 * Created by Gregor on 20.02.2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private String[] mImagePosterPaths;
    private MovieAdapterOnClickHandler mClickHandler;


    public interface MovieAdapterOnClickHandler{
        void onClick(Movie posterMovie);
    }


    MovieAdapter(String[] posterPaths, MovieAdapterOnClickHandler handler){
        mImagePosterPaths = posterPaths;
        mClickHandler = handler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mMoviePoster;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie posterMovie;
            if(MainActivity.MOST_POPULAR) {
                posterMovie = JsonUtils.getPosterMovie(JsonUtils.requestJsonStringMostPopular, adapterPosition);
            } else if(MainActivity.BEST_RATED){
                posterMovie = JsonUtils.getPosterMovie(JsonUtils.requestJsonStringBestRated, adapterPosition);
            } else{
                posterMovie = MainActivity.favorites[adapterPosition];
           /*     String[] idsFromFavorites;
                idsFromFavorites = JsonUtils.getPosterIdsFromFavorites(mMoviePoster.getContext());
                String id = idsFromFavorites[adapterPosition];
                posterMovie = JsonUtils.getMovieById(id);
                Log.i("movieTitle", posterMovie.getMovieTitle());
                */
            }
            mClickHandler.onClick(posterMovie);
        }
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.grid_item_view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;


        View view = layoutInflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, int position) {
        String posterPath = mImagePosterPaths[position];
        Context context = holder.mMoviePoster.getContext();
        Picasso.with(context)
                .load(MainActivity.BASE_URL + MainActivity.SIZE_POSTER + posterPath)
       //         .error(R.drawable.ic_launcher_background)
                .into(holder.mMoviePoster);

    }

    @Override
    public int getItemCount() {
        return mImagePosterPaths.length;
    }



}
