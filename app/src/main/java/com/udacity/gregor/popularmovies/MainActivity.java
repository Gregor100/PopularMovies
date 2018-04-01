package com.udacity.gregor.popularmovies;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.gregor.popularmovies.data.FavoriteMoviesContract;
import com.udacity.gregor.popularmovies.utils.JsonUtils;
import com.udacity.gregor.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<String[]>,
        MovieAdapter.MovieAdapterOnClickHandler{

    public static final int SPAN_COUNT_GRID = 2;
    public static final int POSTER_LOADER_ID = 2323;
    public static final String POSTER_LOADER_KEY = "poster_loader";
    public static final String BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String SIZE_POSTER = "w185/";
    public static boolean MOST_POPULAR = true;
    public static boolean BEST_RATED = false;
    public static boolean SHOW_FAVORITES = false;
    public static final int NUMBER_OF_SHOWN_RESULTS = 20;

    public static final int titlePosition = 0;
    public static final int voteAveragePosition = 1;
    public static final int releaseDatePosition = 2;
    public static final int posterPathPosition = 3;
    public static final int synopsisPosition = 4;
    public static final int idPosition = 5;

    public static final int NUMBER_OF_INTENT_DETAILS = 6;

    public static final String BASE_API = "http://api.themoviedb.org/3/movie/";
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";



    MovieAdapter mMovieAdapter;
    RecyclerView moviesRecyclerView;
    String[] posterPaths = null;
    public static String[] ids = null;
    public static com.udacity.gregor.popularmovies.model.Movie[] favorites;
    LoaderCallbacks<String[]> callback = MainActivity.this;

    String[] FAVORITES_PROJECTION = {
            FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,
            FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE,
            FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
            FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS,
            FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH,
            FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE
    };

    public static final int INDEX_COLUMN_MOVIE_ID = 0;
    public static final int INDEX_COLUMN_MOVIE_TITLE = 1;
    public static final int INDEX_COLUMN_RELEASE_DATE = 2;
    public static final int INDEX_COLUMN_SYNOPSIS = 3;
    public static final int INDEX_COLUMN_MOVIE_POSTER_PATH = 4;
    public static final int INDEX_COLUMN_VOTE_AVERAGE = 5;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        moviesRecyclerView = findViewById(R.id.recyclerview_movies);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this,
                SPAN_COUNT_GRID);
        moviesRecyclerView.setLayoutManager(gridLayoutManager);
        moviesRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().destroyLoader(POSTER_LOADER_ID);
        getSupportLoaderManager().initLoader(POSTER_LOADER_ID, null, callback);
        getSupportLoaderManager().restartLoader(POSTER_LOADER_ID, null, callback);
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public android.support.v4.content.Loader<String[]> onCreateLoader(int id, Bundle args) {

        return new android.support.v4.content.AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {
                if(posterPaths != null) {
                    deliverResult(posterPaths);
                } else{
                    forceLoad();
                }
            }

 /*           @Override
            protected void onStopLoading() {
                cancelLoad();
            }
*/
            @Override
            public String[] loadInBackground() {
                return JsonUtils.getPosterPaths(MainActivity.this);
            }


            @Override
            public void deliverResult(String[] data) {
                if(isLoadInBackgroundCanceled()) return;
                posterPaths = data;
                super.deliverResult(data);
            }



        };
    }


    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String[]> loader, String[] data) {
        mMovieAdapter = new MovieAdapter(data,this);
        moviesRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String[]> loader) {
        if(loader != null){
            getSupportLoaderManager().destroyLoader(POSTER_LOADER_ID);
        }
    }

    @Override
    public void onClick(com.udacity.gregor.popularmovies.model.Movie posterMovie) {
        Class destinationClass = DetailActivity.class;
        Intent movieDetailIntent = new Intent(MainActivity.this, destinationClass);
        String[] intentData = new String[NUMBER_OF_INTENT_DETAILS];
        intentData[titlePosition] = posterMovie.getMovieTitle();
        Log.i("posterMovieTitle",posterMovie.getMovieTitle());
        intentData[voteAveragePosition] = Double.toString(posterMovie.getVoteAverage());
        intentData[releaseDatePosition] = posterMovie.getReleaseDate();
        intentData[posterPathPosition] = posterMovie.getPosterPath();
        intentData[synopsisPosition] = posterMovie.getSynopsis();
        Log.i("posterMovieId", posterMovie.getId());
        intentData[idPosition] = posterMovie.getId();
        movieDetailIntent.putExtra(Intent.EXTRA_TEXT,intentData);
        startActivity(movieDetailIntent);
    }
}
