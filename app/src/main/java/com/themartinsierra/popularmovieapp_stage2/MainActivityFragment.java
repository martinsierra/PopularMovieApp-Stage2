/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
    public MovieFragment() {
}

    @Override
 */
package com.themartinsierra.popularmovieapp_stage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.themartinsierra.popularmovieapp_stage2.data.MoviesContract;

/**
 * Encapsulates fetching the movie and displaying it as a {@link GridView} layout.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TMDB_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,

    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TMDB_ID = 1;
    static final int COL_POSTER_PATH = 2;

    private MovieGridViewAdapter pma;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri somethingUri);
    }

    public MainActivityFragment() {
        Log.v(LOG_TAG, "In MainActivityFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "In onCreate");
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "In onCreateOptionsMenu");
        inflater.inflate(R.menu.mainactivityfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "In onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
       /* if (id == R.id.action_refresh) {
            updateMovie();
            return true;
        }*/
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "In onCreateView");
        pma = new MovieGridViewAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById((R.id.gridview_movies));
        mGridView.setAdapter(pma);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l){
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {

                    ((Callback)getActivity())
                            .onItemSelected(MoviesContract.MovieEntry.buildMovieUriWithTMDBID(cursor.getInt(COL_MOVIE_TMDB_ID)));
                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "In onActivityCreated");
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void updateMovie() {
        Log.v(LOG_TAG, "In updateMovie");
        if (checkSettings()){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String listtype = prefs.getString(getString(R.string.pref_gridtype_key),getString(R.string.pref_value_most_popular));
            FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());
            fetchMovieTask.execute(listtype);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "In onSaveInstanceState");
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "In onCreateLoader");
        if (checkSettings()) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String listtype = prefs.getString(getString(R.string.pref_gridtype_key),getString(R.string.pref_value_most_popular));
            return new CursorLoader(getActivity(),
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    MoviesContract.MovieEntry.COLUMN_MOVIE_TYPE + "= ?",
                    new String [] {listtype},
                    MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " ASC ");
        }
        else {
            return new CursorLoader(getActivity(),
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    MoviesContract.MovieEntry.COLUMN_FAVORITE + "= ?",
                    new String [] {Integer.toString(1)},
                    MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " ASC ");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");
        pma.swapCursor(cursor);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.v(LOG_TAG, "In onLoaderReset");
        pma.swapCursor(null);
    }

    private boolean checkSettings(){
        Log.v(LOG_TAG, "In checkSettings");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String listtype = prefs.getString(getString(R.string.pref_gridtype_key),getString(R.string.pref_value_most_popular));
        return listtype.equals("popular") || listtype.equals("top_rated");
    }
}
