package com.timen4.ronnny.timemovies.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.bean.SortResult;
import com.timen4.ronnny.timemovies.bean.SortResult_Table;
import com.timen4.ronnny.timemovies.db.AppDatabase;
import com.timen4.ronnny.timemovies.utils.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.timen4.ronnny.timemovies.BuildConfig.API_KEY;

/**
 * Created by ronny on 2017/3/4.
 */

public class DataHelper {

    //deal request internet data logic
    //key:c10d7f437c43bf19190ac874c33541bc
    //http://api.themoviedb.org/3/movie/popular?language=zh&api_key=[YOUR_API_KEY]
    //http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=[YOUR_API_KEY]

    private  Context mContext;

    private static final String BASE_URL = "http://api.themoviedb.org";

    public DataHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 从网络上拉去数据到本地
     * @param context
     */
    public void pullMovieBicInfo(final Context context){
        if (!Utility.checkNetIsConnected(context)){
            Toast.makeText(context,"当前网络不可用，请链接网络后重试",Toast.LENGTH_SHORT).show();
            return;
        }
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .baseUrl(BASE_URL)//主机地址
                .build();

        //2.创建访问API的请求
        final String sort = Utility.getPreferedSort(context);
        String mLanguage="zh";
        Call<MovieResult> call=null;
        BasicInfoService service = retrofit.create(BasicInfoService.class);
        switch (sort){
            case "popular":
               call=service.getPopularResult(mLanguage,API_KEY);
                break;
            case "top_rated":
                call = service.getTopRatedResult(mLanguage, API_KEY);
                break;
        }

        //3.发送请求
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                //4.处理结果
                if (response.isSuccessful()){
                    ArrayList result= (ArrayList) response.body().getResults();
                    SortResult sortResult = new SortResult();
                    sortResult.sort=sort;
                    //现插入排序方式
                    ContentUtils.insert(context.getContentResolver(), AppDatabase.SortProviderModel.CONTENT_URI,sortResult);
                    //再插入电影详情
                    Where<SortResult> resultWhere2 = SQLite.select().from(SortResult.class).where(SortResult_Table.sort.eq(sort));
                    sortResult._id=resultWhere2.querySingle()._id;
                    ((MovieResult.MovieInfo)result.get(0)).sort=sortResult._id;
                    ContentUtils.insert(context.getContentResolver(), AppDatabase.MovieProviderModel.CONTENT_URI, result.get(0));
//                    mAdapter.setDatas(result);
//                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Log.e("失败",t.toString());
            }
        });

    }
    public interface BasicInfoService {
        @GET("/3/movie/popular")
        Call<MovieResult> getPopularResult(@Query("language") String language, @Query("api_key") String api_key);

        @GET("/3/movie/top_rated")
        Call<MovieResult> getTopRatedResult(@Query("language") String language, @Query("api_key") String api_key);
    }




}
