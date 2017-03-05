package com.timen4.ronnny.timemovies.bean;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.timen4.ronnny.timemovies.db.AppDatabase;

import java.util.List;

/**
 * Created by ronny on 2017/3/4.
 */

public class TrailerResult {
    /**
     * id : 297761
     * results : [{"id":"58a5cc809251413cb20070d8","iso_639_1":"en","iso_3166_1":"US","key":"-RLP2xTbdg0","name":"Official Trailer 2","site":"YouTube","size":1080,"type":"Trailer"},{"id":"58403fbb9251417f8600b5d4","iso_639_1":"en","iso_3166_1":"US","key":"NW0lOo9_8Bw","name":"Team Suicide Squad","site":"YouTube","size":720,"type":"Featurette"},{"id":"58403fcb9251417f7900b892","iso_639_1":"en","iso_3166_1":"US","key":"JsbG97hO6lA","name":"Harley Quinn Therapy","site":"YouTube","size":720,"type":"Featurette"},{"id":"58a5cc679251416faf001b64","iso_639_1":"en","iso_3166_1":"US","key":"fcvyevK32Rc","name":"Official Comic Trailer","site":"YouTube","size":1080,"type":"Trailer"},{"id":"58a5cc73c3a3683da1007328","iso_639_1":"en","iso_3166_1":"US","key":"6W-P7_4N3CI","name":"Official Trailer 1","site":"YouTube","size":1080,"type":"Trailer"}]
     */
    private int id;
    private List<MovieTrailer> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MovieTrailer> getResults() {
        return results;
    }

    public void setResults(List<MovieTrailer> results) {
        this.results = results;
    }

    @Table(database = AppDatabase.class,name = MovieTrailer.NAME,allFields = true,insertConflict = ConflictAction.IGNORE)
    public static class MovieTrailer {
        public static final String NAME="MovieTrailer";
        /**
         * id （视频id）: 58a5cc809251413cb20070d8
         * iso_639_1 （语言简码）: en
         * iso_3166_1 （国家简码）: US
         * key （youtube视频拼接字符串）: -RLP2xTbdg0
         * name（片名） : Official Trailer 2
         * site（网站） : YouTube
         * size （分辨率）: 1080
         * type （类型）: Trailer
         */
        @PrimaryKey
        @Unique(onUniqueConflict = ConflictAction.IGNORE)
        private String id;
        @ForeignKey(tableClass = MovieResult.MovieInfo.class)
        public int movie;
        private String iso_639_1;
        private String iso_3166_1;
        private String key;
        private String name;
        private String site;
        private int size;

        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public String getIso_3166_1() {
            return iso_3166_1;
        }

        public void setIso_3166_1(String iso_3166_1) {
            this.iso_3166_1 = iso_3166_1;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
