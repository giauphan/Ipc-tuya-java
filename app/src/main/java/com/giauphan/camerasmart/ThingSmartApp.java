package com.giauphan.camerasmart;

import android.app.Application;

import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class ThingSmartApp  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThingHomeSdk.init(this);
    }
}
