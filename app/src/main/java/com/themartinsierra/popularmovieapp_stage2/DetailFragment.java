package com.themartinsierra.popularmovieapp_stage2;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;


import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;

import static com.themartinsierra.popularmovieapp_stage2.data.MoviesContract.*;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MovieReviewAsyncResponse,MovieTrailerAsyncResponse {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    private ReviewListViewAdapter rlva;
    private TrailerListViewAdapter tlva;
    private static final String [] DETAIL_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_FAVORITE,
            MovieEntry.COLUMN_TMDB_ID
            //MovieReviews.COLUMN_REVIEW__AUTHOR,
            //MovieReviews.COLUMN_REVIEW_CONTENT,
            //MovieTrailers.COLUMN_TRAILER_KEY,
            //MovieTrailers.COLUMN_TRAILER_NAME
    };




    private static final int COL_MOVIE_ID = 0;
    private static final int COL_ORIGINAL_TITLE = 1;
    private static final int COL_OVERVIEW= 2;
    private static final int COL_RELEASE_DATE = 3;
    private static final int COL_MOVIE_POSTER_PATH = 4;
    private static final int COL_VOTE_AVERAGE  = 5;
    private static final int COL_FAVORITE = 6;
    private static final int COL_TMDB_ID = 7;
    //private static final int COL_REVIEW_AUTHOR = 7;
    //private static final int COL_REVIEW_CONTENT = 8;
    //private static final int COL_TRAILER_KEY = 9;
    //private static final int COL_TRAILER_NAME = 10;

    private TextView mTitle;
    private ImageView mPosterView;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private Button mButton;
    private TextView mOverView;
    private ListView mTrailers;
    private ListView mReviews;
    private TextView mReviewTextView;
    private TextView mTrailerTextView;

    public DetailFragment() {
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "In onCreateView");
        Bundle arguments = getArguments();

        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitle = (TextView) rootView.findViewById(R.id.originaltitle_text);
        mPosterView = (ImageView) rootView.findViewById(R.id.movieposter_image);
        mReleaseDate = (TextView) rootView.findViewById(R.id.releasedate_text);
        mVoteAverage = (TextView) rootView.findViewById(R.id.voteaverage_text);
        mButton = (Button) rootView.findViewById(R.id.favorite_button);
        mOverView = (TextView) rootView.findViewById(R.id.overview_text);
        mTrailers = (ListView) rootView.findViewById(R.id.trailers_list);
        mReviews = (ListView) rootView.findViewById(R.id.reviews_list);
        mReviewTextView = (TextView) rootView.findViewById(R.id.reviews_text);
        mTrailerTextView = (TextView) rootView.findViewById(R.id.trailers_text);

        mButton.setVisibility(View.GONE);
        mReviewTextView.setVisibility(View.GONE);
        mTrailerTextView.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "In onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "In onActivityCreated");
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }else
        {
            Intent intent = getActivity().getIntent();
            if (intent == null  ||intent.getData() == null) {
                return null;
            }

            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }




        final int rowId = data.getInt(COL_MOVIE_ID);
        String moviePosterPath = data.getString(COL_MOVIE_POSTER_PATH);
        String voteAverage = data.getString(COL_VOTE_AVERAGE);
        String releaseDate = data.getString(COL_RELEASE_DATE);
        String originalTitle = data.getString(COL_ORIGINAL_TITLE);
        String overView = data.getString(COL_OVERVIEW);
        String tmdbId = data.getString(COL_TMDB_ID);
        int favorite = data.getInt(COL_FAVORITE);

        mButton.setVisibility(View.VISIBLE);
        mReviewTextView.setVisibility(View.VISIBLE);
        mTrailerTextView.setVisibility(View.VISIBLE);

        FetchReviewsTask frt = new FetchReviewsTask(this);
        frt.execute(tmdbId);

        FetchTrailersTask ftt = new FetchTrailersTask(this);
        ftt.execute(tmdbId);



        mTitle.setText(originalTitle);
        Uri posterUri = Uri.parse(moviePosterPath);
        Picasso.with(getContext()).load(posterUri).into(mPosterView);
        mOverView.setText(overView);
        mVoteAverage.setText(voteAverage);
        mReleaseDate.setText(releaseDate);

        if (favorite == 0){
            mButton.setText(R.string.button_add_favorite);
        }
        else {
            mButton.setText(R.string.button_remove_favorite);
        }

        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ContentValues updatedValues = new ContentValues();

                Button b = (Button) v;
                if (b.getText().equals(getResources().getString(R.string.button_remove_favorite))){
                    b.setText(R.string.button_add_favorite);
                    updatedValues.put(MovieEntry.COLUMN_FAVORITE, 0 );
                }
                else {
                    updatedValues.put(MovieEntry.COLUMN_FAVORITE, 1 );
                }
                int count = getContext().getContentResolver().update(
                        MovieEntry.CONTENT_URI,
                        updatedValues,
                        MovieEntry._ID + "= ?",
                        new String[] { Integer.toString(rowId)});
                Log.v(LOG_TAG, "Total changes of value? " + count);
            }
        });

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "In onLoaderReset");

    }

    @Override
    public void movieReviewProcessFinish(MovieReview[] mr) {
        Log.v(LOG_TAG, "In movieReviewProcessFinish");
        rlva = new ReviewListViewAdapter(getActivity(), Arrays.asList(mr));
        mReviews.setAdapter(rlva);
    }

    @Override
    public void movieTrailerProcessFinish(MovieTrailer[] mt) {
        Log.v(LOG_TAG, "In movieTrailerProcessFinish");
        tlva = new TrailerListViewAdapter(getActivity(), Arrays.asList(mt));
        mTrailers.setAdapter(tlva);
    }
}

