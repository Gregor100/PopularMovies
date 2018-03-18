package com.udacity.gregor.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.squareup.picasso.Picasso;
import com.udacity.gregor.popularmovies.utils.JsonUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<String[]>,
        TrailerAdapter.TrailerAdapterOnClickHandler {

    String[] intentData;
    public static final int REVIEW_LOADER_ID = 5656;
    public static final int TRAILER_LOADER_ID = 7878;

    @InjectView(R.id.iv_poster_detail)
    ImageView detailPoster;

    @InjectView(R.id.tv_overview)
    TextView overview;

    @InjectView(R.id.tv_vote_count)
    TextView voteCount;

    @InjectView(R.id.tv_release_date)
    TextView releaseDate;

    @InjectView(R.id.tv_original_title)
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent mainToDetailIntent = getIntent();
        if(mainToDetailIntent.hasExtra(Intent.EXTRA_TEXT)){
            intentData = mainToDetailIntent.getStringArrayExtra(Intent.EXTRA_TEXT);
            String titleString = intentData[MainActivity.titlePosition];
            String overviewString = intentData[MainActivity.synopsisPosition];
            String voteCountString = intentData[MainActivity.voteCountPosition];
            String posterPathString = intentData[MainActivity.posterPathPosition];
            String releaseDateString = intentData[MainActivity.releaseDatePosition];
            title.setText(titleString);
            overview.setText(overviewString);
            voteCount.setText(voteCountString);
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
                    return JsonUtils.getPosterMovieReviews(DetailActivity.this, intentData[MainActivity.idPosition]);
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
                    return JsonUtils.getPosterTrailerKeys(DetailActivity.this, intentData[MainActivity.idPosition]);
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
