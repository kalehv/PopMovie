package me.kalehv.popmovie.services;

import com.google.gson.JsonObject;

import me.kalehv.popmovie.models.MoviesData;
import me.kalehv.popmovie.models.ReviewsData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by harshadkale on 4/10/16.
 */
interface TheMoviesDBApi {

    /*
        Movies
     */
    // http://api.themoviedb.org/3/movie/top_rated?api_key=<api_key>
    @GET("3/movie/{filter}")
    Call<MoviesData> getMoviesData(@Path("filter") String filter, @Query("page") int page, @Query("api_key") String apiKey);

    /*
        Videos
     */
    // http://api.themoviedb.org/3/movie/244786/videos?api_key=<api_key>
    @GET("3/movie/{movieId}/videos")
    Call<JsonObject> getMovieVideoKey(@Path("movieId") int movieId, @Query("api_key") String apiKey);

    /*
        Reviews
     */
    // http://api.themoviedb.org/3/movie/244786/reviews?api_key=<api_key>
    @GET("3/movie/{movieId}/reviews")
    Call<ReviewsData> getReviewsData(@Path("movieId") int movieId, @Query("page") int page, @Query("api_key") String apiKey);

}
