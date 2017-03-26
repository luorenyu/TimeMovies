package com.timen4.ronnny.timemovies;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.timen4.ronnny.timemovies.utils.SharedPreferencesUtils;
import com.timen4.ronnny.timemovies.utils.Utility;

/**
 * Created by ronny on 2017/3/3.
 */

public class TimeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this)
                .openDatabasesOnInit(true).build());
        SharedPreferencesUtils.setParam(this,MoviesFragment.SELECTED_KEY,0);
    }


}
