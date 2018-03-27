package com.dws.wardrobeos;

import android.app.Application;
import android.location.Location;

import com.dws.GPSTracker;

import io.realm.Realm;

public class MyApp extends Application {

    GPSTracker mGPSTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        mGPSTracker = new GPSTracker(getApplicationContext());
        if (mGPSTracker.canGetLocation()) {
            Location location = mGPSTracker.getLocation();
        }
    }
}
