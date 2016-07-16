package me.kalehv.popmovie.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by harshadkale on 7/3/16.
 */

public class MovieSyncService extends Service {

    private final String TAG = MovieSyncService.class.getSimpleName();

    private static final Object syncLock = new Object();
    private static MovieSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: " + TAG);
        synchronized (syncLock) {
            if (syncAdapter == null) {
                syncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
