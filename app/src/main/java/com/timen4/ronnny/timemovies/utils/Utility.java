package com.timen4.ronnny.timemovies.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.timen4.ronnny.timemovies.R;

/**
 * Created by ronny on 2017/3/4.
 */

public class Utility {


    /**
     * 判断当前网络是否可用
     * @return
     */
    public static boolean checkNetIsConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * 判断是否是第一次进入app
     * @param context
     * @return
     */
    public static boolean isFirstInApp(Context context){
        SharedPreferences sp = context.getSharedPreferences("isFirstIn", Activity.MODE_PRIVATE);
        boolean isFirstIn = sp.getBoolean("isFirstIn", true);
        if(isFirstIn) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        }
        return isFirstIn;
    }

    public static String getPreferedSort(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort= sharedPrefs.getString(context.getString(R.string.pre_sort_key),context.getString(R.string.pre_popular_sort));
        return sort;
    }
    public static int getPreferedSyncFrequency(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String frequency= sharedPrefs.getString(context.getString(R.string.sync_frequency_key),context.getString(R.string.sync_a_day));
        int sync;
        if (frequency.equals(context.getString(R.string.sync_a_day))){
            sync=24;
        }else if (frequency.equals(context.getString(R.string.sync_4_hours))){
            sync=4;
        }else if (frequency.equals(context.getString(R.string.sync_a_week))){
            sync=24*60;
        }else{
            sync= Integer.MAX_VALUE;
        }

        return sync;
    }
}
