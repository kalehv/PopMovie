package me.kalehv.popmovie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.kalehv.popmovie.models.Movie;
import me.kalehv.popmovie.models.MoviesData;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    @Bind(R.id.gridview_thumbnails) GridView mGridView;

    private ArrayList<Movie> movies;

    private ThumbnailsAdapter mThumbnailsAdapter;
    private TheMovieDBServiceManager mTheMovieDBServiceManager;

    public MainFragment() {
        mTheMovieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.movies = new ArrayList<>();
        fetchMoviesData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        this.movies = new ArrayList<>();
        fetchMoviesData();

        return rootView;
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

        mTheMovieDBServiceManager.getMoviesData(filter, 1, new Callback<MoviesData>() {
            @Override
            public void onResponse(Call<MoviesData> moviesDataCall, Response<MoviesData> response) {
                if (response.isSuccessful()) {
                    movies.addAll(response.body().getMovies());
                    setGridViewAdapter();
                }
            }

            @Override
            public void onFailure(Call<MoviesData> moviesDataCall, Throwable t) {

            }
        });
    }
}
