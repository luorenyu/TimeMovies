package com.timen4.ronnny.timemoies;

import java.util.List;

/**
 * Created by :luore
 * Date: 2017/2/16
 */

public class MovieItem {

    public MovieItem(boolean adult, List<Integer> genre_ids, String backdrop_path,
                     int id, String original_language, String original_title,
                     String overview, double popularity, String poster_path,
                     String release_date, String title, boolean video,
                     double vote_average, int vote_count) {
        this.adult = adult;
        this.genre_ids = genre_ids;
        this.backdrop_path = backdrop_path;
        this.id = id;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.title = title;
        this.video = video;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
    }

    /**
     * adult : false
     * backdrop_path : /biN2sqExViEh8IYSJrXlNKjpjxx.jpg
     * genre_ids : [27]
     * id : 14564
     * original_language : en
     * original_title : Rings
     * overview : 故事设定在《美版午夜凶铃2》结尾的13年后，玛蒂尔达·鲁茨（Matilda Lutz）和阿历克斯·罗（Alex Roe）将在片中饰演一对情侣，后者因为看了录像带而开始疏远女友。
     * popularity : 159.533213
     * poster_path : /AmbtHzH5kGt4dPTw2E4tBZQcLjz.jpg
     * release_date : 2017-02-01
     * title : 午夜凶铃3(美版)
     * video : false
     * vote_average : 5.2
     * vote_count : 209
     */


    private boolean adult;
    private String backdrop_path;
    private int id;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private String poster_path;
    private String release_date;
    private String title;
    private boolean video;
    private double vote_average;
    private int vote_count;
    private List<Integer> genre_ids;

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }
}
