package me.kalehv.popmovie.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.R;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Trailer;

/**
 * Created by harshadkale on 7/17/16.
 */
public class TrailersAdapter
        extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private final String TAG = ThumbnailsAdapter.class.getSimpleName();

    private Context context;
    private List<Trailer> trailerList;

    public TrailersAdapter(Context context, List<Trailer> data) {
        this.context = context;
        trailerList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View trailerView = inflater.inflate(R.layout.item_movie_trailer, parent, false);

        return new ViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);
        String youtubeThumbnailUri = trailer.getImageUrl();

        Picasso.with(this.context)
                .load(youtubeThumbnailUri)
                .into(holder.imageView);

        holder.videoUrl = Uri.parse(trailer.getUrl());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.item_movie_trailer_thumbnail)
        ImageView imageView;

        Uri videoUrl;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, videoUrl);
            youtubeIntent.putExtra(C.YOUTUBE_FORCE_FULLSCREEN, true);
            context.startActivity(youtubeIntent);
        }
    }
}
