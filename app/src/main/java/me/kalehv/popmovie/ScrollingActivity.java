package me.kalehv.popmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.kalehv.popmovie.global.C;

public class ScrollingActivity extends AppCompatActivity {

    private final String TAG = ScrollingActivity.class.getSimpleName();

    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.header) ImageView mImageViewHeader;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.nested_scrollview) NestedScrollView mNestedScrollView;
    @Bind(R.id.cardview_movie_details) FrameLayout mCardViewMovieDetails;
    @Bind(R.id.imageview_movie_detail_poster) ImageView mImageViewMovieDetailPoster;
    @Bind(R.id.textview_movie_overview) TextView mTextViewOverview;
    @Bind(R.id.textview_movie_title) TextView mTextViewMovieTitle;
    @Bind(R.id.textview_movie_release_adult) TextView mTextViewMovieReleaseAdult;
    @Bind(R.id.ratingbar_movie_rating) RatingBar mRatingBarMovieRating;

    private Intent mIncomingIntent;
    private int mActionBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        mIncomingIntent = getIntent();

        setupActionBar();
        setupView();
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        setTitle(""); // Do not display any title by default

        // Set expanded appbar height to aspect ratio of 3:2
        // Ref - http://stackoverflow.com/a/31362835/906577
        float heightDp = getResources().getDisplayMetrics().heightPixels;
        float widthDp = getResources().getDisplayMetrics().widthPixels;
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        appBarLayoutParams.height = (int) ((widthDp * 2) / 3);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                // Only display title when toolbar is displayed with size greater than equal to action bar
                if (scrollRange + verticalOffset <= mActionBarHeight && scrollRange + verticalOffset >= 0) {
                    mCollapsingToolbarLayout.setTitle(mIncomingIntent.getStringExtra(C.EXTRAS_TITLE));
                    isShow = true;
                } else if(isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        if (widthDp >= heightDp) {
            // landscape
            FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) mCardViewMovieDetails.getLayoutParams();
            int topMargin = (int) (heightDp / 3);
            cardViewLayoutParams.setMargins(128, 0, 128, 0);

            CoordinatorLayout.LayoutParams appBarLayoutNestedScrollViewParams =
                    (CoordinatorLayout.LayoutParams) mNestedScrollView.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior appBarLayoutNestedScrollViewParamsBehavior =
                    (AppBarLayout.ScrollingViewBehavior) appBarLayoutNestedScrollViewParams.getBehavior();
            appBarLayoutNestedScrollViewParamsBehavior.setOverlayTop((int) heightDp - topMargin);
        }
    }

    private void setupView() {
        String backdropPath = mIncomingIntent.getStringExtra(C.EXTRAS_BACKDROP_PATH);
        String posterPath = mIncomingIntent.getStringExtra(C.EXTRAS_POSTER_PATH);

        Picasso.with(this)
                .load(C.POSTER_IMAGE_BASE_URL + posterPath)
                .into(mImageViewMovieDetailPoster);

        Picasso.with(this)
                .load(C.BACKDROP_IMAGE_BASE_URL + backdropPath)
                .into(mImageViewHeader);

        mTextViewMovieTitle.setText(mIncomingIntent.getStringExtra(C.EXTRAS_TITLE));
        mTextViewOverview.setText(mIncomingIntent.getStringExtra(C.EXTRAS_OVERVIEW));

        String releaseDate = mIncomingIntent.getStringExtra(C.EXTRAS_RELEASE_DATE_STRING);
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        if (mIncomingIntent.getBooleanExtra(C.EXTRAS_IS_ADULT, false)) {
            releaseAdult += "   " + getResources().getString(R.string.indicator_adult_movie);
        } else {
            releaseAdult += "   " + getResources().getString(R.string.indicator_universal_movie);
        }

        mRatingBarMovieRating.setRating((float) mIncomingIntent.getDoubleExtra(C.EXTRAS_VOTE_AVERAGE, 0));

        mTextViewMovieReleaseAdult.setText(releaseAdult);
    }
}
