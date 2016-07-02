package me.kalehv.popmovie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import org.parceler.Parcels;

import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Movie;

public class MainActivity
        extends ToolbarAppCompatActivity
        implements MainFragment.OnMovieItemClickListener    {

    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";
    private static final int DETAIL_RESULT_REQUEST_CODE = 1001;

    private Movie selectedMovie;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Running in split master-detail mode (Landscape tablet)
        if (getResources().getBoolean(R.bool.has_two_panes)) {
            if (selectedMovie != null) {
                loadDetailFragment(Parcels.wrap(selectedMovie));
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
        Parcelable movieParcel = Parcels.wrap(selectedMovie);
        outState.putParcelable(C.MOVIE_PARCEL, movieParcel);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Parcelable movieParcel = savedInstanceState.getParcelable(C.MOVIE_PARCEL);
        selectedMovie = Parcels.unwrap(movieParcel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DETAIL_RESULT_REQUEST_CODE) {
            Parcelable movieParcel = data.getParcelableExtra(C.MOVIE_PARCEL);
            if (movieParcel != null) {
                selectedMovie = Parcels.unwrap(movieParcel);
                if (getResources().getBoolean(R.bool.has_two_panes)) {
                    loadDetailFragment(movieParcel);
                }
            }
        }
    }

    @Override
    public void onMovieItemClick(Movie movie) {
        selectedMovie = movie;
        Parcelable movieParcel = Parcels.wrap(movie);

        if (movie != null && movieParcel != null) {
            if (!getResources().getBoolean(R.bool.has_two_panes)) {
                loadDetailActivity(movieParcel);
            } else {
                loadDetailFragment(movieParcel);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadDetailFragment(Parcelable movieParcel) {
        // Add fragment only if there is any selected movie
        if (movieParcel != null) {
            Bundle args = new Bundle();
            args.putParcelable(C.MOVIE_PARCEL, movieParcel);

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

    private void loadDetailActivity(Parcelable movieParcel) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(C.MOVIE_PARCEL, movieParcel);

        startActivityForResult(detailIntent, DETAIL_RESULT_REQUEST_CODE);
    }
}
