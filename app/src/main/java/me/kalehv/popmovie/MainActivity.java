package me.kalehv.popmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.utils.Utility;

public class MainActivity
        extends AppCompatActivity
        implements MainFragment.OnMovieItemClickListener {

    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";
    private static final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";
    private static final int DETAIL_RESULT_REQUEST_CODE = 1001;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Uri selectedMovieUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Bundle args = new Bundle();
        args.putParcelable(C.MOVIE_PARCEL, selectedMovieUri);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        loadMainFragment();

        // Running in split master-detail mode (Landscape tablet)
        if (getResources().getBoolean(R.bool.has_two_panes)) {
            if (selectedMovieUri != null) {
                loadDetailFragment(selectedMovieUri);
            }
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setElevation(0f);
            }

            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (detailFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(C.MOVIE_PARCEL, selectedMovieUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedMovieUri = savedInstanceState.getParcelable(C.MOVIE_PARCEL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DETAIL_RESULT_REQUEST_CODE) {
            selectedMovieUri = data.getParcelableExtra(C.MOVIE_PARCEL);
            if (selectedMovieUri != null) {
                if (getResources().getBoolean(R.bool.has_two_panes)) {
                    loadDetailFragment(selectedMovieUri);
                }
            }
        }
    }

    @Override
    public void onMovieItemClick(Uri uri) {
        selectedMovieUri = uri;

        if (selectedMovieUri != null) {
            if (!getResources().getBoolean(R.bool.has_two_panes)) {
                loadDetailActivity(selectedMovieUri);
            } else {
                loadDetailFragment(selectedMovieUri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String filterPref = Utility.getMoviesFilter(this, R.string.pref_filter_popular);
        int menuId = 0;
        if (filterPref.equals(getString(R.string.pref_filter_popular))) {
            menuId = R.id.pref_filter_popularity;
        } else if (filterPref.equals(getString(R.string.pref_filter_top_rated))) {
            menuId = R.id.pref_filter_top_rated;
        } else if (filterPref.equals(getString(R.string.pref_filter_favorite))) {
            menuId = R.id.pref_filter_favorite;
        }
        MenuItem item = menu.findItem(menuId);
        if (item != null) {
            item.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean ignoreSelection = false;
        String filter = getString(R.string.pref_filter_popular);
        switch (id) {
            case R.id.pref_filter_favorite:
                filter = getString(R.string.pref_filter_favorite);
                break;
            case R.id.pref_filter_top_rated:
                filter = getString(R.string.pref_filter_top_rated);
                break;
            case R.id.pref_filter_popularity:
                filter = getString(R.string.pref_filter_popular);
                break;
            default:
                ignoreSelection = true;
        }
        if (!ignoreSelection) {
            Utility.setMoviesFilter(this, filter);
            loadMainFragment();
            item.setChecked(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMainFragment() {
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main_container, mainFragment, MAIN_FRAGMENT_TAG)
                .commit();
    }

    private void loadDetailFragment(Uri movieUri) {
        // Add fragment only if there is any selected movie
        if (movieUri != null) {
            Bundle args = new Bundle();
            args.putParcelable(C.MOVIE_PARCEL, movieUri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            // Remove existing Detail Fragment if there is no selected movie
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (detailFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
            }
        }
    }

    private void loadDetailActivity(Uri movieUri) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(C.MOVIE_PARCEL, movieUri);

        startActivityForResult(detailIntent, DETAIL_RESULT_REQUEST_CODE);
    }
}
