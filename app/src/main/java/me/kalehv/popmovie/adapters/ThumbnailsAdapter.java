package me.kalehv.popmovie.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.BuildConfig;
import me.kalehv.popmovie.R;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Movie;

/**
 * Created by harshadkale on 4/9/16.
 * Articles referred -
 * http://developer.android.com/training/improving-layouts/smooth-scrolling.html
 * http://javatechig.com/android/android-gridview-example-building-image-gallery-in-android
 */
public class ThumbnailsAdapter extends ArrayAdapter {
    private final String TAG = ThumbnailsAdapter.class.getSimpleName();

    private Context context;
    private int layoutResourceId;

    public ThumbnailsAdapter(Context context, @LayoutRes int layoutResourceId, ArrayList data) {
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

        Movie movie = (Movie) getItem(position);

        String posterPath = movie.getPosterPath();
        if (posterPath.charAt(0) == '/') {
            posterPath = posterPath.substring(1, posterPath.length());
        }

        Uri posterUri = Uri.parse(C.POSTER_IMAGE_BASE_URL).buildUpon()
                .appendPath(posterPath)
                .appendQueryParameter(C.API_KEY_QUERY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        holder.image.setAdjustViewBounds(true);

        Picasso.with(this.context)
                .load(posterUri)
                .into(holder.image);

        return row;
    }

    static class ViewHolder {
        @BindView(R.id.imageview_movie_poster) ImageView image;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
