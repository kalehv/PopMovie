package me.kalehv.popmovie.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import me.kalehv.popmovie.R;

/**
 * Created by harshadkale on 7/3/16.
 */

public class Utility {

    public static String getMovieFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                context.getString(R.string.pref_filter_key),
                context.getString(R.string.pref_filter_top_rated)
        );
    }

}
