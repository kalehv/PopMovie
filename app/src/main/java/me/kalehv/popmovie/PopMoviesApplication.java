package me.kalehv.popmovie;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by harshadkale on 4/10/16.
 */
public class PopMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
