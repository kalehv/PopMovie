package me.kalehv.popmovie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by harshadkale on 5/17/16.
 */
public class PopMoviesContract {

    public static final String CONTENT_AUTHORITY = "me.kalehv.popmovie.movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_POPULARITY = "popularity";
    public static final String PATH_HIGHLY_RATED = "rating";
    public static final String PATH_REVIEW = "review";

    public static final class MovieEntry implements BaseColumns {
        /* Content Provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        /* Table */
        public static final String TABLE_NAME = "movie";

        /* Columns */
        // Primary Key
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_TRAILER_PATH = "trailer_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_FAVORITE = "favorite";

        /* Uri Builders */
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /* Getters */
        public static String getMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PopularityEntry implements BaseColumns {
        /* Content Provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULARITY).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULARITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULARITY;

        /* Table */
        public static final String TABLE_NAME = "popularity";

        /* Columns */
        // Primary Key
        public static final String _ID = "_id";
        // Foreign Key to Movie Table
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        /* Uri Builders */
        // Review for id
        public static Uri buildPopularityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /* Getters */
        public static String getPopularMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class HighlyRatedEntry implements BaseColumns {
        /* Content Provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HIGHLY_RATED).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHLY_RATED;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHLY_RATED;

        /* Table */
        public static final String TABLE_NAME = "highly_rated";

        /* Columns */
        // Primary Key
        public static final String _ID = "_id";
        // Foreign Key to Movie Table
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        /* Uri Builders */
        // Review for id
        public static Uri buildHighlyRatedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /* Getters */
        public static String getHighlyRatedMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        /* Content Provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        /* Table */
        public static final String TABLE_NAME = "review";

        /* Columns */
        // Primary Key
        public static final String _ID = "_id";
        // Foreign Key to Movie Table
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        /* Uri Builders */
        // Review for id
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Review for Movie
        public static Uri buildReviewMovie(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        /* Getters */
        public static String getMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
