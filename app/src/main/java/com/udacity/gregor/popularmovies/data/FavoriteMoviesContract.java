package com.udacity.gregor.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by Gregor on 20.03.2018.
 */

public class FavoriteMoviesContract {

    public static final class FavoriteMovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movieFavorites";
        public static final String COLUMN_MOVIE_ID = "movieIds";
        public static final String COLUMN_MOVIE_TITLE = "movieTitles";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPaths";
        public static final String COLUMN_RELEASE_DATE = "movieReleaseDates";
        public static final String COLUMN_SYNOPSIS = "movieOverviews";
        public static final String COLUMN_VOTE_AVERAGE = "movieVoteAverages";

    }
}
