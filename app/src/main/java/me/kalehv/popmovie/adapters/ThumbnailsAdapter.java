package me.kalehv.popmovie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.R;
import me.kalehv.popmovie.data.MovieContract;

/**
 * Created by harshadkale on 4/9/16.
 * Articles referred -
 * http://developer.android.com/training/improving-layouts/smooth-scrolling.html
 * http://javatechig.com/android/android-gridview-example-building-image-gallery-in-android
 */
public class ThumbnailsAdapter extends CursorAdapter {
    private final String TAG = ThumbnailsAdapter.class.getSimpleName();

    private Context context;

    public ThumbnailsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_movies, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String posterPath = cursor.getString(MovieContract.MovieEntry.COL_INDEX_POSTER_PATH);
        if (posterPath != null) {
            Uri posterUri = Uri.parse(posterPath);

            viewHolder.image.setAdjustViewBounds(true);

            Picasso.with(this.context)
                    .load(posterUri)
                    .into(viewHolder.image);
        }
    }

    static class ViewHolder {
        @BindView(R.id.imageview_movie_poster) ImageView image;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
