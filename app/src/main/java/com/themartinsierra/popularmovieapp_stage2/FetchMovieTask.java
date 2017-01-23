package com.themartinsierra.popularmovieapp_stage2;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.themartinsierra.popularmovieapp_stage2.data.MoviesContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Martin on 12/18/2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieTask(Context context) {mContext = context;}

    private boolean DEBUG = true;

    /**
     * Take the String representing the complete Movie Information in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getPopularMovieDataFromJson(String movieJsonStr, String movieType)

            throws JSONException {
        Log.v(LOG_TAG, "In getPopularMovieDataFromJson");
        try {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_ORIGINAL_TITLE = "title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_MOVIE_ID = "id";

            JSONObject popularMovieJson = new JSONObject(movieJsonStr);
            JSONArray popularMoviesArray = popularMovieJson.getJSONArray(TMDB_RESULTS);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(popularMoviesArray.length());
           /* int deletedReviews = mContext.getContentResolver().delete(MoviesContract.MovieReviews.CONTENT_URI,
                    MoviesContract.MovieReviews.COLUMN_MOVIE_TMDB_ID + " IN (SELECT "
                            + MoviesContract.MovieEntry.COLUMN_TMDB_ID + " FROM "
                            + MoviesContract.MovieEntry.TABLE_NAME + " WHERE favorite = 0)",
                    null);*/

            /*
            int deletedTrailers = mContext.getContentResolver().delete(MoviesContract.MovieTrailers.CONTENT_URI,
                    MoviesContract.MovieTrailers.COLUMN_MOVIE_TMDB_ID + " IN (SELECT "
                            + MoviesContract.MovieEntry.COLUMN_TMDB_ID + " FROM "
                            + MoviesContract.MovieEntry.TABLE_NAME + " WHERE favorite = 0)",
                    null);*/
            for (int i = 0; i < popularMoviesArray.length(); i++) {

                String originalTitle;
                String moviePosterPath;
                String overview;
                String voteAverage;
                String releaseDate;
                String id;
                int favorite = 0;
                // Get the JSON object representing the movie
                JSONObject movie = popularMoviesArray.getJSONObject(i);


                originalTitle = movie.getString(TMDB_ORIGINAL_TITLE);
                moviePosterPath = movie.getString(TMDB_POSTER_PATH);
                overview = movie.getString(TMDB_OVERVIEW);
                voteAverage = movie.getString(TMDB_VOTE_AVERAGE);
                releaseDate = movie.getString(TMDB_RELEASE_DATE);
                id = movie.getString(TMDB_MOVIE_ID);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,originalTitle);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, mContext.getString(R.string.image_path) + mContext.getString(R.string.image_size) + "/" + moviePosterPath );
                movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW,overview);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,voteAverage);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,releaseDate);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_FAVORITE,favorite);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TYPE,movieType);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TMDB_ID, id);
                cVVector.add (movieValues);

            }
            int inserted = 0;
            int deleted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                deleted = mContext.getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                       "favorite = 0",
                        null);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
            Log.d(LOG_TAG, "FetchMovieTask Complete. " + deleted + " deleted");

        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.v(LOG_TAG, "In doInBackground");
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String popularMoviesJsonStr = null;

        try {
            // Construct the URL for the The Movie DB
            final String POPULARMOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(POPULARMOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEAPIKEY)
                    .build();
            URL url = new URL(builtUri.toString());

            // Create the request to The Movie Database, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            popularMoviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the Movie data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getPopularMovieDataFromJson(popularMoviesJsonStr, params[0]);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the movie.
        return null;
    }
}
