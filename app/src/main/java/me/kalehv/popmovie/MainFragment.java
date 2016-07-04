package me.kalehv.popmovie;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.adapters.ThumbnailsAdapter;
import me.kalehv.popmovie.models.Movie;
import me.kalehv.popmovie.models.MoviesData;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;
import me.kalehv.popmovie.sync.MovieSyncAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment
        extends Fragment
        implements GridView.OnItemClickListener {
    @BindView(R.id.gridview_thumbnails) GridView gridView;

    private ArrayList<Movie> movies;

    private TheMovieDBServiceManager movieDBServiceManager;
    private String filteredBy;

    public interface OnMovieItemClickListener {
        void onMovieItemClick(Movie movie);
    }

    public MainFragment() {
        movieDBServiceManager = TheMovieDBServiceManager.getInstance();
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
        gridView.setOnItemClickListener(this);

        fetchMoviesData();

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Movie movie = (Movie) adapterView.getItemAtPosition(i);
        OnMovieItemClickListener listenerActivity = (OnMovieItemClickListener) getActivity();

        if (movie != null && listenerActivity != null) {
            listenerActivity.onMovieItemClick(movie);
        }
    }

    private void setAdapter() {
        ThumbnailsAdapter thumbnailsAdapter = new ThumbnailsAdapter(getActivity(), R.layout.item_grid_movies, movies);
        this.gridView.setAdapter(thumbnailsAdapter);
    }

    private void fetchMoviesData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String filter = preferences.getString(
                getString(R.string.pref_filter_key),
                getString(R.string.pref_filter_top_rated)
        );

        // Only update data if previous filter is not the same as one in Shared Preferences
        if (filteredBy == null || !filteredBy.equals(filter)) {
            // Update filter and fetch data
            this.movies = new ArrayList<>();
            filteredBy = filter;

            movieDBServiceManager.getMoviesData(filter, 1, new Callback<MoviesData>() {
                @Override
                public void onResponse(Call<MoviesData> moviesDataCall, Response<MoviesData> response) {
                    if (response.isSuccessful()) {
                        movies.addAll(response.body().getMovies());
                        setAdapter();
                        MovieSyncAdapter.syncImmediately(getActivity());
                    }
                }

                @Override
                public void onFailure(Call<MoviesData> moviesDataCall, Throwable t) {}
            });
        }
    }
}
