package me.kalehv.popmovie.services;

import com.google.gson.JsonObject;

import me.kalehv.popmovie.BuildConfig;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.MoviesData;
import me.kalehv.popmovie.models.ReviewsData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by harshadkale on 4/10/16.
 */
public class TheMovieDBServiceManager {

    private final String TAG = TheMovieDBServiceManager.class.getSimpleName();

    public static Retrofit mRetrofit = null;
    public static TheMoviesDBApi mApi = null;
    public static TheMovieDBServiceManager mServiceManager = null;
    public static final String mApiKey = BuildConfig.THE_MOVIE_DB_API_KEY;


    private TheMovieDBServiceManager() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(C.THE_MOVIES_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApi = mRetrofit.create(TheMoviesDBApi.class);
    }

    public static TheMovieDBServiceManager getInstance() {
        if (mServiceManager == null) {
            mServiceManager = new TheMovieDBServiceManager();
        }
        return mServiceManager;
    }

    /*
        Movies
     */
    public void getMoviesData(String filter, int pageNum, Callback<MoviesData> callback) {
        Call<MoviesData> moviesDataCall = mApi.getMoviesData(filter, pageNum, mApiKey);
        moviesDataCall.enqueue(callback);
    }

    /*
        Videos
     */
    public void getMoviesVideos(int movieId, Callback<JsonObject> callback) {
        Call<JsonObject> videoKeyCall = mApi.getMovieVideoKey(movieId, mApiKey);
        videoKeyCall.enqueue(callback);
    }

    /*
        Reviews
     */
    public void getReviewsData(int movieId, int pageNum, Callback<ReviewsData> callback) {
        Call<ReviewsData> reviewsDataCall = mApi.getReviewsData(movieId, pageNum, mApiKey);
        reviewsDataCall.enqueue(callback);
    }
}
