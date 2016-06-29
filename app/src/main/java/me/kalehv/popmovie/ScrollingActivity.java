package me.kalehv.popmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.adapters.ReviewsAdapter;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.models.Review;
import me.kalehv.popmovie.models.ReviewsData;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrollingActivity extends AppCompatActivity {

    private final String TAG = ScrollingActivity.class.getSimpleName();

    @BindView(R.id.app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.header) ImageView imageViewHeader;
    @BindView(R.id.header_button_trailer) ImageButton imageButtonHeader;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nested_scrollview) NestedScrollView nestedScrollView;
    @BindView(R.id.cardview_movie_details) FrameLayout cardViewMovieDetails;
    @BindView(R.id.imageview_movie_detail_poster) ImageView imageViewMovieDetailPoster;
    @BindView(R.id.textview_movie_overview) TextView textViewOverview;
    @BindView(R.id.textview_movie_title) TextView textViewMovieTitle;
    @BindView(R.id.textview_movie_release_adult) TextView textViewMovieReleaseAdult;
    @BindView(R.id.ratingbar_movie_rating) RatingBar ratingBarMovieRating;
    @BindView(R.id.recyclerview_movie_review) RecyclerView recyclerViewMovieReviews;

    private Intent incomingIntent;
    private int actionBarHeight;
    private TheMovieDBServiceManager movieDBServiceManager;
    private ArrayList<Review> reviews;

    public ScrollingActivity() {
        movieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        incomingIntent = getIntent();

        setupActionBar();
        setupView();
        fetchVideoKey();
        fetchReviews();
    }

    private void setAdapter() {
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this, reviews);
        recyclerViewMovieReviews.setAdapter(reviewsAdapter);

        recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMovieReviews.setNestedScrollingEnabled(false);
    }

    private void fetchReviews() {
        reviews = new ArrayList<>();
        if (movieDBServiceManager != null) {
            int movieId = incomingIntent.getIntExtra(C.EXTRAS_MOVIE_ID, -1);
            if (movieId != -1) {
                movieDBServiceManager.getReviewsData(movieId, 1, new Callback<ReviewsData>() {
                    @Override
                    public void onResponse(Call<ReviewsData> moviesDataCall, Response<ReviewsData> response) {
                        if (response.isSuccessful()) {
                            reviews.addAll(response.body().getReviews());
                            setAdapter();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewsData> reviewsDataCall, Throwable t) {}
                });
            }
        }
    }

    private void fetchVideoKey() {
        if (movieDBServiceManager != null) {
            int movieId = incomingIntent.getIntExtra(C.EXTRAS_MOVIE_ID, -1);
            if (movieId != -1) {
                movieDBServiceManager.getMoviesVideos(movieId, new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call <JsonObject> call, Response<JsonObject> response) {
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

    private void assignTrailerUri(final String videoKey, final View view ) {
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

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        setTitle(""); // Do not display any title by default

        // Set expanded appbar height to aspect ratio of 3:2
        // Ref - http://stackoverflow.com/a/31362835/906577
        float heightDp = getResources().getDisplayMetrics().heightPixels;
        float widthDp = getResources().getDisplayMetrics().widthPixels;
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.height = (int) ((widthDp * 2) / 3);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                // Only display title when toolbar is displayed with size greater than equal to action bar
                if (scrollRange + verticalOffset <= actionBarHeight && scrollRange + verticalOffset >= 0) {
                    collapsingToolbarLayout.setTitle(incomingIntent.getStringExtra(C.EXTRAS_TITLE));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        // Landscape
        if (widthDp >= heightDp) {
            FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardViewMovieDetails.getLayoutParams();
            int topMargin = (int) (heightDp / 3);
            cardViewLayoutParams.width = (int) (widthDp - getResources().getDimension(R.dimen.margin_detail_side));
            cardViewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            CoordinatorLayout.LayoutParams appBarLayoutNestedScrollViewParams =
                    (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior appBarLayoutNestedScrollViewParamsBehavior =
                    (AppBarLayout.ScrollingViewBehavior) appBarLayoutNestedScrollViewParams.getBehavior();
            appBarLayoutNestedScrollViewParamsBehavior.setOverlayTop((int) heightDp - topMargin);
        }
    }

    private void setupView() {
        String backdropPath = incomingIntent.getStringExtra(C.EXTRAS_BACKDROP_PATH);
        String posterPath = incomingIntent.getStringExtra(C.EXTRAS_POSTER_PATH);

        Picasso.with(this)
                .load(C.POSTER_IMAGE_BASE_URL + posterPath)
                .into(imageViewMovieDetailPoster);

        Picasso.with(this)
                .load(C.BACKDROP_IMAGE_BASE_URL + backdropPath)
                .into(imageViewHeader);

        textViewMovieTitle.setText(incomingIntent.getStringExtra(C.EXTRAS_TITLE));
        textViewOverview.setText(incomingIntent.getStringExtra(C.EXTRAS_OVERVIEW));

        String releaseDate = incomingIntent.getStringExtra(C.EXTRAS_RELEASE_DATE_STRING);
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        fromFormat.setLenient(false);
        DateFormat toFormat = DateFormat.getDateInstance();
        toFormat.setLenient(false);
        try {
            Date date = fromFormat.parse(releaseDate);
            releaseDate = toFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "setupView: Date parse exception", e);
        }

        String releaseAdult = releaseDate;
        if (incomingIntent.getBooleanExtra(C.EXTRAS_IS_ADULT, false)) {
            releaseAdult += "   " + getResources().getString(R.string.indicator_adult_movie);
        } else {
            releaseAdult += "   " + getResources().getString(R.string.indicator_universal_movie);
        }

        ratingBarMovieRating.setRating((float) incomingIntent.getDoubleExtra(C.EXTRAS_VOTE_AVERAGE, 0) / 2.0f);

        textViewMovieReleaseAdult.setText(releaseAdult);
    }
}
