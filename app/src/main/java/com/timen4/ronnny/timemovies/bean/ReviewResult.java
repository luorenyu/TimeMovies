package com.timen4.ronnny.timemovies.bean;

import com.raizlabs.android.dbflow.annotation.Column;
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

public class ReviewResult {
    /**
     * id : 297761
     * page : 1
     * results :
     * total_pages : 1
     * total_results : 5
     */

    private int id;
    private int page;
    private int total_pages;
    private int total_results;
    private List<MovieReview> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public List<MovieReview> getResults() {
        return results;
    }

    public void setResults(List<MovieReview> results) {
        this.results = results;
    }

    @Table(database = AppDatabase.class,name = MovieReview.NAME,insertConflict = ConflictAction.IGNORE)
    public static class MovieReview {
        public static final String NAME="MovieReview";
        /**
         * id : 57a814dc9251415cfb00309a
         * author : Frank Ochieng
         * content :
         (c) **Frank Ochieng** (2016)
         * url : https://www.themoviedb.org/review/57a814dc9251415cfb00309a
         */


//        @Column
//        public long _id;
        @Column
        @ForeignKey(tableClass = MovieResult.MovieInfo.class)
        public int movie;
        @PrimaryKey
        @Unique(onUniqueConflict = ConflictAction.IGNORE)
        @Column
        private String id;
        @Column
        private String author;
        @Column
        private String content;
        @Column
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
