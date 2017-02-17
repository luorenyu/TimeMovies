package com.timen4.ronnny.timemovies;

import java.io.Serializable;
import java.util.List;

/**
 * Created by :luore
 * Date: 2017/2/17
 */

public class MovieResults {
    /**
     * page : 1
     * results : [{"adult":false,"backdrop_path":"/biN2sqExViEh8IYSJrXlNKjpjxx.jpg","genre_ids":[27],"id":14564,"original_language":"en","original_title":"Rings","overview":"故事设定在《美版午夜凶铃2》结尾的13年后，玛蒂尔达·鲁茨（Matilda Lutz）和阿历克斯·罗（Alex Roe）将在片中饰演一对情侣，后者因为看了录像带而开始疏远女友。","popularity":159.533213,"poster_path":"/AmbtHzH5kGt4dPTw2E4tBZQcLjz.jpg","release_date":"2017-02-01","title":"午夜凶铃3(美版)","video":false,"vote_average":5.1,"vote_count":216},{"adult":false,"backdrop_path":"/lubzBMQLLmG88CLQ4F3TxZr2Q7N.jpg","genre_ids":[12,16,35,10751],"id":328111,"original_language":"en","original_title":"The Secret Life of Pets","overview":"讲述了在纽约一幢热闹的公寓大楼里，有一群宠物，每天主人出门后、回家前这里就变成了它们的乐园：有的和其他宠物一起出去玩；有的聚在一起交流主人的糗事；还有的在不停捯饬自己的外貌，使自己看上去更可爱以便从主人那里要来更多的零食\u2026\u2026总之，宠物们每天的\u201c朝九晚五\u201d是他们一天中最自由、最惬意的时光。  　　在这群宠物中，有一只小猎犬是当仁不让的领袖，他叫麦克斯（Max），机智可爱，自认为是女主人生活的中心\u2014\u2014直到她从外带回家一只懒散、没有家教的杂种狗\u201c公爵\u201d（Duke）。  　　麦克斯和公爵人生观价值观都不一样，自然很难和平共处。但当它们一起流落纽约街头后，两人又必须抛弃分歧、共同阻止一只被主人抛弃的宠物兔\u201c雪球\u201d（Snowball）\u2014\u2014后者为了报复人类，准备组织一支遭弃宠物大军在晚饭前向人类发起总攻\u2026\u2026","popularity":109.400417,"poster_path":"/AgzX7mmCrQcSozvqWGwSpFAsEXj.jpg","release_date":"2016-06-18","title":"爱宠大机密","video":false,"vote_average":5.8,"vote_count":2224}]
     * total_pages : 974
     * total_results : 19467
     */

    private int page;
    private int total_pages;
    private int total_results;
    private List<ResultsBean> results;

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

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean implements Serializable{
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
         * vote_average : 5.1
         * vote_count : 216
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

        public ResultsBean(String title, double vote_average) {
            this.title=title;
            this.vote_average=vote_average;
        }

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
}
