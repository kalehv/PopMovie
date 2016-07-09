package me.kalehv.popmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Movie;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends ToolbarAppCompatActivity {

    private final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.app_bar_detail) AppBarLayout appBarDetailLayout;
    @BindView(R.id.header) ImageView imageViewHeader;
    @BindView(R.id.header_button_trailer) ImageButton imageButtonHeader;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nested_scrollview) NestedScrollView nestedScrollView;
    @BindView(R.id.fragment_detail_container) FrameLayout detailFragmentContainer;

    private int actionBarHeight;
    private Intent incomingIntent;
    private TheMovieDBServiceManager movieDBServiceManager;
    private Movie selectedMovie;

    public DetailActivity() {
        movieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.has_two_panes)) {
            if (savedInstanceState != null) {
                Parcelable movieParcel = savedInstanceState.getParcelable(C.MOVIE_PARCEL);
                selectedMovie = Parcels.unwrap(movieParcel);
                setResultData();
            }

            finish();
            return;
        }

        ButterKnife.bind(this);

        incomingIntent = getIntent();
        if (incomingIntent.getExtras() != null) {
            Parcelable movieParcel = incomingIntent.getParcelableExtra(C.MOVIE_PARCEL);
            selectedMovie = Parcels.unwrap(movieParcel);

            Bundle args = new Bundle();
            args.putParcelable(C.MOVIE_PARCEL, movieParcel);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment)
                    .commit();
        }

        fetchVideoKey();

        setupHeaderView();
        setupActionBar();
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


    }

    private void setResultData() {
        Intent resultIntent = new Intent();
        Parcelable movieParcel = Parcels.wrap(selectedMovie);
        resultIntent.putExtra(C.MOVIE_PARCEL, movieParcel);
        setResult(RESULT_OK, resultIntent);
    }

    private void setupHeaderView() {
        String backdropPath = selectedMovie.getBackdropPath();
        Picasso.with(this)
                .load(backdropPath)
                .into(imageViewHeader);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(""); // Do not display any title by default
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }


        // Set expanded appbar height to aspect ratio of 3:2
        // Ref - http://stackoverflow.com/a/31362835/906577
        float heightDp = getResources().getDisplayMetrics().heightPixels;
        float widthDp = getResources().getDisplayMetrics().widthPixels;
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarDetailLayout.getLayoutParams();
        appBarLayoutParams.height = (int) ((widthDp * 2) / 3);

        appBarDetailLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                // Only display title when toolbar is displayed with size greater than equal to action bar
                if (scrollRange + verticalOffset <= actionBarHeight && scrollRange + verticalOffset >= 0) {
                    collapsingToolbarLayout.setTitle(selectedMovie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        // Landscape
        if (widthDp >= heightDp) {
            FrameLayout.LayoutParams movieDetailsFragmentLayoutParams = (FrameLayout.LayoutParams) detailFragmentContainer.getLayoutParams();
            int topMargin = (int) (heightDp / 3);
            movieDetailsFragmentLayoutParams.width = (int) (widthDp - getResources().getDimension(R.dimen.margin_detail_side));
            movieDetailsFragmentLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            CoordinatorLayout.LayoutParams appBarLayoutNestedScrollViewParams =
                    (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior appBarLayoutNestedScrollViewParamsBehavior =
                    (AppBarLayout.ScrollingViewBehavior) appBarLayoutNestedScrollViewParams.getBehavior();
            appBarLayoutNestedScrollViewParamsBehavior.setOverlayTop((int) heightDp - topMargin);
        }
    }

    private void fetchVideoKey() {
        if (movieDBServiceManager != null) {
            int movieId = selectedMovie.getMovieId();
            if (movieId != -1) {
                movieDBServiceManager.getMoviesVideos(movieId, new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        onReceiveVideoSuccessfulResponse(response);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.i(TAG, "onFailure: Error fetching trailer video link");
                    }
                });
            }
        }
    }

    private void onReceiveVideoSuccessfulResponse(Response<JsonObject> response) {
        JsonArray results = response.body().get("results").getAsJsonArray();
        if (results != null) {
            JsonObject first = results.get(0).getAsJsonObject();
            if (first != null) {
                final String videoKey = first.get(C.VIDEOS_YOUTUBE_KEY_NAME).getAsString();
                assignTrailerUri(videoKey, imageButtonHeader);
            }
        }
    }

    private void assignTrailerUri(final String videoKey, final View view) {
        if (videoKey != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri uri = Uri.parse(C.YOUTUBE_BASE_URL)
                                    .buildUpon()
                                    .appendQueryParameter(C.YOUTUBE_QUERY_PARAM, videoKey)
                                    .build();

                            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, uri);
                            youtubeIntent.putExtra(C.YOUTUBE_FORCE_FULLSCREEN, true);
                            startActivity(youtubeIntent);
                        }
                    });
                }
            });
        }
    }
}