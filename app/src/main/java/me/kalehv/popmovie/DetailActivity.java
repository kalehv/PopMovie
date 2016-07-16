package me.kalehv.popmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kalehv.popmovie.data.MovieContract;
import me.kalehv.popmovie.data.MovieProvider;
import me.kalehv.popmovie.global.C;
import me.kalehv.popmovie.services.TheMovieDBServiceManager;

public class DetailActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = DetailActivity.class.getSimpleName();

    private static final int MOVIES_LOADER = 0;

    //region ButterKnife declarations
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.header)
    ImageView imageViewHeader;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nested_scrollview)
    NestedScrollView nestedScrollView;

    @BindView(R.id.cardview_movie_details)
    FrameLayout cardViewMovieDetails;

    @BindView(R.id.imageview_movie_detail_poster)
    ImageView imageViewMovieDetailPoster;

    @BindView(R.id.textview_movie_overview)
    TextView textViewOverview;

    @BindView(R.id.textview_movie_title)
    TextView textViewMovieTitle;

    @BindView(R.id.textview_movie_release_adult)
    TextView textViewMovieReleaseAdult;

    @BindView(R.id.ratingbar_movie_rating)
    RatingBar ratingBarMovieRating;

    @BindView(R.id.recyclerview_movie_review)
    RecyclerView recyclerViewMovieReviews;

    @BindView(R.id.fab_favorite_movie)
    FloatingActionButton fabFavoriteMovie;
    //endregion

    private int actionBarHeight;
    private Intent incomingIntent;
    private TheMovieDBServiceManager movieDBServiceManager;
    private Uri selectedMovieUri;
    private boolean isFavorite;
    private int movieKey;

    private View.OnClickListener onFavoriteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toggleMovieFavorite();
        }
    };

    public DetailActivity() {
        movieDBServiceManager = TheMovieDBServiceManager.getInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        fabFavoriteMovie.setOnClickListener(onFavoriteClickListener);

        if (getResources().getBoolean(R.bool.has_two_panes)) {
            if (savedInstanceState != null) {
                selectedMovieUri = savedInstanceState.getParcelable(C.MOVIE_PARCEL);
                setResultData();
            }

            finish();
            return;
        }

        incomingIntent = getIntent();
        if (incomingIntent.getExtras() != null) {
            selectedMovieUri = incomingIntent.getParcelableExtra(C.MOVIE_PARCEL);
        }

        getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (selectedMovieUri != null) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    this,
                    selectedMovieUri,
                    C.SELECT_ALL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            setupView(data);
            isFavorite = data.getInt(MovieContract.MovieEntry.COL_INDEX_FAVORITE) != 0;
            movieKey = data.getInt(MovieContract.MovieEntry.COL_INDEX_MOVIE_KEY);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void setResultData() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(C.MOVIE_PARCEL, selectedMovieUri);
        setResult(RESULT_OK, resultIntent);
    }

    private void setupHeaderView(Cursor data) {
        if (data != null && data.moveToFirst()) {
            String backdropPath = data.getString(MovieContract.MovieEntry.COL_INDEX_BACKDROP_PATH);
            Picasso.with(this)
                    .load(backdropPath)
                    .into(imageViewHeader);
        }
    }

    private void setupActionBar(final Cursor data) {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null); // Do not display any title by default
            actionBar.setDisplayShowTitleEnabled(false);
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

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
                    collapsingToolbarLayout.setTitle(data.getString(MovieContract.MovieEntry.COL_INDEX_TITLE));
                    isShow = true;
                } else if (isShow) {
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

    private void setupView(Cursor data) {
        if (!getResources().getBoolean(R.bool.has_two_panes)) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.width_poster_image),
                    (int) getResources().getDimension(R.dimen.height_poster_image)
            );
            float standardMargin = getResources().getDimension(R.dimen.margin_standard);
            float topMargin = getResources().getDimension(R.dimen.negative_poster_top_margin);
            layoutParams.setMargins((int) standardMargin, (int) topMargin, (int) standardMargin, 0);
            imageViewMovieDetailPoster.setLayoutParams(layoutParams);
        }

        String posterPath = data.getString(MovieContract.MovieEntry.COL_INDEX_POSTER_PATH);
        Picasso.with(this)
                .load(posterPath)
                .into(imageViewMovieDetailPoster);

        textViewMovieTitle.setText(data.getString(MovieContract.MovieEntry.COL_INDEX_TITLE));
        textViewOverview.setText(data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW)+data.getString(MovieContract.MovieEntry.COL_INDEX_OVERVIEW));

        String releaseDate = data.getString(MovieContract.MovieEntry.COL_INDEX_RELEASE_DATE);
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
        if (data.getInt(MovieContract.MovieEntry.COL_INDEX_ADULT) == 1) {
            releaseAdult += "   " + getResources().getString(R.string.indicator_adult_movie);
        } else {
            releaseAdult += "   " + getResources().getString(R.string.indicator_universal_movie);
        }

        ratingBarMovieRating.setRating((float) (data.getDouble(MovieContract.MovieEntry.COL_INDEX_VOTE_AVERAGE) / 2.0f));

        textViewMovieReleaseAdult.setText(releaseAdult);

        setupHeaderView(data);
        setupActionBar(data);
    }

    private void toggleMovieFavorite() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, isFavorite ? 0 : 1);

        Snackbar.make(
                findViewById(android.R.id.content),
                isFavorite ? R.string.snack_movie_unfavorited : R.string.snack_movie_favorited,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_undo, onFavoriteClickListener).show();

        getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                contentValues,
                MovieProvider.movieByKeySelection,
                new String[]{String.valueOf(movieKey)}
        );

        isFavorite = !isFavorite;
    }
}
