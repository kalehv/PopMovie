package me.kalehv.popmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import me.kalehv.popmovie.global.C;

public class MainActivity
        extends ToolbarAppCompatActivity
        implements MainFragment.OnMovieItemClickListener    {

    private static final String SELECTED_TAB_POSITION = "SELECTED_TAB_POSITION";
    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";
    private static final int DETAIL_RESULT_REQUEST_CODE = 1001;

    private Uri selectedMovieUri;

    @BindView(R.id.viewpager_filter_tabs) ViewPager viewPager;
    @BindView(R.id.tabs_movies_filter) TabLayout tabLayout;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FilterFragmentPageAdapter pageAdapter = new FilterFragmentPageAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(pageAdapter.getIcon(i));
            }
        }

        // Running in split master-detail mode (Landscape tablet)
        if (getResources().getBoolean(R.bool.has_two_panes)) {
            if (selectedMovieUri!= null) {
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
        outState.putInt(SELECTED_TAB_POSITION, tabLayout.getSelectedTabPosition());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(SELECTED_TAB_POSITION, 0));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
