package me.kalehv.popmovie;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import me.kalehv.popmovie.adapters.TrailersAdapter;
import me.kalehv.popmovie.data.MovieContract;
import me.kalehv.popmovie.data.MovieProvider;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Review;
import me.kalehv.popmovie.models.Trailer;
import me.kalehv.popmovie.sync.MovieSyncAdapter;

/**
 * Created by harshadkale on 6/28/16.
 */

public class DetailFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, MovieSyncAdapter.OnSyncListener {

    private final String TAG = DetailFragment.class.getSimpleName();

    private static final int MOVIE_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;

    //region ButterKnife Declarations
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

    @BindView(R.id.recyclerview_movie_trailer)
    RecyclerView recyclerViewMovieTrailers;
    //endregion

    FloatingActionButton fabFavoriteMovie;

    private Bundle args;
    private Uri selectedMovieUri;
    private boolean isFavorite;
    private int movieKey;
    private boolean areTrailersFetchedFromServer = false;
    private boolean areReviewsFetchedFromServer = false;

    private View.OnClickListener onFavoriteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toggleMovieFavorite();
        }
    };

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

        fabFavoriteMovie = (FloatingActionButton) getActivity().findViewById(R.id.fab_favorite_movie);
        fabFavoriteMovie.setOnClickListener(onFavoriteClickListener);
        fabFavoriteMovie.setVisibility(View.VISIBLE);

        args = getArguments();

        // When run on wide tablet, detail may not have any selection made
        // in that case there is no data sent by MainActivity to DetailActivity
        // Do not populate views if there is no data passed by MainActivity.
        if (args != null) {
            selectedMovieUri = args.getParcelable(C.MOVIE_PARCEL);
            MovieSyncAdapter movieSyncAdapter = new MovieSyncAdapter(getActivity(), true);
            movieSyncAdapter.setOnSyncListener(this);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (selectedMovieUri != null) {
            switch (id) {
                case MOVIE_LOADER:
                    return new CursorLoader(
                            getActivity(),
                            selectedMovieUri,
                            C.SELECT_ALL_COLUMNS,
                            null,
                            null,
                            null
                    );
                case TRAILERS_LOADER:
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.TrailerEntry.buildTrailerUriForMovie(movieKey),
                            C.SELECT_ALL_COLUMNS,
                            null,
                            null,
                            null
                    );
                case REVIEWS_LOADER:
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.ReviewEntry.buildReviewUriForMovie(movieKey),
                            C.SELECT_ALL_COLUMNS,
                            null,
                            null,
                            null
                    );

            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIE_LOADER:
                if (data != null && data.moveToFirst()) {
                    setupView(data);
                    isFavorite = data.getInt(MovieContract.MovieEntry.COL_INDEX_FAVORITE) != 0;
                    movieKey = data.getInt(MovieContract.MovieEntry.COL_INDEX_MOVIE_KEY);
                    getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
                    getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
                }
                break;
            case TRAILERS_LOADER:
                if (data != null && data.moveToFirst()) {
                    setTrailersView(data);
                } else if (!areTrailersFetchedFromServer) {
                    MovieSyncAdapter movieSyncAdapter = new MovieSyncAdapter(getActivity(), true);
                    movieSyncAdapter.syncTrailers(movieKey);
                    movieSyncAdapter.setOnSyncListener(this);
                }
                break;
            case REVIEWS_LOADER:
                if (data != null && data.moveToFirst()) {
                    setReviewsView(data);
                } else if (!areReviewsFetchedFromServer) {
                    MovieSyncAdapter movieSyncAdapter = new MovieSyncAdapter(getActivity(), true);
                    movieSyncAdapter.syncReviews(movieKey);
                    movieSyncAdapter.setOnSyncListener(this);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setupView(final Cursor data) {
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

    private void toggleMovieFavorite() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, isFavorite ? 0 : 1);

        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                isFavorite ? R.string.snack_movie_unfavorited : R.string.snack_movie_favorited,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_undo, onFavoriteClickListener).show();

        getActivity().getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                contentValues,
                MovieProvider.movieByKeySelection,
                new String[]{String.valueOf(movieKey)}
        );

        isFavorite = !isFavorite;
    }

    private void setReviewsView(Cursor data) {
        ArrayList<Review> reviewArrayList = new ArrayList<>();
        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            // The Cursor is now set to the right position
            Review review = new Review();
            review.setAuthor(data.getString(MovieContract.ReviewEntry.COL_INDEX_AUTHOR));
            review.setContent(data.getString(MovieContract.ReviewEntry.COL_INDEX_CONTENT));
            reviewArrayList.add(review);
        }

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getActivity(), reviewArrayList);
        recyclerViewMovieReviews.setAdapter(reviewsAdapter);

        recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMovieReviews.setNestedScrollingEnabled(false);
    }

    private void setTrailersView(Cursor data) {
        ArrayList<Trailer> trailerArrayList = new ArrayList<>();
        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            // The Cursor is now set to the right position
            Trailer trailer = new Trailer();
            trailer.setUrl(data.getString(MovieContract.TrailerEntry.COL_INDEX_TRAILER_URL));
            trailer.setImageUrl(data.getString(MovieContract.TrailerEntry.COL_INDEX_TRAILER_IMAGE_URL));
            trailerArrayList.add(trailer);
        }

        TrailersAdapter trailersAdapter = new TrailersAdapter(getActivity(), trailerArrayList);
        recyclerViewMovieTrailers.setAdapter(trailersAdapter);

        recyclerViewMovieTrailers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovieTrailers.setNestedScrollingEnabled(false);
    }

    @Override
    public void onSyncComplete(int syncDataType) {
        switch (syncDataType) {
            case MovieSyncAdapter.SYNC_TRAILERS_DATA:
                getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
                areTrailersFetchedFromServer = true;
                break;
            case MovieSyncAdapter.SYNC_REVIEWS_DATA:
                getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
                areReviewsFetchedFromServer = true;
                break;
        }
    }
}
