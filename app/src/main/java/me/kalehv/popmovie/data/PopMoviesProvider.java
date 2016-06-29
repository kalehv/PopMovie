package me.kalehv.popmovie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by hk022893 on 5/20/16.
 */

public class PopMoviesProvider extends ContentProvider {
    private static final String TAG = PopMoviesProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopMoviesDbHelper mDbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIE_BY_ID = 101;
    private static final int MOVIES_POPULAR = 102;
    private static final int MOVIES_HIGHLY_RATED = 103;
    private static final int REVIEWS_BY_MOVIE = 300;

    private static final SQLiteQueryBuilder sPopularMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sHighlyRatedMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    private static final SQLiteQueryBuilder sReviewByMovieQueryBuilder;

    static {
        sPopularMoviesQueryBuilder = new SQLiteQueryBuilder();
        sPopularMoviesQueryBuilder.setTables(
                PopMoviesContract.PopularityEntry.TABLE_NAME + " INNER JOIN " +
                        PopMoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + PopMoviesContract.PopularityEntry.TABLE_NAME +
                        "." + PopMoviesContract.PopularityEntry.COLUMN_MOVIE_KEY +
                        " = " + PopMoviesContract.MovieEntry.TABLE_NAME +
                        "." + PopMoviesContract.MovieEntry._ID
        );

        sHighlyRatedMoviesQueryBuilder = new SQLiteQueryBuilder();
        sHighlyRatedMoviesQueryBuilder.setTables(
                PopMoviesContract.HighlyRatedEntry.TABLE_NAME + " INNER JOIN " +
                        PopMoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + PopMoviesContract.HighlyRatedEntry.TABLE_NAME +
                        "." + PopMoviesContract.HighlyRatedEntry.COLUMN_MOVIE_KEY +
                        " = " + PopMoviesContract.MovieEntry.TABLE_NAME +
                        "." + PopMoviesContract.MovieEntry._ID
        );

        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(
                PopMoviesContract.MovieEntry.TABLE_NAME
        );

        sReviewByMovieQueryBuilder = new SQLiteQueryBuilder();
        sReviewByMovieQueryBuilder.setTables(
                PopMoviesContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        PopMoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + PopMoviesContract.ReviewEntry.TABLE_NAME +
                        "." + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + PopMoviesContract.MovieEntry.TABLE_NAME +
                        "." + PopMoviesContract.MovieEntry._ID
        );
    }

    // Movie.MovieKey = ?
    private static String sMovieByIdSelection = PopMoviesContract.MovieEntry.TABLE_NAME +
            "." + PopMoviesContract.MovieEntry.COLUMN_MOVIE_KEY + " = ? ";

    // Review.MovieKey = ?
    private static String sReviewByMovieIdSelection = PopMoviesContract.ReviewEntry.TABLE_NAME +
            "." + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopMoviesContract.CONTENT_AUTHORITY;

        // GET Popular Movies
        matcher.addURI(authority, PopMoviesContract.PATH_POPULARITY, MOVIES_POPULAR);

        // GET Highly Rated Movies
        matcher.addURI(authority, PopMoviesContract.PATH_POPULARITY, MOVIES_HIGHLY_RATED);

        // GET Movie/Id
        matcher.addURI(authority, PopMoviesContract.PATH_MOVIE + "/#", MOVIE_BY_ID);

        // GET Review/MovieId
        matcher.addURI(authority, PopMoviesContract.PATH_REVIEW + "/#", REVIEWS_BY_MOVIE);

        return matcher;
    }

    // TODO: NOT SURE IF THIS WILL WORK
    private Cursor getMovies(Uri uri, String sortOrder) {
        return sMovieQueryBuilder.query(mDbHelper.getReadableDatabase(),
                new String[]{"*"},
                null,
                null,
                null,
                null,
                sortOrder);
    }

    private Cursor getPopularMovies() {
        return sPopularMoviesQueryBuilder.query(mDbHelper.getReadableDatabase(),
                new String[]{"*"},
                null,
                null,
                null,
                null,
                "DESC");
    }

    private Cursor getHighlyRatedMovies() {
        return sHighlyRatedMoviesQueryBuilder.query(mDbHelper.getReadableDatabase(),
                new String[]{"*"},
                null,
                null,
                null,
                null,
                "DESC");
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String movieId = PopMoviesContract.MovieEntry.getMovieFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = sMovieByIdSelection;

        return sMovieQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getReviewsByMovie(Uri uri, String[] projection, String sortOrder) {
        String movieId = PopMoviesContract.ReviewEntry.getMovieFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = sReviewByMovieIdSelection;

        return sReviewByMovieQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PopMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                cursor = getMovies(uri, sortOrder);
                break;
            case MOVIES_POPULAR:
                cursor = getPopularMovies();
                break;
            case MOVIES_HIGHLY_RATED:
                cursor = getHighlyRatedMovies();
                break;
            case MOVIE_BY_ID:
                cursor = getMovieById(uri, projection, sortOrder);
                break;
            case REVIEWS_BY_MOVIE:
                cursor = getReviewsByMovie(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return PopMoviesContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIES_POPULAR:
                return PopMoviesContract.PopularityEntry.CONTENT_DIR_TYPE;
            case MOVIES_HIGHLY_RATED:
                return PopMoviesContract.HighlyRatedEntry.CONTENT_DIR_TYPE;
            case MOVIE_BY_ID:
                return PopMoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEWS_BY_MOVIE:
                return PopMoviesContract.ReviewEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    // This doesn't need to be called from actual code. Only needed for testing code.
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
