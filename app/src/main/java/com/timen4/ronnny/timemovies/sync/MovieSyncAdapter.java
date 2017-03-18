package com.timen4.ronnny.timemovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.timen4.ronnny.timemovies.Helper.DataHelper;
import com.timen4.ronnny.timemovies.MainActivity;
import com.timen4.ronnny.timemovies.R;
import com.timen4.ronnny.timemovies.bean.DetailResult;
import com.timen4.ronnny.timemovies.bean.MovieInfo_Table;
import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.bean.ReviewResult;
import com.timen4.ronnny.timemovies.bean.SortResult;
import com.timen4.ronnny.timemovies.bean.SortResult_Table;
import com.timen4.ronnny.timemovies.bean.TrailerResult;
import com.timen4.ronnny.timemovies.db.AppDatabase;
import com.timen4.ronnny.timemovies.utils.ToastUtil;
import com.timen4.ronnny.timemovies.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.timen4.ronnny.timemovies.BuildConfig.API_KEY;


public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = MovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 240 = 4 hours
    public static final int SYNC_INTERVAL = 60*60*4;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    private static final String BASE_URL = "http://api.themoviedb.org/";

    private static final String POPULAR="popular";
    private static final String TOP_RATED="top_rated";



    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
//        DataHelper dataHelper=new DataHelper(getContext());
//        dataHelper.pullMovieBicInfo(getContext());
        final Context context = getContext();
        if (!Utility.checkNetIsConnected(context)){
            ToastUtil.show(context,context.getString(R.string.no_network_tip));
            return;
        }
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)//主机地址
                .build();

        final String mLanguage="zh";
//        //2.创建访问API的请求
        final String sort = Utility.getPreferedSort(context);
        Call<MovieResult> call=null;

        final DataHelper.MovieService service = retrofit.create(DataHelper.MovieService.class);
// note: i wanna try to use rxjava+retrofit ,but failed,i don't where doesn't work.this is code:

//        service.getMovieInfo2(mLanguage, API_KEY)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.io())
//                .map(new Func1<MovieResult, List<MovieResult.MovieInfo>>() {
//                    @Override
//                    public List<MovieResult.MovieInfo> call(MovieResult movieResult) {
//                        List<MovieResult.MovieInfo> results = movieResult.getResults();
//                        System.out.print(results.get(0).getTitle());
//                        return null;
//                    }
//                });




        switch (sort){
            case "popular":
                call=service.getMovieInfo3(POPULAR,mLanguage,API_KEY);
                break;
            case "top_rated":
                call = service.getMovieInfo3(TOP_RATED, mLanguage, API_KEY);
                break;
        }

        //3.send request
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                //4.处理结果
                if (response.isSuccessful()){
                    ArrayList<MovieResult.MovieInfo> result= (ArrayList) response.body().getResults();
                    SortResult sortResult = new SortResult();
                    sortResult.sort=sort;
                    if (result==null||result.size()<1){
                        return;
                    }
                    //first inert the the way of sort
                    ContentUtils.insert(context.getContentResolver(), AppDatabase.SortProviderModel.CONTENT_URI,sortResult);
                    Where<SortResult> resultWhere2 = SQLite.select().from(SortResult.class).where(SortResult_Table.sort.eq(sort));
                    for (final MovieResult.MovieInfo movie :result) {
                        movie.sort=resultWhere2.querySingle()._id;
                        MovieResult.MovieInfo movieInfo1 = ContentUtils.querySingle(context.getContentResolver(),
                                AppDatabase.MovieProviderModel.CONTENT_URI,
                                MovieResult.MovieInfo.class,
                                ConditionGroup.clause().and(MovieInfo_Table.id.is(movie.getId())), null);
                        if (movieInfo1!=null&&movieInfo1.getTime()!=0){
                            continue;
                        }
                        ContentUtils.insert(context.getContentResolver(), AppDatabase.MovieProviderModel.CONTENT_URI,movie);
                        Log.e(TAG,movie.getId()+movie.getTitle());

                        Call<DetailResult> movieCall = service.getMovieDetail(movie.getId(), API_KEY);
                        movieCall.enqueue(new Callback<DetailResult>() {
                            @Override
                            public void onResponse(Call<DetailResult> call, Response<DetailResult> response) {
                                if (response.isSuccessful()){
                                    DetailResult detailResult = response.body();
                                    MovieResult.MovieInfo movieInfo = ContentUtils.querySingle(context.getContentResolver(),
                                            AppDatabase.MovieProviderModel.CONTENT_URI,
                                            MovieResult.MovieInfo.class,
                                            ConditionGroup.clause().and(MovieInfo_Table.id.is(detailResult.getId())), null);
                                    if (detailResult==null||movieInfo==null){
                                        return;
                                    }
                                    movieInfo.setTime(detailResult.getRuntime());
                                    int update = ContentUtils.update(context.getContentResolver(), AppDatabase.MovieProviderModel.CONTENT_URI, movieInfo);
                                    Log.e(TAG,"更新时间的数量:"+update+"——"+movieInfo.getTitle()+":"+movieInfo.getTime());
                                    SqlUtils.notifyModelChanged(MovieResult.MovieInfo.class, BaseModel.Action.CHANGE, null);
                                }
                            }

                            @Override
                            public void onFailure(Call<DetailResult> call, Throwable t) {
                                Log.e(TAG,"更新movieInfo失败:"+t.toString());
                            }
                        });

                        Call<ReviewResult> reviewCall = service.getMovieReview(movie.getId(), API_KEY);
                        //request movie's reviews
                        reviewCall.enqueue(new Callback<ReviewResult>() {
                            @Override
                            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                                if(response.isSuccessful()){
                                    List<ReviewResult.MovieReview> reviewList= response.body().getResults();
                                    if (reviewList==null){
                                        return;
                                    }
                                    for (ReviewResult.MovieReview review:reviewList) {
                                        review.movie=response.body().getId();
                                        ContentUtils.insert(context.getContentResolver(), AppDatabase.ReviewProviderModel.CONTENT_URI,review);
                                        Log.e(TAG,"更新评论："+review.movie+":"+review.getUrl());
                                    }

                                }

                            }

                            @Override
                            public void onFailure(Call<ReviewResult> call, Throwable t) {
                                Log.e("request movieReview failed",t.toString());
                            }
                        });

                        //request movie's trailers
                        final Call<TrailerResult> trailerCall = service.getMovieTrailer(movie.getId(), API_KEY);
                        trailerCall.enqueue(new Callback<TrailerResult>() {
                            @Override
                            public void onResponse(Call<TrailerResult> call, Response<TrailerResult> response) {
                                if (response.isSuccessful()){
                                    List<TrailerResult.MovieTrailer> trailerList = response.body().getResults();
                                    if (trailerList==null){
                                        return;
                                    }
                                    for (TrailerResult.MovieTrailer trailer :trailerList) {
                                        trailer.movie=response.body().getId();
                                        ContentUtils.insert(context.getContentResolver(), AppDatabase.TrailerProvidermodel.CONTENT_URI,trailer);
                                        Log.e(TAG,"更新预告片："+trailer.movie+":"+trailer.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<TrailerResult> call, Throwable t) {
                                Log.e("request movieTrailer failed",t.toString());
                            }
                        });
                    }
                    MovieSyncAdapter.notifyMovies(context);
                }
            }
            //
            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Log.e("request movieInfo failed",t.toString());
            }
        });

        Log.e(TAG,"正在同步数据......");
    }

//    /**
//     * Take the String representing the complete forecast in JSON Format and
//     * pull out the data we need to construct the Strings needed for the wireframes.
//     *
//     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//     * into an Object hierarchy for us.
//     */
//    private void getWeatherDataFromJson(String forecastJsonStr, String locationSetting)
//            throws JSONException {
//
//        // Now we have a String representing the complete forecast in JSON Format.
//        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
//        // into an Object hierarchy for us.
//
//        // These are the names of the JSON objects that need to be extracted.
//
//        // Location information
//        final String OWM_CITY = "city";
//        final String OWM_CITY_NAME = "name";
//        final String OWM_COORD = "coord";
//
//        // Location coordinate
//        final String OWM_LATITUDE = "lat";
//        final String OWM_LONGITUDE = "lon";
//
//        // Weather information.  Each day's forecast info is an element of the "list" array.
//        final String OWM_LIST = "list";
//
//        final String OWM_PRESSURE = "pressure";
//        final String OWM_HUMIDITY = "humidity";
//        final String OWM_WINDSPEED = "speed";
//        final String OWM_WIND_DIRECTION = "deg";
//
//        // All temperatures are children of the "temp" object.
//        final String OWM_TEMPERATURE = "temp";
//        final String OWM_MAX = "max";
//        final String OWM_MIN = "min";
//
//        final String OWM_WEATHER = "weather";
//        final String OWM_DESCRIPTION = "main";
//        final String OWM_WEATHER_ID = "id";
//
//        try {
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
//            String cityName = cityJson.getString(OWM_CITY_NAME);
//
//            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
//            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
//            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
//
//            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);
//
//            // Insert the new weather information into the database
//            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // These are the values that will be collected.
//                long dateTime;
//                double pressure;
//                int humidity;
//                double windSpeed;
//                double windDirection;
//
//                double high;
//                double low;
//
//                String description;
//                int weatherId;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//
//                pressure = dayForecast.getDouble(OWM_PRESSURE);
//                humidity = dayForecast.getInt(OWM_HUMIDITY);
//                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
//                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);
//
//                // Description is in a child array called "weather", which is 1 element long.
//                // That element also contains a weather code.
//                JSONObject weatherObject =
//                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//                weatherId = weatherObject.getInt(OWM_WEATHER_ID);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                high = temperatureObject.getDouble(OWM_MAX);
//                low = temperatureObject.getDouble(OWM_MIN);
//
//                ContentValues weatherValues = new ContentValues();
//
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
//
//                cVVector.add(weatherValues);
//            }
//
//            int inserted = 0;
//            // add to database
//            if ( cVVector.size() > 0 ) {
//                ContentValues[] cvArray = new ContentValues[cVVector.size()];
//                cVVector.toArray(cvArray);
//                getContext().getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray);
//
//                // delete old data so we don't build up an endless history
//                getContext().getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
//                        WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
//                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});
//
//                notifyMovies();
//            }
//
//            Log.d(TAG, "Sunshine Service Complete. " + cVVector.size() + " Inserted");
//
//        } catch (JSONException e) {
//            Log.e(TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }
//    }

    public static void notifyMovies(Context context) {
//        Context context = getContext();
        //checking the last update and notify if it's the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if ( displayNotifications ) {

            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {

                List<MovieResult.MovieInfo> movies= new ArrayList();
                String sort = Utility.getPreferedSort(context);
                if (sort.equals("popular")){
                    movies = ContentUtils.queryList(AppDatabase.MovieProviderModel.CONTENT_URI,
                            MovieResult.MovieInfo.class,
                            ConditionGroup.clause(),
                            AppDatabase.MovieProviderModel.MOVIE_POPULAR+" DESC",new String[]{});
                    sort=context.getString(R.string.poularMovie);

                }else{
                    movies =  ContentUtils.queryList(AppDatabase.MovieProviderModel.CONTENT_URI,
                            MovieResult.MovieInfo.class,
                            ConditionGroup.clause(),
                            AppDatabase.MovieProviderModel.MOVIE_SCORE+" DESC",new String[]{});
                    sort=context.getString(R.string.highScoreMovie);
                }

                Resources resources = context.getResources();
                Bitmap largeIcon = BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher);
                String title = context.getString(R.string.app_name);

                // Define the text of the forecast.
                String contentText = "更新"+sort+"『"+movies.get(0).getTitle()+"』";

                // NotificationCompatBuilder is a very convenient way to build backward-compatible
                // notifications.  Just throw in some data.
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setAutoCancel(true)
                                .setColor(resources.getColor(R.color.colorPrimary))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(largeIcon)
                                .setContentTitle(title)
                                .setContentText(contentText);

                // Make something interesting happen when the user clicks on the notification.
                // In this case, opening the app is sufficient.
                Intent resultIntent = new Intent(context, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());

                //refreshing last sync
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.commit();
            }
        }

    }



    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }
//
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}