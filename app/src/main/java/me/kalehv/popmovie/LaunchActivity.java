package me.kalehv.popmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.kalehv.popmovie.sync.MovieSyncAdapter;

public class LaunchActivity
        extends AppCompatActivity
        implements MovieSyncAdapter.OnSyncListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do Whatever is needed
        MovieSyncAdapter.addListener(this);
        MovieSyncAdapter.syncImmediately(this);
//        deleteDatabase(C.MOVIE_DATABASE_NAME);
    }

    @Override
    public void onSyncComplete() {
        // Remove self from listener
        MovieSyncAdapter.removeListener(this);
        // Launch Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
