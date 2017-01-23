package com.themartinsierra.popularmovieapp_stage2;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by Martin on 12/18/2016.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, MovieReview[]> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    //private final Context mContext;

    //public FetchReviewsTask(Context context) {mContext = context;}

    public FetchReviewsTask(MovieReviewAsyncResponse delegate){
        this.delegate = delegate;
    }
    private boolean DEBUG = true;

    public MovieReviewAsyncResponse delegate = null;
    /**
     * Take the String representing the complete Review Information in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private MovieReview[] getMovieReviewDataFromJson(String movieJsonStr)
            throws JSONException {
        Log.v(LOG_TAG, "In getMovieReviewDataFromJson");
        List<MovieReview> movieReviews = new ArrayList<>();
        try {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_REVIEW_RESULTS = "results";
            final String TMDB_REVIEW_AUTHOR = "author";
            final String TMDB_REVIEW_CONTENT = "content";


            JSONObject movieReviewsJSON = new JSONObject(movieJsonStr);
            JSONArray movieReviewArray = movieReviewsJSON.getJSONArray(TMDB_REVIEW_RESULTS);

            for (int i = 0; i < movieReviewArray.length(); i++) {
                String tmdbReviewAuthor;
                String tmdbReviewContent;

                JSONObject review = movieReviewArray.getJSONObject(i);

                tmdbReviewAuthor = review.getString(TMDB_REVIEW_AUTHOR);
                tmdbReviewContent = review.getString(TMDB_REVIEW_CONTENT);

                movieReviews.add( new MovieReview(tmdbReviewAuthor,tmdbReviewContent));
            }
            if (movieReviewArray.length() == 0){
                movieReviews.add( new MovieReview(null,null));
            }
            Log.d(LOG_TAG, "getMovieReviewDataFromJson Complete. " + movieReviewArray.length() + " Reviews Inserted");
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return movieReviews.toArray(new MovieReview[movieReviews.size()]);
    }

    @Override
    protected MovieReview[] doInBackground(String... params) {
        Log.v(LOG_TAG, "In doInBackground");
        final String POPULARMOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String APPID_PARAM = "api_key";
        final String REVIEWS = "reviews";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewsJson = null;
        try {

            Uri builtUri = Uri.parse(POPULARMOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(REVIEWS)
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
            reviewsJson = buffer.toString();
        }
        catch (IOException e) {
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
            return getMovieReviewDataFromJson(reviewsJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(MovieReview [] result) {
        if (result != null) {
            delegate.movieReviewProcessFinish(result);
        }
    }
}
