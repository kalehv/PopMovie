package me.kalehv.popmovie.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.v7.preference.PreferenceManager;

import me.kalehv.popmovie.R;

/**
 * Created by harshadkale on 7/3/16.
 */

public class Utility {

    public static String getMoviesFilter(Context context, @StringRes int defaultFilter) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String filter = sharedPreferences.getString(
                context.getString(R.string.pref_filter_key),
                context.getString(defaultFilter)
        );
        return filter;
    }

    public static void setMoviesFilter(Context context, String filter) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(context.getString(R.string.pref_filter_key), filter).apply();
    }

}
