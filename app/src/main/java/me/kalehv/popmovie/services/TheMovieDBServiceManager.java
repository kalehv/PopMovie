package me.kalehv.popmovie.services;

import com.google.gson.JsonObject;

import me.kalehv.popmovie.BuildConfig;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.MoviesData;
import me.kalehv.popmovie.models.ReviewsData;
import me.kalehv.popmovie.models.TrailersData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by harshadkale on 4/10/16.
 */
public class TheMovieDBServiceManager {

    private final String TAG = TheMovieDBServiceManager.class.getSimpleName();

    private static TheMoviesDBApi moviesDBApi = null;
    private static TheMovieDBServiceManager movieDBServiceManager = null;
    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;


    private TheMovieDBServiceManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(C.THE_MOVIES_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        moviesDBApi = retrofit.create(TheMoviesDBApi.class);
    }

    public static TheMovieDBServiceManager getInstance() {
        if (movieDBServiceManager == null) {
            movieDBServiceManager = new TheMovieDBServiceManager();
        }
        return movieDBServiceManager;
    }

    /*
        Movies
     */
    public void getMoviesData(String filter, int pageNum, Callback<MoviesData> callback) {
        Call<MoviesData> moviesDataCall = moviesDBApi.getMoviesData(filter, pageNum, API_KEY);
        moviesDataCall.enqueue(callback);
    }

    /*
        Videos
     */
    public void getMoviesVideos(int movieId, Callback<JsonObject> callback) {
        Call<JsonObject> videoKeyCall = moviesDBApi.getMovieVideoKey(movieId, API_KEY);
        videoKeyCall.enqueue(callback);
    }


    /*
        Trailers
     */
    public void getTrailersData(int movieId, Callback<TrailersData> callback) {
        Call<TrailersData> trailersDataCall = moviesDBApi.getTrailersData(movieId, API_KEY);
        trailersDataCall.enqueue(callback);
    }
    /*
        Reviews
     */
    public void getReviewsData(int movieId, int pageNum, Callback<ReviewsData> callback) {
        Call<ReviewsData> reviewsDataCall = moviesDBApi.getReviewsData(movieId, pageNum, API_KEY);
        reviewsDataCall.enqueue(callback);
    }
}
