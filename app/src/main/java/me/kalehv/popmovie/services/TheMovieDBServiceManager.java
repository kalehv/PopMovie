package me.kalehv.popmovie.services;

import me.kalehv.popmovie.BuildConfig;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.MoviesData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by harshadkale on 4/10/16.
 */
public class TheMovieDBServiceManager {

    private final String TAG = TheMovieDBServiceManager.class.getSimpleName();

    public static Retrofit retrofit = null;
    public static TheMoviesDBApi api = null;
    public static TheMovieDBServiceManager serviceManager = null;


    private TheMovieDBServiceManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(C.THE_MOVIES_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(TheMoviesDBApi.class);
    }

    public static TheMovieDBServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new TheMovieDBServiceManager();
        }
        return serviceManager;
    }

    public void getMoviesData(String filter, int pageNum, Callback<MoviesData> callback) {
        String apiKey = BuildConfig.THE_MOVIE_DB_API_KEY;
        Call<MoviesData> moviesDataCall = api.getMoviesData(filter, pageNum, apiKey);
        moviesDataCall.enqueue(callback);
    }
}
