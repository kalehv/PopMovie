package me.kalehv.popmovie.services;

import com.google.gson.JsonObject;

import me.kalehv.popmovie.models.MoviesData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by harshadkale on 4/10/16.
 */
public interface TheMoviesDBApi {

    // http://api.themoviedb.org/3/movie/top_rated?api_key=e19c269eefc0509a3cb2e153c839d590
    @GET("3/movie/{filter}")
    Call<MoviesData> getMoviesData(@Path("filter") String filter, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("3/movie/{movieId}/videos")
    Call<JsonObject> getMovieVideoKey(@Path("movieId") int movieId, @Query("api_key") String apiKey);
}
