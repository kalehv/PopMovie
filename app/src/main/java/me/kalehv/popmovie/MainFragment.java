package me.kalehv.popmovie;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.adapters.ThumbnailsAdapter;
import me.kalehv.popmovie.data.MovieContract;
import me.kalehv.popmovie.data.MovieProvider;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.utils.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment
        extends Fragment
        implements ThumbnailsAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.recyclerview_movie_thumbnails)
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    private String filterBy;
    private ThumbnailsAdapter thumbnailsAdapter;

    private static final int MOVIES_LOADER = 0;

    public interface OnMovieItemClickListener {
        void onMovieItemClick(Uri uri);
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

        filterBy = Utility.getMoviesFilter(getActivity(), R.string.pref_filter_popular);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        thumbnailsAdapter = new ThumbnailsAdapter(getActivity(), null);
        thumbnailsAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(thumbnailsAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(View view, Cursor cursor) {
        if (cursor != null) {
            OnMovieItemClickListener listenerActivity = (OnMovieItemClickListener) getActivity();
            if (listenerActivity != null) {
                long movieKey = cursor.getLong(MovieContract.MovieEntry.COL_INDEX_MOVIE_KEY);
                listenerActivity.onMovieItemClick(MovieContract.MovieEntry.buildMovieUri(movieKey));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = null;
        String selection;

        if (filterBy.equals(getString(R.string.pref_filter_top_rated))) {
            selection = MovieProvider.topRatedMoviesSelection;
            sortOrder = MovieProvider.topRatedMoviesSortOrder;
        } else if (filterBy.equals(getString(R.string.pref_filter_favorite))) {
            selection = MovieProvider.favoriteMoviesSelection;
        } else {
            selection = MovieProvider.popularMoviesSelection;
            sortOrder = MovieProvider.popularMoviesSortOrder;
        }

        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                C.SELECT_ALL_COLUMNS,
                selection,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        thumbnailsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        thumbnailsAdapter.swapCursor(null);
    }
}
