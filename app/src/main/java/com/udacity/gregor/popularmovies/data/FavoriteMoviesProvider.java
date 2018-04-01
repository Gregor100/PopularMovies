package com.udacity.gregor.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.udacity.gregor.popularmovies.data.FavoriteMoviesContract.PATH_FAVORITES;
import static com.udacity.gregor.popularmovies.data.FavoriteMoviesContract.CONTENT_AUTHORITY;

public class FavoriteMoviesProvider extends ContentProvider {

    public static final String TAG = FavoriteMoviesProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int CODE_FAVORITE = 100;
    public static final int CODE_FAVORITE_WITH_POSITION = 101;

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITES, CODE_FAVORITE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITES + "/#", CODE_FAVORITE_WITH_POSITION);

        return uriMatcher;
    }
    FavoriteMoviesDbHelper mMoviesHelper;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMoviesHelper = new FavoriteMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor returnCursor;

        final SQLiteDatabase sqLiteDatabase = mMoviesHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        switch(match){
            case CODE_FAVORITE:

                returnCursor = sqLiteDatabase.query(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;

            case CODE_FAVORITE_WITH_POSITION:
                String moviePosition = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{moviePosition};

                returnCursor = sqLiteDatabase.query(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                            projection,
                            FavoriteMoviesContract.FavoriteMovieEntry._ID,
                            selectionArguments,
                            null,
                            null,
                            null);
                break;
            default:
                throw new UnsupportedOperationException("Query failed with unknown Uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase sqLiteDatabase = mMoviesHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsInserted = 0;

        switch (match){
            case CODE_FAVORITE:
                sqLiteDatabase.beginTransaction();
                try {
                    for (ContentValues value : values) {

                        Long id = sqLiteDatabase.insert(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME, null, value);
                        if (id > 0) {
                            rowsInserted++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                }finally {
                    sqLiteDatabase.endTransaction();
                }
                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri,values);
        }

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = mMoviesHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        long newId;
        switch (match){
            case CODE_FAVORITE:
                newId = sqLiteDatabase.insert(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return Uri.parse(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI.toString() + "/" + newId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mMoviesHelper.getWritableDatabase();

        int rowsDeleted;

        int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_FAVORITE:
                rowsDeleted = sqLiteDatabase.delete(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Uri unknown: " + uri);
        }
        if(rowsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public void shutdown() {
        mMoviesHelper.close();
        super.shutdown();
    }
}
