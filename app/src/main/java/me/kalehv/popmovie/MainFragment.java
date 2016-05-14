package me.kalehv.popmovie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Movie;
import me.kalehv.popmovie.models.MoviesData;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment
        extends Fragment
        implements GridView.OnItemClickListener {
    @Bind(R.id.gridview_thumbnails) GridView mGridView;

    private ArrayList<Movie> movies;

    private ThumbnailsAdapter mThumbnailsAdapter;
    private TheMovieDBServiceManager mTheMovieDBServiceManager;
    private String mFilteredBy;

    public MainFragment() {
        mTheMovieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMoviesData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mGridView.setOnItemClickListener(this);

        fetchMoviesData();

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Movie movie = (Movie) adapterView.getItemAtPosition(i);
        Intent detailIntent = new Intent(getActivity(), ScrollingActivity.class);

        if (movie != null) {
            detailIntent.putExtra(C.EXTRAS_MOVIE_ID, movie.getId());
            detailIntent.putExtra(C.EXTRAS_POSTER_PATH, movie.getPosterPath());
            detailIntent.putExtra(C.EXTRAS_BACKDROP_PATH, movie.getBackdropPath());
            detailIntent.putExtra(C.EXTRAS_TITLE, movie.getTitle());
            detailIntent.putExtra(C.EXTRAS_OVERVIEW, movie.getOverview());
            detailIntent.putExtra(C.EXTRAS_RELEASE_DATE_STRING, movie.getReleaseDate());
            detailIntent.putExtra(C.EXTRAS_LANGUAGE, movie.getOriginalLanguage());
            detailIntent.putExtra(C.EXTRAS_POPULARITY, movie.getPopularity());
            detailIntent.putExtra(C.EXTRAS_VOTE_COUNT, movie.getVoteCount());
            detailIntent.putExtra(C.EXTRAS_VOTE_AVERAGE, movie.getVoteAverage());
            detailIntent.putExtra(C.EXTRAS_HAS_VIDEO, movie.getVideo());
            detailIntent.putExtra(C.EXTRAS_IS_ADULT, movie.getAdult());
        }

        startActivity(detailIntent);
    }

    private void setGridViewAdapter() {
        mThumbnailsAdapter = new ThumbnailsAdapter(getActivity(), R.layout.item_grid_movies, movies);
        this.mGridView.setAdapter(mThumbnailsAdapter);
    }

    private void fetchMoviesData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String filter = preferences.getString(
                getString(R.string.pref_filter_key),
                getString(R.string.pref_filter_top_rated)
        );

        // Only update data if previous filter is not the same as one in Shared Preferences
        if (mFilteredBy == null || !mFilteredBy.equals(filter)) {
            // Update filter and fetch data
            this.movies = new ArrayList<>();
            mFilteredBy = filter;

            mTheMovieDBServiceManager.getMoviesData(filter, 1, new Callback<MoviesData>() {
                @Override
                public void onResponse(Call<MoviesData> moviesDataCall, Response<MoviesData> response) {
                    if (response.isSuccessful()) {
                        movies.addAll(response.body().getMovies());
                        setGridViewAdapter();
                    }
                }

                @Override
                public void onFailure(Call<MoviesData> moviesDataCall, Throwable t) {}
            });
        }
    }
}
