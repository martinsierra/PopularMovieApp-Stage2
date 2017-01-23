package com.themartinsierra.popularmovieapp_stage2;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Martin on 1/12/2017.
 */
public class ReviewListViewAdapter extends ArrayAdapter<MovieReview> {


    private static final String LOG_TAG = ReviewListViewAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final TextView authorName;
        public final TextView reviewContent;
        public final TextView authorNameTV;
        public ViewHolder(View view) {
            authorName = (TextView) view.findViewById(R.id.review_author_name);
            reviewContent = (TextView) view.findViewById(R.id.review_content);
            authorNameTV = (TextView) view.findViewById(R.id.review_author_namefield);
        }
    }

    public ReviewListViewAdapter(Activity context, List<MovieReview> movieReviews) {
        super(context, 0, movieReviews);
        Log.v(LOG_TAG, "In ReviewListViewAdapter");
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.v(LOG_TAG, "In getView");
        MovieReview movie = getItem(position);

        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(convertView);
        if ((movie.reviewAuthor != null && movie.reviewContent != null)){

            viewHolder.authorName.setText(movie.reviewAuthor);
            viewHolder.reviewContent.setText(movie.reviewContent);
        }
        else {
            viewHolder.authorName.setVisibility(View.GONE);
            viewHolder.reviewContent.setText(getContext().getResources().getString(R.string.no_reviews));
            viewHolder.authorNameTV.setVisibility((View.GONE));
        }
        return convertView;
    }
}
