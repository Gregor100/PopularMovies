package com.udacity.gregor.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.udacity.gregor.popularmovies.data.FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI;

/**
 * Created by Gregor on 20.03.2018.
 */

public class FavoriteMoviesContract {

    public static final String PATH_FAVORITES = "movieFavorites";

    public static final class FavoriteMovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "movieFavorites";
        public static final String COLUMN_MOVIE_ID = "movieIds";
        public static final String COLUMN_MOVIE_TITLE = "movieTitles";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPaths";
        public static final String COLUMN_RELEASE_DATE = "movieReleaseDates";
        public static final String COLUMN_SYNOPSIS = "movieOverviews";
        public static final String COLUMN_VOTE_AVERAGE = "movieVoteAverages";
    }

    public static String CONTENT_AUTHORITY = "com.udacity.gregor.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static Uri FAVORITES_URI = Uri.parse(BASE_CONTENT_URI + "/" + FavoriteMovieEntry.TABLE_NAME);

    public static String FAVORITE_MOVIES_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/" + CONTENT_AUTHORITY + "/" + FavoriteMovieEntry.TABLE_NAME;

    public static String FAVORITE_MOVIES_ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/" + CONTENT_AUTHORITY + "/" + FavoriteMovieEntry.TABLE_NAME;

    public static Uri buildFavoriteUriWithPosition(int position) {
        return CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(position))
                .build();
    }


}
