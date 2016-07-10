package me.kalehv.popmovie;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

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
            case 0:
                return MainFragment.newInstance(context.getString(R.string.pref_filter_popular));
            case 1:
                return MainFragment.newInstance(context.getString(R.string.pref_filter_top_rated));
            case 2:
                return MainFragment.newInstance(context.getString(R.string.pref_filter_favorite));
        }
        return null;
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
