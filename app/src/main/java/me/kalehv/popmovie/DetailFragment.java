package me.kalehv.popmovie;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.adapters.ReviewsAdapter;
import me.kalehv.popmovie.data.MovieContract;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Review;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;

/**
 * Created by harshadkale on 6/28/16.
 */

public class DetailFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = DetailFragment.class.getSimpleName();

    private static final int MOVIE_LOADER = 0;

//    @BindView(R.id.header)
//    ImageView imageViewHeader;

    @BindView(R.id.imageview_movie_detail_poster)
    ImageView imageViewMovieDetailPoster;

    @BindView(R.id.textview_movie_overview)
    TextView textViewOverview;

    @BindView(R.id.textview_movie_title)
    TextView textViewMovieTitle;

    @BindView(R.id.textview_movie_release_adult)
    TextView textViewMovieReleaseAdult;

    @BindView(R.id.ratingbar_movie_rating)
    RatingBar ratingBarMovieRating;

    @BindView(R.id.recyclerview_movie_review)
    RecyclerView recyclerViewMovieReviews;

    private Bundle args;
    private Uri selectedMovieUri;
    private int actionBarHeight;
    private TheMovieDBServiceManager movieDBServiceManager;
    private ArrayList<Review> reviews;

    public DetailFragment() {
        movieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    public interface DataLoaderCallback {
        public void OnDataLoaded(Cursor data);
    }

    private void setAdapter() {
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getActivity(), reviews);
        recyclerViewMovieReviews.setAdapter(reviewsAdapter);

        recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMovieReviews.setNestedScrollingEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        args = getArguments();

        // When run on wide tablet, detail may not have any selection made
        // in that case there is no data sent by MainActivity to DetailActivity
        // Do not populate views if there is no data passed by MainActivity.
        if (args != null) {
            selectedMovieUri = args.getParcelable(C.MOVIE_PARCEL);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (selectedMovieUri != null) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    selectedMovieUri,
                    C.SELECT_ALL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            setupView(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setupView(final Cursor data) {
//        String backdropPath = data.getString(MovieContract.MovieEntry.COL_INDEX_BACKDROP_PATH);
//        Picasso.with(getActivity())
//                .load(backdropPath)
//                .into(imageViewHeader);

        String posterPath = data.getString(MovieContract.MovieEntry.COL_INDEX_POSTER_PATH);
        Picasso.with(getActivity())
                .load(posterPath)
                .into(imageViewMovieDetailPoster);

        textViewMovieTitle.setText(data.getString(MovieContract.MovieEntry.COL_INDEX_TITLE));
        textViewOverview.setText(data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW));

        String releaseDate = data.getString(MovieContract.MovieEntry.COL_INDEX_RELEASE_DATE);
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
        if (data.getInt(MovieContract.MovieEntry.COL_INDEX_ADULT) == 1) {
            releaseAdult += "   " + getResources().getString(R.string.indicator_adult_movie);
        } else {
            releaseAdult += "   " + getResources().getString(R.string.indicator_universal_movie);
        }

        ratingBarMovieRating.setRating((float) (data.getDouble(MovieContract.MovieEntry.COL_INDEX_VOTE_AVERAGE) / 2.0f));

        textViewMovieReleaseAdult.setText(releaseAdult);
    }
}
