package com.udacity.gregor.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.udacity.gregor.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gregor on 20.03.2018.
 */

public class DatabaseUtils {


    public static boolean movieIsFavorite(String idString, SQLiteDatabase database,String tableName, String columnName){
        String favoriteQuery = "SELECT " + columnName + " FROM " + tableName;
        Cursor cursor = database.rawQuery(favoriteQuery, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID)).
                        equals(idString)){
                    cursor.close();
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }
    public static void addMovieToFavorites(Movie movie, SQLiteDatabase database, ContentValues contentValues){

        String posterPathString = movie.getPosterPath();
        String idString = movie.getId();
        String titleString = movie.getMovieTitle();
        String releaseDateString = movie.getReleaseDate();
        String overviewString = movie.getSynopsis();
        double voteAverage = movie.getVoteAverage();
        if(posterPathString == null || idString == null || titleString == null) return;
        contentValues.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPathString);
        contentValues.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, idString);
        contentValues.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE, titleString);
        contentValues.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, releaseDateString);
        contentValues.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS, overviewString);
        contentValues.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

        database.insert(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
    }

    public static void removeMovieFromFavorites(Movie movie, SQLiteDatabase database){
        String idString = movie.getId();
        if(idString == null) return;
        String favoriteQuery = "SELECT " + FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID +
                " FROM " + FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME;
        Cursor cursor = database.rawQuery(favoriteQuery, null);
        if(cursor.moveToFirst()) {
            do{
                database.delete(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                            FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID
                                + "=?", new String[]{idString});

            } while(cursor.moveToNext());
            cursor.close();
        }
    }
}
