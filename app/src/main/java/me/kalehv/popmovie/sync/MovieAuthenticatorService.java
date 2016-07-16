package me.kalehv.popmovie.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by harshadkale on 7/3/16.
 */

public class MovieAuthenticatorService extends Service {

    private MovieAuthenticator authenticator;

    @Override
    public void onCreate() {
        this.authenticator = new MovieAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
