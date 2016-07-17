package me.kalehv.popmovie.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import me.kalehv.popmovie.R;
import me.kalehv.popmovie.data.MovieContract;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Movie;
import me.kalehv.popmovie.models.MoviesData;
import me.kalehv.popmovie.models.Review;
import me.kalehv.popmovie.models.ReviewsData;
import me.kalehv.popmovie.models.Trailer;
import me.kalehv.popmovie.models.TrailersData;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_ADULT;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_BACKDROP_PATH;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_FAVORITE;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_MOVIE_KEY;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_OVERVIEW;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_POPULARITY;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_POPULAR_PAGE_NUMBER;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_POSTER_PATH;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_RATING_PAGE_NUMBER;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_RELEASE_DATE;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_TITLE;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_TRAILER_PATH;
import static me.kalehv.popmovie.data.MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE;

/**
 * Created by harshadkale on 7/3/16.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String TAG = MovieSyncAdapter.class.getSimpleName();

    private static OnSyncListener onSyncListener;
    private Context context;

    public static final int SYNC_INTERVAL = 60 * 1440; // Sync once in 24 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;


    private static final String SYNC_DATA_TYPE = "SYNC_DATA_TYPE";
    public static final int SYNC_MOVIES_DATA = 0; // Keep this default to 0
    public static final int SYNC_TRAILERS_DATA = 1;
    public static final int SYNC_REVIEWS_DATA = 2;

    public interface OnSyncListener {
        void onSyncComplete(int syncDataType);
    }

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    public void fetchMoviesData(final String filter, final int pageNumber) {
        TheMovieDBServiceManager movieDBServiceManager = TheMovieDBServiceManager.getInstance();
        movieDBServiceManager.getMoviesData(filter, pageNumber, new Callback<MoviesData>() {
            @Override
            public void onResponse(Call<MoviesData> call, Response<MoviesData> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Got the response");
                    MoviesData moviesData = response.body();
                    if (moviesData != null) {
                        saveMovies(moviesData, filter, pageNumber);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesData> call, Throwable t) {
                Log.d(TAG, "onResponse: Got error fetching movies");
            }
        });
    }

    public void fetchReviewsData(final int movieId, int pageNumber) {
        TheMovieDBServiceManager movieDBServiceManager = TheMovieDBServiceManager.getInstance();
        movieDBServiceManager.getReviewsData(movieId, pageNumber, new Callback<ReviewsData>() {
            @Override
            public void onResponse(Call<ReviewsData> call, Response<ReviewsData> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Got the response");
                    ReviewsData reviewsData = response.body();
                    if (reviewsData != null && reviewsData.getMovieId() == movieId) {
                        saveReviews(reviewsData, movieId);
                    } else {
                        Log.d(TAG, "onResponse: Reviews data mismatch");
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewsData> call, Throwable t) {
                Log.d(TAG, "onFailure: Got error fetching reviews");
            }
        });
    }

    public void fetchTrailersData(final int movieId) {
        TheMovieDBServiceManager movieDBServiceManager = TheMovieDBServiceManager.getInstance();
        movieDBServiceManager.getTrailersData(movieId, new Callback<TrailersData>() {
            @Override
            public void onResponse(Call<TrailersData> call, Response<TrailersData> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Got the response");
                    TrailersData trailersData = response.body();
                    if (trailersData != null && trailersData.getMovieId() == movieId) {
                        saveTrailers(trailersData, movieId);
                    } else {
                        Log.d(TAG, "onResponse: Trailers data mismatch");
                    }
                }
            }

            @Override
            public void onFailure(Call<TrailersData> call, Throwable t) {
                Log.d(TAG, "onFailure: Got error fetching trailers");
            }
        });
    }

    private void saveReviews(ReviewsData reviewsData, int movieId) {
        List<Review> reviewList = reviewsData.getReviews();
        if (reviewList != null) {
            Vector<ContentValues> contentValuesVector = new Vector<>(reviewList.size());

            for (int i = 0; i < reviewList.size(); i++) {
                Review review = reviewList.get(i);

                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieId);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_KEY, review.getId());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());

                contentValuesVector.add(reviewValues);
            }

            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValues);
                getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, contentValues);
            }

            if (onSyncListener != null) {
                onSyncListener.onSyncComplete(SYNC_REVIEWS_DATA);
            }
        }
    }

    private void saveTrailers(TrailersData trailersData, int movieId) {
        List<Trailer> trailerList = trailersData.getTrailers();
        if (trailerList != null) {
            Vector<ContentValues> contentValuesVector = new Vector<>(trailerList.size());

            for (int i = 0; i < trailerList.size(); i++) {
                Trailer trailer = trailerList.get(i);

                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieId);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.getId());
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_URL, C.YOUTUBE_VIDEO_URL + trailer.getKey());

                contentValuesVector.add(trailerValues);
            }

            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValues);
                getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);
            }

            if (onSyncListener != null) {
                onSyncListener.onSyncComplete(SYNC_TRAILERS_DATA);
            }
        }
    }

    private void saveMovies(MoviesData moviesData, String filter, int pageNumber) {
        List<Movie> movieList = moviesData.getMovies();
        if (movieList != null) {
            Vector<ContentValues> contentValuesVector = new Vector<>(movieList.size());

            for (int i = 0; i < movieList.size(); i++) {
                Movie movie = movieList.get(i);
                if (filter.equals(getContext().getResources().getString(R.string.pref_filter_popular))) {
                    movie.setPopularPageNumber(pageNumber);
                    movie.setRatingPageNumber(0);
                } else {
                    movie.setRatingPageNumber(pageNumber);
                    movie.setPopularPageNumber(0);
                }

                ContentValues movieValues = new ContentValues();

                movieValues.put(COLUMN_MOVIE_KEY, movie.getMovieId());
                movieValues.put(COLUMN_POSTER_PATH, C.POSTER_IMAGE_BASE_URL + movie.getPosterPath());
                movieValues.put(COLUMN_BACKDROP_PATH, C.BACKDROP_IMAGE_BASE_URL + movie.getBackdropPath());
                movieValues.put(COLUMN_TRAILER_PATH, movie.getVideo());
                movieValues.put(COLUMN_ADULT, movie.getAdult());
                movieValues.put(COLUMN_TITLE, movie.getTitle());
                movieValues.put(COLUMN_OVERVIEW, movie.getOverview());
                movieValues.put(COLUMN_RELEASE_DATE, movie.getReleaseDate());
                movieValues.put(COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                movieValues.put(COLUMN_POPULARITY, movie.getPopularity());
                movieValues.put(COLUMN_FAVORITE, movie.getFavorite());
                movieValues.put(COLUMN_POPULAR_PAGE_NUMBER, movie.getPopularPageNumber());
                movieValues.put(COLUMN_RATING_PAGE_NUMBER, movie.getRatingPageNumber());

                contentValuesVector.add(movieValues);
            }

            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValues);
                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            }

            if (onSyncListener != null) {
                onSyncListener.onSyncComplete(SYNC_MOVIES_DATA);
            }
            Log.d(TAG, "saveMovies: Done Saving");
        }
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle bundle,
            String s,
            ContentProviderClient contentProviderClient,
            SyncResult syncResult) {

        Log.d(TAG, "onPerformSync: Performing Sync");

        int syncDataType = bundle.getInt(SYNC_DATA_TYPE);
        switch (syncDataType) {
            case SYNC_MOVIES_DATA: {
                fetchMoviesData(getContext().getString(R.string.pref_filter_top_rated), 1);
                fetchMoviesData(getContext().getString(R.string.pref_filter_popular), 1);
                // Page 2
                fetchMoviesData(getContext().getString(R.string.pref_filter_top_rated), 2);
                fetchMoviesData(getContext().getString(R.string.pref_filter_popular), 2);
                break;
            }
            case SYNC_REVIEWS_DATA: {
                int movieKey = bundle.getInt(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY);
                fetchReviewsData(movieKey, 1);
                break;
            }
            case SYNC_TRAILERS_DATA: {
                int movieKey = bundle.getInt(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY);
                fetchTrailersData(movieKey);
                break;
            }
        }
    }

    public void setOnSyncListener(OnSyncListener listener) {
        MovieSyncAdapter.onSyncListener = listener;
    }

    public void syncImmediately() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(
                getSyncAccount(),
                context.getString(R.string.content_authority),
                bundle
        );
    }

    public void syncMovies() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(SYNC_DATA_TYPE, SYNC_MOVIES_DATA);
        ContentResolver.requestSync(
                getSyncAccount(),
                context.getString(R.string.content_authority),
                bundle
        );
    }

    public void syncTrailers(int movieKey) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(SYNC_DATA_TYPE, SYNC_TRAILERS_DATA);
        bundle.putInt(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, movieKey);
        ContentResolver.requestSync(
                getSyncAccount(),
                context.getString(R.string.content_authority),
                bundle
        );
    }

    public void syncReviews(int movieKey) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(SYNC_DATA_TYPE, SYNC_REVIEWS_DATA);
        bundle.putInt(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, movieKey);
        ContentResolver.requestSync(
                getSyncAccount(),
                context.getString(R.string.content_authority),
                bundle
        );
    }

    public Account getSyncAccount() {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount);
        }

        return newAccount;
    }

    public void onAccountCreated(Account newAccount) {
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately();
    }

    public void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount();
        String authority = context.getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();

            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }
}
