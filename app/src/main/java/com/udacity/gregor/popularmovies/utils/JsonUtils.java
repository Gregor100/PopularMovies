package com.udacity.gregor.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.udacity.gregor.popularmovies.DetailActivity;
import com.udacity.gregor.popularmovies.MainActivity;
import com.udacity.gregor.popularmovies.R;
import com.udacity.gregor.popularmovies.data.FavoriteMoviesContract;
import com.udacity.gregor.popularmovies.data.FavoriteMoviesDbHelper;
import com.udacity.gregor.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Gregor on 25.02.2018.
 */

public class JsonUtils{
    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_VOTE_Average_KEY = "vote_average";
    private static final String JSON_TITLE_KEY = "title";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";
    private static final String JSON_POSTER_PATH_KEY = "poster_path";
    private static final String JSON_SYNOPSIS_KEY = "overview";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_NUMBER_OF_REVIEWS_KEY = "total_results";
    private static final String JSON_REVIEW_CONTENT_KEY = "content";
    private static final String JSON_KEY_KEY = "key";

    private static String apiString;
    private static String requestJsonStringRecent;
    public static String requestJsonStringMostPopular;
    public static String requestJsonStringBestRated;
    public static String[] keys;
    private static JSONArray resultsBestRatedArray = null;
    private static JSONArray resultsMostPopularArray = null;

    private static SQLiteDatabase database;
    private static String[] posterPaths;
    private static String[] ids;

    private static URL requestUrl = null;


    public static String[] getPosterIdsFromFavorites(Context context){

        ids = new String[MainActivity.NUMBER_OF_SHOWN_RESULTS];

        final String favoritesDatabaseQuery = "SELECT " +
                FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID +
                " FROM " +
                FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME;
        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(context, null);
        database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(favoritesDatabaseQuery, null);

        if(cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID);
            if (cursor.moveToFirst()) {
                int i;
                do {
                    i = cursor.getPosition();
                    ids[i] = cursor.getString(columnIndex);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return ids;

    }
    public static String[] getPosterPaths(Context context) {

        posterPaths = new String[MainActivity.NUMBER_OF_SHOWN_RESULTS];


        if (MainActivity.MOST_POPULAR) {
            apiString = MainActivity.BASE_API
                    + context.getResources().getString(R.string.api_part_most_popular)
                    + "?api_key="
                    + MainActivity.API_KEY;
            requestUrl = NetworkUtils.buildUrl(apiString);
            try {
                requestJsonStringMostPopular = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                JSONObject popularOrBestRatedJson = new JSONObject(requestJsonStringMostPopular);
                String resultsJsonString = popularOrBestRatedJson.getString(JSON_RESULTS_KEY);
                JSONArray resultsJsonArray = new JSONArray(resultsJsonString);
                for (int i = 0; i < MainActivity.NUMBER_OF_SHOWN_RESULTS; i++) {
                    String jsonPart = resultsJsonArray.getString(i);
                    JSONObject singleMovieJson = new JSONObject(jsonPart);
                    posterPaths[i] = singleMovieJson.getString(JSON_POSTER_PATH_KEY);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        } else if (MainActivity.BEST_RATED) {
            apiString = MainActivity.BASE_API
                    + context.getResources().getString(R.string.api_part_best_rated)
                    + "?api_key="
                    + MainActivity.API_KEY;
            requestUrl = NetworkUtils.buildUrl(apiString);
            try {
                requestJsonStringBestRated = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                JSONObject popularOrBestRatedJson = new JSONObject(requestJsonStringBestRated);
                String resultsJsonString = popularOrBestRatedJson.getString(JSON_RESULTS_KEY);
                JSONArray resultsJsonArray = new JSONArray(resultsJsonString);
                for (int i = 0; i < MainActivity.NUMBER_OF_SHOWN_RESULTS; i++) {
                    String jsonPart = resultsJsonArray.getString(i);
                    JSONObject singleMovieJson = new JSONObject(jsonPart);
                    posterPaths[i] = singleMovieJson.getString(JSON_POSTER_PATH_KEY);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        } else if(MainActivity.SHOW_FAVORITES){
            final String favoritesDatabaseQuery = "SELECT " +
                    FavoriteMoviesContract.FavoriteMovieEntry.MOVIE_POSTER_PATH +
                    " FROM " +
                    FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME;
            FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(context, null);
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(favoritesDatabaseQuery, null);

            if(cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(FavoriteMoviesContract.FavoriteMovieEntry.MOVIE_POSTER_PATH);
                if (cursor.moveToFirst()) {
                    int i;
                    do {
                        i = cursor.getPosition();
                        posterPaths[i] = cursor.getString(columnIndex);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
            Log.i("posterPaths", Arrays.toString(posterPaths));
            return posterPaths;
        }



        Log.i("posterPaths", Arrays.toString(posterPaths));
        return posterPaths;
    }

    public static Movie getMovieById(String movieId){
        Double voteAverage = 0.0;
        String title = null;
        String releaseDate = null;
        String posterPath = null;
        String synopsis = null;

        apiString = MainActivity.BASE_API
                + movieId
                + "?api_key="
                + MainActivity.API_KEY;
        requestUrl = NetworkUtils.buildUrl(apiString);

        try {
            requestJsonStringRecent = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject clickedMovieJson = new JSONObject(requestJsonStringRecent);
            posterPath = clickedMovieJson.getString(JSON_POSTER_PATH_KEY);
            voteAverage = clickedMovieJson.getDouble(JSON_VOTE_Average_KEY);
            title = clickedMovieJson.getString(JSON_TITLE_KEY);
            releaseDate = clickedMovieJson.getString(JSON_RELEASE_DATE_KEY);
            synopsis = clickedMovieJson.getString(JSON_SYNOPSIS_KEY);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.i("Info", "exception triggered");
        }

        return new Movie(title,voteAverage,releaseDate,posterPath,synopsis, movieId);
    }

    public static Movie getPosterMovie(String json, int adapterPosition) {
        String id = null;
        Double voteAverage = 0.0;
        String title = null;
        String releaseDate = null;
        String posterPath = null;
        String synopsis = null;


        try {

            if(MainActivity.MOST_POPULAR || MainActivity.BEST_RATED) {
                JSONObject requestJson = new JSONObject(json);
                String resultsString = requestJson.getString(JSON_RESULTS_KEY);
                JSONObject resultsJsonMovie;
                if(MainActivity.MOST_POPULAR){
                    resultsMostPopularArray = new JSONArray(resultsString);
                    String jsonPart = resultsMostPopularArray.getString(adapterPosition);
                    resultsJsonMovie = new JSONObject(jsonPart);
                }else{
                    resultsBestRatedArray = new JSONArray(resultsString);
                    String jsonPart = resultsBestRatedArray.getString(adapterPosition);
                    resultsJsonMovie = new JSONObject(jsonPart);
                }

                id = resultsJsonMovie.getString(JSON_ID_KEY);
                title = resultsJsonMovie.getString(JSON_TITLE_KEY);
                voteAverage = resultsJsonMovie.getDouble(JSON_VOTE_Average_KEY);
                releaseDate = resultsJsonMovie.getString(JSON_RELEASE_DATE_KEY);
                posterPath = resultsJsonMovie.getString(JSON_POSTER_PATH_KEY);
                synopsis = resultsJsonMovie.getString(JSON_SYNOPSIS_KEY);
            } else if(MainActivity.SHOW_FAVORITES) {
                id = ids[adapterPosition];
                Movie movie = JsonUtils.getMovieById(id);

                title = movie.getMovieTitle();
                voteAverage = movie.getVoteAverage();
                releaseDate = movie.getReleaseDate();
                posterPath = movie.getPosterPath();
                synopsis = movie.getSynopsis();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return new Movie(title,
                voteAverage,
                releaseDate,
                posterPath,
                synopsis,
                id);


    }

    public static String[] getPosterTrailerKeys(Context context, String movieId){
        keys = null;
        apiString = MainActivity.BASE_API
                + movieId
                + context.getResources().getString(R.string.api_part_trailers)
                + "?api_key="
                + MainActivity.API_KEY;
        requestUrl = NetworkUtils.buildUrl(apiString);

        try {
            requestJsonStringRecent = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject clickedMovieTrailersJson = new JSONObject(requestJsonStringRecent);
            String clickedMovieTrailersResults = clickedMovieTrailersJson.getString(JSON_RESULTS_KEY);
            JSONArray clickedMovieTrailersData = new JSONArray(clickedMovieTrailersResults);
            int numberOfTrailersJson = clickedMovieTrailersData.length();
            keys = new String[numberOfTrailersJson];
            for (int i = 0; i < numberOfTrailersJson; i++){
                JSONObject ithTrailerJson = clickedMovieTrailersData.getJSONObject(i);
                String ithTrailerKey = ithTrailerJson.getString(JSON_KEY_KEY);
                keys[i] = ithTrailerKey;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.i("Info", "exception triggered");
        }
        return keys;
    }

    public static Uri getPosterTrailerUri(String trailerKey){
        apiString = MainActivity.YOUTUBE_BASE_URL
                + trailerKey;
        Uri uri = Uri.parse(apiString);
        Log.i("requestUrl Trailer", requestUrl.toString());
        return uri;
    }

    public static void openTrailer(Context context,Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(intent.resolveActivity(context.getPackageManager())!= null){
            context.startActivity(intent);
        }
    }



    public static String[] getPosterMovieReviews(Context context, String movieId){
        String[] reviews = null;
        Log.i("movieId", movieId);
        apiString = MainActivity.BASE_API
                + movieId
                + context.getResources().getString(R.string.api_part_reviews)
                + "?api_key="
                + MainActivity.API_KEY;
        requestUrl = NetworkUtils.buildUrl(apiString);
        Log.i("reviews url", requestUrl.toString());

        try {
            requestJsonStringRecent = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject clickedMovieReviewsJson = new JSONObject(requestJsonStringRecent);
            int numberOfReviewsJson = clickedMovieReviewsJson.getInt(JSON_NUMBER_OF_REVIEWS_KEY);
            String clickedMovieReviewsResults = clickedMovieReviewsJson.getString(JSON_RESULTS_KEY);
            JSONArray clickedMovieReviewsData = new JSONArray(clickedMovieReviewsResults);
            reviews = new String[numberOfReviewsJson];
            for (int i = 0; i < numberOfReviewsJson; i++){
                String ithReview = clickedMovieReviewsData.getString(i);
                JSONObject ithReviewsJson = new JSONObject(ithReview);
                String reviewContent = ithReviewsJson.getString(JSON_REVIEW_CONTENT_KEY);
                reviews[i] = reviewContent;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.i("Info", "exception triggered");
        }
        return reviews;
    }
}
