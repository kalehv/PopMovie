package me.kalehv.popmovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.R;
import me.kalehv.popmovie.models.Review;

/**
 * Created by harshadkale on 5/15/16.
 */
public class ReviewsAdapter
        extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final String TAG = ThumbnailsAdapter.class.getSimpleName();

    private Context context;
    private List<Review> reviewList;

    public ReviewsAdapter(Context context, List<Review> data) {
        this.context = context;
        reviewList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View reviewView = inflater.inflate(R.layout.item_movie_review, parent, false);

        return new ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.textViewAuthor.setText(review.getAuthor().toUpperCase());
        holder.textViewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_review_author) TextView textViewAuthor;
        @BindView(R.id.textview_review_content) TextView textViewContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
