package me.kalehv.popmovie;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import me.kalehv.popmovie.global.MoviesFilter;

/**
 * Created by hk022893 on 7/9/16.
 */

public class FilterFragmentPageAdapter
        extends FragmentPagerAdapter {

    private final String TAG = FilterFragmentPageAdapter.class.getSimpleName();

    private Context context;

    private final int[] tabTitlesResources = {
            R.string.pref_filter_popular_title,
            R.string.pref_filter_top_rated_title,
            R.string.pref_filter_favorite_title
    };

    private final int[] tabIconsResources = {
            R.drawable.ic_local_movies_selector,
            R.drawable.ic_star_selector,
            R.drawable.ic_favorite_selector
    };

    private final int TABS_COUNT = 3;

    public FilterFragmentPageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: Getting appropriate fragment for position - " + position);
        switch (position) {
            case MoviesFilter.POPULARITY:
                return MainFragment.newInstance(MoviesFilter.POPULARITY);
            case MoviesFilter.TOP_RATED:
                return MainFragment.newInstance(MoviesFilter.TOP_RATED);
            case MoviesFilter.FAVORITE:
                return MainFragment.newInstance(MoviesFilter.FAVORITE);
            default:
                return MainFragment.newInstance(MoviesFilter.UNKNOWN);
        }
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: " + TABS_COUNT);
        return TABS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(tabTitlesResources[position]);
    }

    /**
     * Returns Int ResourceId of icon to set for tab at position
     * @param position of tab
     * @return @IntegerRes icon resource
     */
    public int getIcon(int position) {
        return tabIconsResources[position];
    }
}
