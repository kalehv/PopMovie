package me.kalehv.popmovie.global;

import android.content.Context;

import me.kalehv.popmovie.R;

/**
 * Created by hk022893 on 7/10/16.
 */

public class MoviesFilter {
    public static final int UNKNOWN = -1;
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;
    public static final int FAVORITE = 2;

    public static int toMoviesFilter(Context context, String filter) {
        if (filter.equals(context.getString(R.string.pref_filter_popular))) {
            return POPULARITY;
        }
        else if (filter.equals(context.getString(R.string.pref_filter_top_rated))) {
            return TOP_RATED;
        } else if (filter.equals(context.getString(R.string.pref_filter_favorite))) {
            return FAVORITE;
        }
        return UNKNOWN;
    }

    public static String toString(Context context, int filter) {
        switch (filter) {
            case POPULARITY:
                return context.getString(R.string.pref_filter_popular);
            case TOP_RATED:
                return context.getString(R.string.pref_filter_top_rated);
            case FAVORITE:
                return context.getString(R.string.pref_filter_favorite);
            default:
                return context.getString(R.string.pref_filter_unknown);
        }
    }
}
