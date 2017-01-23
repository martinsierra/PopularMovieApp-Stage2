package com.themartinsierra.popularmovieapp_stage2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * * Defines table and column names for the popular movie database.
 */
public class MoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    // THIS HAS TO MATCH THE PACKAGE NAME!

    public static final String CONTENT_AUTHORITY = "com.themartinsierra.popularmovieapp_stage2";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/movies/ is a valid path for
    // looking at weather com.themartinsierra.popularmovie.app.data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.

    public   static final String PATH_MOVIES = "movies";
    public static final String PATH_MOVIE_REVIEWS = "moviereviews";
    public static final String PATH_MOVIE_TRAILERS = "movietrailers";
    /*
        Specifies MovieEntry table's name.  The table's columns will consist of: The TMDB ID,
        Original Title, Movie Poster Path, Overview, Vote Average, movie release date,
        if the movie is a favorite movie, and the type of list that holds the movie's information.
     */
    public static final class MovieEntry implements BaseColumns {

        // table name
        public static final String TABLE_NAME = "movies";

        // columns
        public static final String _ID = "_id";

        public static final String COLUMN_TMDB_ID = "tmdb_id";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        // Movie poster path stored as string
        public static final String COLUMN_MOVIE_POSTER_PATH= "movie_poster_path";
        // Overview of movie stored as a string
        public static final String COLUMN_OVERVIEW = "overview";
        // Vote average stored as string.
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        // Release date stored as string
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_FAVORITE = "favorite";

        public static final String COLUMN_MOVIE_TYPE = "movie_type";



        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithTMDBID(int tmdbid) {
            String test =  Integer.toString(tmdbid);
            Uri testUri = CONTENT_URI.buildUpon().appendPath(test).build();
            return testUri;
        }
    }
}
