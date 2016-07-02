package me.kalehv.popmovie;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.adapters.ReviewsAdapter;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Movie;
import me.kalehv.popmovie.models.Review;
import me.kalehv.popmovie.models.ReviewsData;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by harshadkale on 6/28/16.
 */

public class DetailFragment
        extends Fragment {

    private final String TAG = DetailFragment.class.getSimpleName();

    private Movie selectedMovie;

    @BindView(R.id.imageview_movie_detail_poster) ImageView imageViewMovieDetailPoster;
    @BindView(R.id.textview_movie_overview) TextView textViewOverview;
    @BindView(R.id.textview_movie_title) TextView textViewMovieTitle;
    @BindView(R.id.textview_movie_release_adult) TextView textViewMovieReleaseAdult;
    @BindView(R.id.ratingbar_movie_rating) RatingBar ratingBarMovieRating;
    @BindView(R.id.recyclerview_movie_review) RecyclerView recyclerViewMovieReviews;

    private Bundle args;
    private TheMovieDBServiceManager movieDBServiceManager;
    private ArrayList<Review> reviews;

    public DetailFragment() {
        movieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        args = getArguments();

        // When run on wide tablet, detail may not have any selection made
        // in that case there is no data sent by MainActivity to DetailActivity
        // Do not populate views if there is no data passed by MainActivity.
        if (args != null) {
            Parcelable movieParcel = args.getParcelable(C.MOVIE_PARCEL);
            selectedMovie = Parcels.unwrap(movieParcel);

            if (selectedMovie != null) {
                setupView();
                fetchReviews();
            }
        }

        return rootView;
    }

    private void setAdapter() {
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getActivity(), reviews);
        recyclerViewMovieReviews.setAdapter(reviewsAdapter);

        recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMovieReviews.setNestedScrollingEnabled(false);
    }

    private void fetchReviews() {
        reviews = new ArrayList<>();
        if (movieDBServiceManager != null) {
            int movieId = selectedMovie.getId();
            if (movieId != -1) {
                movieDBServiceManager.getReviewsData(movieId, 1, new Callback<ReviewsData>() {
                    @Override
                    public void onResponse(Call<ReviewsData> moviesDataCall, Response<ReviewsData> response) {
                        if (response.isSuccessful()) {
                            reviews.addAll(response.body().getReviews());
                            setAdapter();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewsData> reviewsDataCall, Throwable t) {}
                });
            }
        }
    }

    private void setupView() {
        if (!getResources().getBoolean(R.bool.has_two_panes)) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.width_poster_image),
                    (int) getResources().getDimension(R.dimen.height_poster_image)
            );
            float standardMargin = getResources().getDimension(R.dimen.margin_standard);
            float topMargin = getResources().getDimension(R.dimen.negative_poster_top_margin);
            layoutParams.setMargins((int) standardMargin, (int) topMargin, (int) standardMargin, 0);
            imageViewMovieDetailPoster.setLayoutParams(layoutParams);
        }

        String posterPath = selectedMovie.getPosterPath();
        Picasso.with(getActivity())
                .load(C.POSTER_IMAGE_BASE_URL + posterPath)
                .into(imageViewMovieDetailPoster);

        textViewMovieTitle.setText(selectedMovie.getTitle());
        textViewOverview.setText(selectedMovie.getOverview());

        String releaseDate = selectedMovie.getReleaseDate();
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        fromFormat.setLenient(false);
        DateFormat toFormat = DateFormat.getDateInstance();
        toFormat.setLenient(false);
        try {
            Date date = fromFormat.parse(releaseDate);
            releaseDate = toFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "setupView: Date parse exception", e);
        }

        String releaseAdult = releaseDate;
        if (selectedMovie.getAdult()) {
            releaseAdult += "   " + getResources().getString(R.string.indicator_adult_movie);
        } else {
            releaseAdult += "   " + getResources().getString(R.string.indicator_universal_movie);
        }

        ratingBarMovieRating.setRating((float) (selectedMovie.getVoteAverage() / 2.0f));

        textViewMovieReleaseAdult.setText(releaseAdult);
    }
}
