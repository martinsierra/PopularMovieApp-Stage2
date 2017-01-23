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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Martin on 12/18/2016.
 */
public class FetchTrailersTask extends AsyncTask<String, Void, MovieTrailer[]> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

    public FetchTrailersTask(MovieTrailerAsyncResponse delegate) {
        this.delegate = delegate;
    }
    private boolean DEBUG = true;

    public MovieTrailerAsyncResponse delegate = null;
    /**
     * Take the String representing the complete Trailer Information in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private MovieTrailer[] getMovieTrailersDataFromJson(String movieJsonStr)
            throws JSONException {
        Log.v(LOG_TAG, "In getMovieTrailersDataFromJson");
        List<MovieTrailer> movieTrailers = new ArrayList<>();
        try {
            final String TMDB_TRAILER_RESULTS = "results";
            final String TMDB_TRAILER_KEY = "key";
            final String TMDB_TRAILER_NAME = "name";
            final String TMDB_TRAILER_SITE = "site";

            JSONObject movieTrailersJSON = new JSONObject(movieJsonStr);
            JSONArray movieTrailerArray = movieTrailersJSON.getJSONArray(TMDB_TRAILER_RESULTS);

            for (int i = 0; i < movieTrailerArray.length(); i++) {

                String tmdbTrailerKey;
                String tmdbTrailerName;
                String tmdbTrailerSite;

                JSONObject trailer = movieTrailerArray.getJSONObject(i);

                tmdbTrailerKey = trailer.getString(TMDB_TRAILER_KEY);
                tmdbTrailerName = trailer.getString(TMDB_TRAILER_NAME);
                tmdbTrailerSite = trailer.getString(TMDB_TRAILER_SITE);

                movieTrailers.add(new MovieTrailer(tmdbTrailerKey, tmdbTrailerName, tmdbTrailerSite));
            }
            if (movieTrailerArray.length() == 0){
                movieTrailers.add(new MovieTrailer(null, null, null));
            }
            Log.d(LOG_TAG, "getMovieTrailerDataFromJson Complete. " + movieTrailerArray.length() + " Trailers Inserted");
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return movieTrailers.toArray(new MovieTrailer[movieTrailers.size()]);
    }

    @Override
    protected MovieTrailer[] doInBackground(String... params) {
        Log.v(LOG_TAG, "In doInBackground");

        final String POPULARMOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String APPID_PARAM = "api_key";
        final String TRAILERS = "videos";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailersJson = null;
        try{
            Uri builtUri = Uri.parse(POPULARMOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(TRAILERS)
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
            trailersJson = buffer.toString();
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Error ", e);
        }
        finally {
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
            return getMovieTrailersDataFromJson(trailersJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(MovieTrailer[] result){
        if (result != null) {
            delegate.movieTrailerProcessFinish(result);
        }
    }
}
