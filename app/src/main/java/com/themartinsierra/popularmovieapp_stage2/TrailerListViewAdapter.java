package com.themartinsierra.popularmovieapp_stage2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.Movie;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.themartinsierra.popularmovieapp_stage2.data.MoviesContract;

import java.util.List;

/**
 * Created by Martin on 1/12/2017.
 */
public class TrailerListViewAdapter extends ArrayAdapter<MovieTrailer> {

    private static final String LOG_TAG = TrailerListViewAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final TextView trailerName;
        public final ImageView imageView;
        public final TextView siteName;
        public ViewHolder(View view) {
            trailerName = (TextView) view.findViewById(R.id.trailer_name);
            imageView = (ImageView) view.findViewById(R.id.trailer_playbutton);
            siteName = (TextView) view.findViewById(R.id.trailer_site_name);
        }
    }

    public TrailerListViewAdapter(Activity context, List<MovieTrailer> movieTrailers){
        super(context, 0, movieTrailers);
        Log.v(LOG_TAG, "In TrailerListViewAdapter");
    }



   @Override
    public View getView(int position, View convertView, ViewGroup parent){
       Log.v(LOG_TAG, "In getView");
       final MovieTrailer trailer = getItem(position);

       if (convertView == null){
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_traileritem, parent, false);
       }

       ViewHolder viewHolder = new ViewHolder(convertView);
       if (trailer.movieTrailerKey != null && trailer.movieTrailerName != null && trailer.movieTrailerSite != null){
           viewHolder.trailerName.setText(trailer.movieTrailerName);
           Picasso.with(getContext()).load(R.drawable.ic_live_tv_black_24dp).into(viewHolder.imageView);
           viewHolder.siteName.setText(trailer.movieTrailerSite);

           viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   final String trailerKey = trailer.movieTrailerKey;
                   if (trailer != null) {

                       Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
                       Intent webIntent = new Intent(Intent.ACTION_VIEW,
                               Uri.parse(getContext().getResources().getString(R.string.youtube) + trailerKey));
                       try {
                           getContext().startActivity(appIntent);
                       } catch (ActivityNotFoundException ex) {
                           getContext().startActivity(webIntent);
                       }
                   }
               }
           });
       }
       else{
           viewHolder.imageView.setVisibility(View.GONE);
           viewHolder.trailerName.setText(getContext().getResources().getString(R.string.no_trailers));
       }
       return convertView;

   }
}
