package com.timen4.ronnny.timemovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TimeMovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("TimeMovieSyncService", "onCreate - TimeMovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}