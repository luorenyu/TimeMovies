package com.timen4.ronnny.timemovies.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ronny on 2017/3/7.
 */

public class ToastUtil {

    private static Toast mToast = null;

    public static void show(Context context, String msg){
        if (mToast==null){
            mToast=Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}
