package me.kalehv.popmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by harshadkale on 5/18/16.
 */
public class PopMoviesDbHelper extends SQLiteOpenHelper {
    /* Version */
    public static final int DATABASE_VERSION = 2;

    /* Database name */
    static final String DATABASE_NAME = "popmovie.db";

    public PopMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Movie Table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                PopMoviesContract.MovieEntry.TABLE_NAME + " (" +
                PopMoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                PopMoviesContract.MovieEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
                PopMoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT," +
                PopMoviesContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT," +
                PopMoviesContract.MovieEntry.COLUMN_TRAILER_PATH + "  TEXT," +
                PopMoviesContract.MovieEntry.COLUMN_ADULT + " INTEGER," +
                PopMoviesContract.MovieEntry.COLUMN_TITLE + " BLOB," +
                PopMoviesContract.MovieEntry.COLUMN_OVERVIEW + " BLOB," +
                PopMoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER," +
                PopMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL," +
                PopMoviesContract.MovieEntry.COLUMN_FAVORITE + " INTEGER," +
                " );";

        // Popularity Table
        final String SQL_CREATE_POPULARITY_TABLE = "CREATE TABLE " +
                PopMoviesContract.PopularityEntry.TABLE_NAME + " (" +
                PopMoviesContract.PopularityEntry._ID + " INTEGER PRIMARY KEY," +

                " FOREIGN KEY (" + PopMoviesContract.PopularityEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                PopMoviesContract.MovieEntry.TABLE_NAME + " (" + PopMoviesContract.MovieEntry._ID + "), " +
                " UNIQUE (" + PopMoviesContract.PopularityEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE)";

        // HighlyRated Table Table
        final String SQL_CREATE_HIGHLY_RATED_TABLE = "CREATE TABLE " +
                PopMoviesContract.HighlyRatedEntry.TABLE_NAME + " (" +
                PopMoviesContract.HighlyRatedEntry._ID + " INTEGER PRIMARY KEY," +

                " FOREIGN KEY (" + PopMoviesContract.HighlyRatedEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                PopMoviesContract.MovieEntry.TABLE_NAME + " (" + PopMoviesContract.MovieEntry._ID + "), " +
                " UNIQUE (" + PopMoviesContract.HighlyRatedEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE)";

        // Review Table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                PopMoviesContract.ReviewEntry.TABLE_NAME + " (" +
                PopMoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                PopMoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT," +
                PopMoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT," +

                " FOREIGN KEY (" + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                PopMoviesContract.MovieEntry.TABLE_NAME + " (" + PopMoviesContract.MovieEntry._ID + "), " +
                " UNIQUE (" + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE)";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POPULARITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HIGHLY_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO: Update this method to accommodate for transferring current data.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.PopularityEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.HighlyRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
