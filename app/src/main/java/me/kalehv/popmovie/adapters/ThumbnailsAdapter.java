package me.kalehv.popmovie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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
public class ThumbnailsAdapter
        extends CursorRecyclerViewAdapter<ThumbnailsAdapter.ViewHolder> {
    private final String TAG = ThumbnailsAdapter.class.getSimpleName();

    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Cursor cursor);
    }

    public ThumbnailsAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String posterPath = cursor.getString(MovieContract.MovieEntry.COL_INDEX_POSTER_PATH);
        if (posterPath != null) {
            Uri posterUri = Uri.parse(posterPath);

            viewHolder.image.setAdjustViewBounds(true);

            Picasso.with(this.context)
                    .load(posterUri)
                    .into(viewHolder.image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_movies, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.imageview_movie_poster)
        ImageView image;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View view) {
            if (view instanceof ImageView && onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getCursor());
            }
        }
    }
}
