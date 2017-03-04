package com.timen4.ronnny.timemovies.db;

import android.net.Uri;

/**
 * Created by ronny on 2017/3/4.
 */

public class MovieContract  {
    public static final String AUTHORITY = "com.timen4.ronnny.timemovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIE = "MovieInfo";
    public static final String PATH_REVIEW = "location";


}
