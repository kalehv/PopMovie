package me.kalehv.popmovie.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hk022893 on 7/3/16.
 */

public class TrailersData {
    @SerializedName("id")
    private Integer movieId;

    @SerializedName("results")
    private List<Trailer> trailers = new ArrayList<>();

    /**
     *
     * @return
     * The movieId
     */
    public Integer getMovieId() {
        return movieId;
    }

    /**
     *
     * @param movieId
     * The movieId
     */
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    /**
     *
     * @return
     * The results
     */
    public List<Trailer> getTrailers() {
        return trailers;
    }

    /**
     *
     * @param trailers
     * The results
     */
    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
