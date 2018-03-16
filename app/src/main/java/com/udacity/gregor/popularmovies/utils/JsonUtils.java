package com.udacity.gregor.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.udacity.gregor.popularmovies.DetailActivity;
import com.udacity.gregor.popularmovies.MainActivity;
import com.udacity.gregor.popularmovies.R;
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
    private static final String JSON_VOTE_COUNT_KEY = "vote_count";
    private static final String JSON_TITLE_KEY = "original_title";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";
    private static final String JSON_POSTER_PATH_KEY = "poster_path";
    private static final String JSON_SYNOPSIS_KEY = "overview";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_NUMBER_OF_REVIEWS_KEY = "total_results";
    private static final String JSON_REVIEW_CONTENT_KEY = "content";
    private static final String JSON_KEY_KEY = "key";

    public static String apiString;
    public static String requestJsonString;

    private static URL requestUrl = null;


    public static String[] getPosterPaths(Context context) {

        String[] posterPaths = new String[MainActivity.NUMBER_OF_SHOWN_RESULTS];


        if (MainActivity.MOST_POPULAR) {
            apiString = MainActivity.BASE_API
                    + context.getResources().getString(R.string.api_part_most_popular)
                    + "?api_key="
                    + MainActivity.API_KEY;
            requestUrl = NetworkUtils.buildUrl(apiString);
        } else if (MainActivity.BEST_RATED) {
            apiString = MainActivity.BASE_API
                    + context.getResources().getString(R.string.api_part_best_rated)
                    + "?api_key="
                    + MainActivity.API_KEY;
            requestUrl = NetworkUtils.buildUrl(apiString);
        }

        try {
            requestJsonString = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject popularOrBestRatedJson = new JSONObject(requestJsonString);
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

        return posterPaths;
    }

    public static Movie getPosterMovie(String json, int jsonPosition) {
        Movie returnMovie;
        String id = null;
        int voteCount = 0;
        String title = null;
        String releaseDate = null;
        String posterPath = null;
        String synopsis = null;

        try {
            JSONObject requestJson = new JSONObject(json);
            String resultsString = requestJson.getString(JSON_RESULTS_KEY);
            JSONArray resultsArray = new JSONArray(resultsString);
            String jsonPart =resultsArray.getString(jsonPosition);
            JSONObject resultsJsonMovie = new JSONObject(jsonPart);

            id = resultsJsonMovie.getString(JSON_ID_KEY);
            title = resultsJsonMovie.getString(JSON_TITLE_KEY);
            voteCount = resultsJsonMovie.getInt(JSON_VOTE_COUNT_KEY);
            releaseDate = resultsJsonMovie.getString(JSON_RELEASE_DATE_KEY);
            posterPath = resultsJsonMovie.getString(JSON_POSTER_PATH_KEY);
            synopsis = resultsJsonMovie.getString(JSON_SYNOPSIS_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        returnMovie = new Movie(title,
                voteCount,
                releaseDate,
                posterPath,
                synopsis,
                id);

        return returnMovie;
    }

    public static String[] getPosterTrailerKeys(Context context, String movieId){
        String[] keys = null;
        apiString = MainActivity.BASE_API
                + movieId
                + context.getResources().getString(R.string.api_part_trailers)
                + "?api_key="
                + MainActivity.API_KEY;
        requestUrl = NetworkUtils.buildUrl(apiString);

        try {
            requestJsonString = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject clickedMovieTrailersJson = new JSONObject(requestJsonString);
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

    public static Uri getPosterTrailerUri(Context context, String trailerKey){
        apiString = MainActivity.YOUTUBE_BASE_URL
                + trailerKey;
        Uri uri = Uri.parse(apiString);
        Log.i("requestUrl Trailer", requestUrl.toString());
        return uri;
    }

    public static void openTrailer(Uri uri, Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);

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
            requestJsonString = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject clickedMovieReviewsJson = new JSONObject(requestJsonString);
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
