package me.kalehv.popmovie.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * Created by hk022893 on 5/20/16.
 */

public class MovieProvider extends ContentProvider {
    private static final String TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MovieDbHelper dbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final int MOVIES_POPULAR = 200;
    private static final int MOVIES_HIGHLY_RATED = 300;
    private static final int TRAILERS = 400;
    private static final int REVIEWS = 500;

    private static final SQLiteQueryBuilder popularMoviesQueryBuilder;
    private static final SQLiteQueryBuilder highlyRatedMoviesQueryBuilder;
    private static final SQLiteQueryBuilder movieQueryBuilder;
    private static final SQLiteQueryBuilder trailerByMovieQueryBuilder;
    private static final SQLiteQueryBuilder reviewByMovieQueryBuilder;

    static {
        popularMoviesQueryBuilder = new SQLiteQueryBuilder();
        popularMoviesQueryBuilder.setTables(
                MovieContract.PopularityEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.PopularityEntry.TABLE_NAME +
                        "." + MovieContract.PopularityEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID
        );

        highlyRatedMoviesQueryBuilder = new SQLiteQueryBuilder();
        highlyRatedMoviesQueryBuilder.setTables(
                MovieContract.HighlyRatedEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.HighlyRatedEntry.TABLE_NAME +
                        "." + MovieContract.HighlyRatedEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID
        );

        movieQueryBuilder = new SQLiteQueryBuilder();
        movieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME
        );

        trailerByMovieQueryBuilder = new SQLiteQueryBuilder();
        trailerByMovieQueryBuilder.setTables(
                MovieContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID
        );

        reviewByMovieQueryBuilder = new SQLiteQueryBuilder();
        reviewByMovieQueryBuilder.setTables(
                MovieContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID
        );
    }

    // Movie.MovieKey = ?
    private static String movieByIdSelection = MovieContract.MovieEntry.TABLE_NAME +
            "." + MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " = ? ";

    private static String trailerByMovieIdSelection = MovieContract.TrailerEntry.TABLE_NAME +
            "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ";

    // Review.MovieKey = ?
    private static String reviewByMovieIdSelection = MovieContract.ReviewEntry.TABLE_NAME +
            "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ";


    static UriMatcher buildUriMatcher() {
        final String authority = MovieContract.CONTENT_AUTHORITY;
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // GET Popular Movies
        matcher.addURI(authority, MovieContract.PATH_POPULARITY, MOVIES_POPULAR);
        // GET Highly Rated Movies
        matcher.addURI(authority, MovieContract.PATH_HIGHLY_RATED, MOVIES_HIGHLY_RATED);
        // GET Movie/Id
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        // GET Trailer/MovieId
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILERS);
        // GET Review/MovieId
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEWS);

        return matcher;
    }

    // TODO: NOT SURE IF THIS WILL WORK
    private Cursor getMovies(Uri uri, String sortOrder) {
        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                new String[]{"*"},
                null,
                null,
                null,
                null,
                sortOrder);
    }

    private Cursor getPopularMovies() {
        return popularMoviesQueryBuilder.query(dbHelper.getReadableDatabase(),
                new String[]{"*"},
                null,
                null,
                null,
                null,
                "DESC");
    }

    private Cursor getHighlyRatedMovies() {
        return highlyRatedMoviesQueryBuilder.query(dbHelper.getReadableDatabase(),
                new String[]{"*"},
                null,
                null,
                null,
                null,
                "DESC");
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String _movieId = String.valueOf(ContentUris.parseId(uri));

        String[] selectionArgs = new String[]{_movieId};
        String selection = movieByIdSelection;

        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getTrailersByMovie(Uri uri, String[] projection, String sortOrder) {
        String _movieId = String.valueOf(ContentUris.parseId(uri));

        String[] selectionArgs = new String[]{_movieId};
        String selection = trailerByMovieIdSelection;

        return trailerByMovieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getReviewsByMovie(Uri uri, String[] projection, String sortOrder) {
        String _movieId = String.valueOf(ContentUris.parseId(uri));

        String[] selectionArgs = new String[]{_movieId};
        String selection = reviewByMovieIdSelection;

        return reviewByMovieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIES_POPULAR:
                return MovieContract.PopularityEntry.CONTENT_DIR_TYPE;
            case MOVIES_HIGHLY_RATED:
                return MovieContract.HighlyRatedEntry.CONTENT_DIR_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_DIR_TYPE;
            case REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                cursor = getMovies(uri, sortOrder);
                break;
            case MOVIES_POPULAR:
                cursor = getPopularMovies();
                break;
            case MOVIES_HIGHLY_RATED:
                cursor = getHighlyRatedMovies();
                break;
            case MOVIE_WITH_ID:
                cursor = getMovieById(uri, projection, sortOrder);
                break;
            case TRAILERS:
                cursor = getTrailersByMovie(uri, projection, sortOrder);
                break;
            case REVIEWS:
                cursor = getReviewsByMovie(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
            case MOVIES_POPULAR: {
                long _id = db.insert(MovieContract.PopularityEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.PopularityEntry.buildPopularityUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
            case MOVIES_HIGHLY_RATED: {
                long _id = db.insert(MovieContract.HighlyRatedEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.HighlyRatedEntry.buildHighlyRatedUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case MOVIES: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MOVIES_POPULAR: {
                rowsDeleted = db.delete(MovieContract.PopularityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MOVIES_HIGHLY_RATED: {
                rowsDeleted = db.delete(MovieContract.HighlyRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILERS: {
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEWS: {
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null && rowsDeleted != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case MOVIES_POPULAR: {
                rowsUpdated = db.update(MovieContract.PopularityEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case MOVIES_HIGHLY_RATED: {
                rowsUpdated = db.update(MovieContract.HighlyRatedEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TRAILERS: {
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case REVIEWS: {
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null && rowsUpdated != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id > 0) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case MOVIES_POPULAR: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.PopularityEntry.TABLE_NAME, null, value);
                        if (_id > 0) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case MOVIES_HIGHLY_RATED: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.HighlyRatedEntry.TABLE_NAME, null, value);
                        if (_id > 0) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case TRAILERS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id > 0) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case REVIEWS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id > 0) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnCount;
    }

    // This doesn't need to be called from actual code. Only needed for testing code.
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
