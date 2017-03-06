package com.timen4.ronnny.timemovies.db;

import android.net.Uri;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by ronny on 2017/3/3.
 */
@ContentProvider(authority = AppDatabase.AUTHORITY,
        database = AppDatabase.class,
        baseContentUri = AppDatabase.BASE_CONTENT_URI)
@Database(name = AppDatabase.NAME,version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "AppDatabase"; // we will add the .db extension

    public static final int VERSION = 1;

    public static final String AUTHORITY = "com.timen4.ronnny.timemovies.db";

    public static final String BASE_CONTENT_URI = "content://";

    @TableEndpoint(name = MovieProviderModel.ENDPOINT,contentProvider = AppDatabase.class)
    public static class MovieProviderModel {

        public static final String ENDPOINT = "MovieInfo";

        public static final String MOVIE_TITLE="title";
        public static final String MOVIE_SCORE="vote_average";
        public static final String MOVIE_PIC="poster_path";
        public static final String MOVIE_POPULAR="popularity";


        private static Uri buildUri(String... paths) {
            Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
            for (String path : paths) {
                builder.appendPath(path);
            }
            return builder.build();
        }

        @ContentUri(path = MovieProviderModel.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)

        public static Uri CONTENT_URI = buildUri(ENDPOINT);

    }

    @TableEndpoint(name = SortProviderModel.ENDPOINT,contentProvider = AppDatabase.class)
    public static class SortProviderModel{
        public static final String ENDPOINT = "Sort";

        private static Uri buildUri(String... paths) {
            Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
            for (String path : paths) {
                builder.appendPath(path);
            }
            return builder.build();
        }

        @ContentUri(path = SortProviderModel.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)

        public static Uri CONTENT_URI = buildUri(ENDPOINT);

    }
    @TableEndpoint(name = ReviewProviderModel.ENDPOINT,contentProvider = AppDatabase.class)
    public static class ReviewProviderModel{
        public static final String ENDPOINT = "MovieReview";

        private static Uri buildUri(String... paths) {
            Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
            for (String path : paths) {
                builder.appendPath(path);
            }
            return builder.build();
        }

        @ContentUri(path = ReviewProviderModel.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)

        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

    @TableEndpoint(name = TrailerProvidermodel.ENDPOINT,contentProvider = AppDatabase.class)
    public static class TrailerProvidermodel{
        public static final String ENDPOINT = "MovieTrailer";

        private static Uri buildUri(String... paths) {
            Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
            for (String path : paths) {
                builder.appendPath(path);
            }
            return builder.build();
        }

        @ContentUri(path = TrailerProvidermodel.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)

        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

}
