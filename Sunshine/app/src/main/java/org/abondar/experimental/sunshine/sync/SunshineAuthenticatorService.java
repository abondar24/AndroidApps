package org.abondar.experimental.sunshine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by abondar on 1/15/17.
 */
public class SunshineAuthenticatorService extends Service {
    private SunshineAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new SunshineAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
