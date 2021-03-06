package me.kalehv.popmovie;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
        implements GridView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //region ButterKnife Declarations
    @BindView(R.id.gridview_thumbnails) GridView gridView;
    //endregion

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
        thumbnailsAdapter = new ThumbnailsAdapter(getActivity(), null, 0);

        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        gridView.setOnItemClickListener(this);

        filterBy = Utility.getMoviesFilter(getActivity(), R.string.pref_filter_popular);

        gridView.setAdapter(thumbnailsAdapter);
        gridView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
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
