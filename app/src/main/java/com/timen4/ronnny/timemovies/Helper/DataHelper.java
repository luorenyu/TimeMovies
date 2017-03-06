package com.timen4.ronnny.timemovies.Helper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.timen4.ronnny.timemovies.bean.DetailResult;
import com.timen4.ronnny.timemovies.bean.MovieInfo_Table;
import com.timen4.ronnny.timemovies.bean.MovieResult;
import com.timen4.ronnny.timemovies.bean.ReviewResult;
import com.timen4.ronnny.timemovies.bean.SortResult;
import com.timen4.ronnny.timemovies.bean.SortResult_Table;
import com.timen4.ronnny.timemovies.bean.TrailerResult;
import com.timen4.ronnny.timemovies.db.AppDatabase;
import com.timen4.ronnny.timemovies.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

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

    private static final String BASE_URL = "http://api.themoviedb.org/";

    private static final String POPULAR="popular";
    private static final String TOP_RATED="top_rated";
    private String TAG="DataHelper";

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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)//主机地址
                .build();

        final String mLanguage="zh";
//        //2.创建访问API的请求
        final String sort = Utility.getPreferedSort(context);
        Call<MovieResult> call=null;

        final MovieService service = retrofit.create(MovieService.class);
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
                                    Log.e(TAG,"update 的数量"+update);
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
                                        Log.e(TAG,review.movie+review.getContent());
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
                                        Log.e(TAG,trailer.movie+trailer.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<TrailerResult> call, Throwable t) {
                                Log.e("request movieTrailer failed",t.toString());
                            }
                        });
                    }
                }
            }
//
            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Log.e("request movieInfo failed",t.toString());
            }
        });

    }

    public interface MovieService {
        @GET("/3/movie/{sort}")
        Observable<MovieResult> getMovieInfo(@Path("sort") String sort, @Query("language") String language, @Query("api_key") String api_key);

        @GET("/3/movie/popular")
        Observable<MovieResult> getMovieInfo2(@Query("language") String language, @Query("api_key") String api_key);

        @GET("/3/movie/{sort}")
        Call<MovieResult> getMovieInfo3(@Path("sort") String sort, @Query("language") String language, @Query("api_key") String api_key);

        //http://api.themoviedb.org/3/movie/297761/reviews?&api_key=""
        @GET("3/movie/{movieId}/reviews")
        Call<ReviewResult> getMovieReview(@Path("movieId") int movieId,@Query("api_key") String api_Key);

        @GET("3/movie/{movieId}/videos")
        Call<TrailerResult> getMovieTrailer(@Path("movieId") int movieId,@Query("api_key") String api_key);

        //http://api.themoviedb.org/3/movie/297761?api_key=
        @GET("3/movie/{movie_id}")
        Call<DetailResult> getMovieDetail(@Path("movie_id") int movieId, @Query("api_key") String pai_key);

    }




}
