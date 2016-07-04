package me.kalehv.popmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by harshadkale on 5/18/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    /* Version */
    public static final int DATABASE_VERSION = 2;

    /* Database name */
    static final String DATABASE_NAME = "popmovie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    private void addMovieTable(SQLiteDatabase sqLiteDatabase) {
        // Movie Table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT," +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT," +
                MovieContract.MovieEntry.COLUMN_TRAILER_PATH + "  TEXT," +
                MovieContract.MovieEntry.COLUMN_ADULT + " INTEGER," +
                MovieContract.MovieEntry.COLUMN_TITLE + " BLOB," +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " BLOB," +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER," +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL," +
                MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER," +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }
    
    private void addPopularityTable(SQLiteDatabase sqLiteDatabase) {
        // Popularity Table
        final String SQL_CREATE_POPULARITY_TABLE = "CREATE TABLE " +
                MovieContract.PopularityEntry.TABLE_NAME + " (" +
                MovieContract.PopularityEntry._ID + " INTEGER PRIMARY KEY," +

                " FOREIGN KEY (" + MovieContract.PopularityEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + "), " +
                " UNIQUE (" + MovieContract.PopularityEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE)";


        sqLiteDatabase.execSQL(SQL_CREATE_POPULARITY_TABLE);
    }
    
    private void addHighlyRatedTable(SQLiteDatabase sqLiteDatabase) {
        // HighlyRated Table Table
        final String SQL_CREATE_HIGHLY_RATED_TABLE = "CREATE TABLE " +
                MovieContract.HighlyRatedEntry.TABLE_NAME + " (" +
                MovieContract.HighlyRatedEntry._ID + " INTEGER PRIMARY KEY," +

                " FOREIGN KEY (" + MovieContract.HighlyRatedEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + "), " +
                " UNIQUE (" + MovieContract.HighlyRatedEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE)";

        sqLiteDatabase.execSQL(SQL_CREATE_HIGHLY_RATED_TABLE);
    }

    private void addTrailerTable(SQLiteDatabase sqLiteDatabase) {
        // Review Table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.TrailerEntry.COLUMN_TRAILER_URL + " TEXT," +

                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + "), " +
                " UNIQUE (" + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE)";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }
    
    private void addReviewTable(SQLiteDatabase sqLiteDatabase) {
        // Review Table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT," +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT," +

                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + "), " +
                " UNIQUE (" + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE)";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        addMovieTable(sqLiteDatabase);
        addPopularityTable(sqLiteDatabase);
        addHighlyRatedTable(sqLiteDatabase);
        addTrailerTable(sqLiteDatabase);
        addReviewTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO: Update this method to accommodate for transferring current data.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.PopularityEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.HighlyRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
