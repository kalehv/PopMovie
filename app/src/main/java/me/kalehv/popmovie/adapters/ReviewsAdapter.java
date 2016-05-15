package me.kalehv.popmovie.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.kalehv.popmovie.R;
import me.kalehv.popmovie.models.Review;

/**
 * Created by harshadkale on 5/15/16.
 */
public class ReviewsAdapter extends ArrayAdapter {
    private final String TAG = ThumbnailsAdapter.class.getSimpleName();

    private Context context;
    private int layoutResourceId;

    public ReviewsAdapter(Context context, @LayoutRes int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Review review = (Review) getItem(position);
        holder.textViewAuthor.setText(review.getAuthor());
        holder.textViewContent.setText(review.getContent());

        return row;
    }

    static class ViewHolder {
        @Bind(R.id.textview_review_author) TextView textViewAuthor;
        @Bind(R.id.textview_review_content) TextView textViewContent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
