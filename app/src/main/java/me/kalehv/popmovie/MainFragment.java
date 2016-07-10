package me.kalehv.popmovie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import me.kalehv.popmovie.services.TheMovieDBServiceManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment
        extends Fragment
        implements GridView.OnItemClickListener {
    @BindView(R.id.gridview_thumbnails) GridView gridView;

    private static final String FILTER_BY = "filter";

    private ArrayList<Movie> movies;
    private TheMovieDBServiceManager movieDBServiceManager;
    private String filteredBy;

    public interface OnMovieItemClickListener {
        void onMovieItemClick(Movie movie);
    }

    public MainFragment() {
        this.movieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    public static MainFragment newInstance(String filter) {
        Bundle args = new Bundle();
        args.putString(FILTER_BY, filter);
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(args);
        return mainFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        gridView.setOnItemClickListener(this);

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
}
