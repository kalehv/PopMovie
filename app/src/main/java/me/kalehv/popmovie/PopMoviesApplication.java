package me.kalehv.popmovie;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by harshadkale on 4/10/16.
 */
public class PopMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Stetho Configuration
        Stetho.initializeWithDefaults(this);

        // Picasso Configuration
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso picasso = builder.build();
        Picasso.setSingletonInstance(picasso);
    }
}
