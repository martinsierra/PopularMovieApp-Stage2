package com.themartinsierra.popularmovieapp_stage2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.themartinsierra.popularmovieapp_stage2.data.MoviesContract;

/**
 * Created by Martin on 9/4/2016.
 * Used https://github.com/nomanr/android-databinding-example/blob/master/app/src/main/java/com/databinding/example/databindingexample/adapters/SimpleAdapter.java
 * as reference.
 */
public class MovieGridViewAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieGridViewAdapter.class.getSimpleName();

    public static class ViewHolder {

        public final ImageView moviePoster;

        public ViewHolder(View view) {
            moviePoster = (ImageView) view.findViewById(R.id.movie_image);
        }
    }

    public MovieGridViewAdapter(Context context, Cursor c, int flags) {
        super(context, c , flags);
        Log.v(LOG_TAG, "In MovieGridViewAdapter");
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, "In newView");
        int layoutId = R.layout.gridview_movieitem;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
       This is where we fill-in the views with the contents of the cursor.
    */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "In bind View");

        ViewHolder viewHolder = (ViewHolder)view.getTag();
        int moviePosterIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);
        String testUri = cursor.getString(moviePosterIndex);
        Uri posterUri = Uri.parse(testUri);
        Picasso.with(context).load(posterUri).into(viewHolder.moviePoster);
    }
}
