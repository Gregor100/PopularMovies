package com.udacity.gregor.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.squareup.picasso.Picasso;
import com.udacity.gregor.popularmovies.data.DatabaseUtils;
import com.udacity.gregor.popularmovies.data.FavoriteMoviesDbHelper;
import com.udacity.gregor.popularmovies.model.Movie;
import com.udacity.gregor.popularmovies.utils.JsonUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.udacity.gregor.popularmovies.data.FavoriteMoviesContract;

public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<String[]>,
        TrailerAdapter.TrailerAdapterOnClickHandler {

    public static SQLiteDatabase mDatabase;
    String[] intentData;
    String posterPathString;
    String idString;
    static ContentValues contentValues = new ContentValues();
    public static final int REVIEW_LOADER_ID = 5656;
    public static final int TRAILER_LOADER_ID = 7878;

    Movie detailMovie;

    Menu menu;

    @InjectView(R.id.iv_poster_detail)
    ImageView detailPoster;

    @InjectView(R.id.tv_overview)
    TextView overview;

    @InjectView(R.id.tv_vote_average)
    TextView voteAverage;

    @InjectView(R.id.tv_release_date)
    TextView releaseDate;

    @InjectView(R.id.tv_title)
    TextView title;

    @InjectView(R.id.recyclerview_reviews)
    RecyclerView reviewsRecyclerView;

    @InjectView(R.id.recyclerview_trailers)
    RecyclerView trailersRecyclerView;

    ReviewAdapter mReviewAdapter;
    String[] reviews = null;
    LoaderCallbacks<String[]> callback = DetailActivity.this;

    TrailerAdapter mTrailerAdapter;
    String[] keys = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.inject(this);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent mainToDetailIntent = getIntent();
        if(mainToDetailIntent.hasExtra(Intent.EXTRA_TEXT)){
            intentData = mainToDetailIntent.getStringArrayExtra(Intent.EXTRA_TEXT);
            idString = intentData[MainActivity.idPosition];
            String titleString = intentData[MainActivity.titlePosition];
            String overviewString = intentData[MainActivity.synopsisPosition];
            String voteAverageString = intentData[MainActivity.voteAveragePosition];
            posterPathString = intentData[MainActivity.posterPathPosition];
            String releaseDateString = intentData[MainActivity.releaseDatePosition];
            title.setText(titleString);
            overview.setText(overviewString);
            voteAverage.setText(voteAverageString);
            releaseDate.setText(releaseDateString);
            Picasso.with(this)
                    .load(MainActivity.BASE_URL + MainActivity.SIZE_POSTER +
                            posterPathString)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(detailPoster);

        }else{
            Log.i("Info", "No intent extra strings");
        }

        RecyclerView.LayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

        RecyclerView.LayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(trailerLayoutManager);

        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(this, null);

        mDatabase = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onResume() {

        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, callback);
        getSupportLoaderManager().restartLoader(TRAILER_LOADER_ID, null, callback);

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, callback);
        getSupportLoaderManager().restartLoader(REVIEW_LOADER_ID, null, callback);

        super.onResume();
    }

    @Override
    protected void onPause() {
        getSupportLoaderManager().destroyLoader(TRAILER_LOADER_ID);
        getSupportLoaderManager().destroyLoader(REVIEW_LOADER_ID);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        if(!DatabaseUtils.movieIsFavorite(idString, mDatabase,
                FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID)) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.empty_star);
        }else if(DatabaseUtils.movieIsFavorite(idString, mDatabase,
                FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID)) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.full_star);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_favorite){
            if(!DatabaseUtils.movieIsFavorite(idString, mDatabase,
                    FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                    FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID)) {
                item.setIcon(R.drawable.full_star);
                DatabaseUtils.addMovieToFavorites(detailMovie, mDatabase, contentValues);
            }else if(DatabaseUtils.movieIsFavorite(idString, mDatabase,
                    FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                    FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID)) {
                item.setIcon(R.drawable.empty_star);
                DatabaseUtils.removeMovieFromFavorites(detailMovie, mDatabase);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public android.support.v4.content.Loader<String[]> onCreateLoader(int loaderId, Bundle bundle) {
        if(loaderId == REVIEW_LOADER_ID) {
            return new AsyncTaskLoader<String[]>(this) {

                @Override
                protected void onStartLoading() {
                    if (reviews != null) {
                        deliverResult(reviews);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public String[] loadInBackground() {
                    detailMovie = JsonUtils.getMovieById(idString);
                    return JsonUtils.getPosterMovieReviews(DetailActivity.this, idString);
                }

                @Override
                public void deliverResult(String[] data) {
                    if (isLoadInBackgroundCanceled()) return;
                    reviews = data;
                    super.deliverResult(data);
                }
            };
        }else if (loaderId == TRAILER_LOADER_ID){
            return new AsyncTaskLoader<String[]>(this) {

                @Override
                protected void onStartLoading() {
                    if(keys != null){
                        deliverResult(keys);
                    }else{
                        forceLoad();
                    }
                }

                @Override
                public String[] loadInBackground() {
                    return JsonUtils.getPosterTrailerKeys(DetailActivity.this, idString);
                }

                @Override
                public void deliverResult(String[] data) {
                    if(isLoadInBackgroundCanceled()) return;
                    keys = data;
                    super.deliverResult(data);
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String[]> loader, String[] data) {
        if(loader.getId() == REVIEW_LOADER_ID) {
            mReviewAdapter = new ReviewAdapter(data);
            reviewsRecyclerView.setAdapter(mReviewAdapter);
        }else if(loader.getId() == TRAILER_LOADER_ID) {
            mTrailerAdapter = new TrailerAdapter(this, data, this);
            trailersRecyclerView.setAdapter(mTrailerAdapter);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String[]> loader) {
        if(loader != null){
            getSupportLoaderManager().destroyLoader(REVIEW_LOADER_ID);
            getSupportLoaderManager().destroyLoader(TRAILER_LOADER_ID);
        }
    }

    @Override
    public void onClick(Uri uri) {
        JsonUtils.openTrailer(this, uri);
    }
}
