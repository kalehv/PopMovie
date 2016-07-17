package me.kalehv.popmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.kalehv.popmovie.global.C;
/**
 * Created by harshadkale on 5/18/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    /* Version */
    public static final int DATABASE_VERSION = 2;

    /* Database name */
    private static final String DATABASE_NAME = C.MOVIE_DATABASE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    private void addMovieTable(SQLiteDatabase sqLiteDatabase) {
        // Movie Table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " INTEGER UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_TRAILER_PATH + "  TEXT, " +
                MovieContract.MovieEntry.COLUMN_ADULT + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " BLOB, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " BLOB, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_POPULAR_PAGE_NUMBER + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_RATING_PAGE_NUMBER + " INTEGER" +
                " )";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    private void addTrailerTable(SQLiteDatabase sqLiteDatabase) {
        // Review Table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_KEY + " INTEGER UNIQUE NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_URL + " TEXT, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_IMAGE_URL + " TEXT, " +

                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + ") )";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }
    
    private void addReviewTable(SQLiteDatabase sqLiteDatabase) {
        // Review Table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_KEY + " INTEGER UNIQUE NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT, " +

                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + ") )";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        addMovieTable(sqLiteDatabase);
        addTrailerTable(sqLiteDatabase);
        addReviewTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO: Update this method to accommodate for transferring current data.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
