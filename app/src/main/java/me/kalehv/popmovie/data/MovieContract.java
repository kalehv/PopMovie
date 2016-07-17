package me.kalehv.popmovie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by harshadkale on 5/17/16.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "me.kalehv.popmovie.movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final class MovieEntry implements BaseColumns {
        /* Content Provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_MOVIES;

        /* Table */
        public static final String TABLE_NAME = "movie";

        /* Columns */
        // Primary Key
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_TRAILER_PATH = "trailer_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_POPULAR_PAGE_NUMBER = "popular_page_number";
        public static final String COLUMN_RATING_PAGE_NUMBER = "rating_page_number";

        public static final int COL_INDEX_MOVIE_ID = 0;
        public static final int COL_INDEX_MOVIE_KEY = 1;
        public static final int COL_INDEX_POSTER_PATH = 2;
        public static final int COL_INDEX_BACKDROP_PATH = 3;
        public static final int COL_INDEX_TRAILER_PATH = 4;
        public static final int COL_INDEX_ADULT = 5;
        public static final int COL_INDEX_TITLE = 6;
        public static final int COL_INDEX_OVERVIEW = 7;
        public static final int COL_INDEX_RELEASE_DATE = 8;
        public static final int COL_INDEX_VOTE_AVERAGE = 9;
        public static final int COL_INDEX_POPULARITY = 10;
        public static final int COL_INDEX_FAVORITE = 11;
        public static final int COL_INDEX_POPULAR_PAGE_NUMBER = 12;
        public static final int COL_INDEX_RATING_PAGE_NUMBER = 13;

        /* Uri Builders */
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /* Getters */
        public static String getMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_TRAILER).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_TRAILER;

        /* Table */
        public static final String TABLE_NAME = "trailer";

        /* Columns */
        // Foreign Key to Movie Table
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_TRAILER_KEY = "trailer_id";
        public static final String COLUMN_TRAILER_URL = "trailer_url";

        public static final int COL_INDEX_ID = 0;
        public static final int COL_INDEX_MOVIE_KEY = 1;
        public static final int COL_INDEX_TRAILER_KEY = 2;
        public static final int COL_INDEX_TRAILER_URL = 3;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerUriForMovie(int movieKey) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieKey)).build();
        }

        public static String getMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        /* Content Provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_REVIEW;

        /* Table */
        public static final String TABLE_NAME = "review";

        /* Columns */
        // Foreign Key to Movie Table
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_REVIEW_KEY = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        public static final int COL_INDEX_ID = 0;
        public static final int COL_INDEX_MOVIE_KEY = 1;
        public static final int COL_INDEX_REVIEW_KEY = 2;
        public static final int COL_INDEX_AUTHOR = 3;
        public static final int COL_INDEX_CONTENT = 4;

        /* Uri Builders */
        // Review for id
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Review for Movie
        public static Uri buildReviewUriForMovie(int movieKey) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieKey)).build();
        }

        /* Getters */
        public static String getMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
