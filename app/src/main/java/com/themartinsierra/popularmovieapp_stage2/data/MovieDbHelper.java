package com.themartinsierra.popularmovieapp_stage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.themartinsierra.popularmovieapp_stage2.data.MoviesContract.MovieEntry;

/**
 * Manages a local database for popular movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "popularmovie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
         Creates the databases that will be used for the PopularMovie App.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create a table to hold Favorited Popular Movies.  A Favorited Popular movie consists of
        // Title, Movie Poster Path, Overview, Vote Average, Release Date, and trailers.

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_TMDB_ID + " INTEGER NOT NULL , " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TYPE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL, " +

                //"))";
                " UNIQUE (" +
                MovieEntry.COLUMN_ORIGINAL_TITLE + ", " +
                MovieEntry.COLUMN_OVERVIEW + ", " +
                MovieEntry.COLUMN_RELEASE_DATE + ", " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + ", " +
                MovieEntry.COLUMN_VOTE_AVERAGE + ", " +
                MovieEntry.COLUMN_MOVIE_TYPE + ", " +
                MovieEntry.COLUMN_TMDB_ID +
            " ) ON CONFLICT IGNORE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    /*
        Drops the tables when there is an upgrade in database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}