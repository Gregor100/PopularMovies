package com.udacity.gregor.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gregor on 20.03.2018.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movieFavorites.db";
    private static final int DATABASE_VERSION = 2;

    public FavoriteMoviesDbHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_POSTER_PATH_TABLE =
                "Create Table " +
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                        FavoriteMoviesContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                        FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        FavoriteMoviesContract.FavoriteMovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL " +
                        ");";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_POSTER_PATH_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String SQL_DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DROP_TABLE_QUERY);
        onCreate(sqLiteDatabase);
    }
}
