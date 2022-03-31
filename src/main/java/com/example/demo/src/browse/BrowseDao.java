package com.example.demo.src.browse;

import com.example.demo.src.browse.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BrowseDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public GetMainRes getHomeMain() {
        String getHomeMainQuery = "SELECT id, title, ageRate, summary, type, previewUrl, logoImgUrl, mainImgUrl " +
                "FROM Content WHERE logoImgUrl != 'N' AND releaseDate <= current_timestamp " +
                "ORDER BY RAND() LIMIT 1";
        return this.jdbcTemplate.queryForObject(getHomeMainQuery,
                (rs, rowNum) -> new GetMainRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("previewUrl"),
                        rs.getString("logoImgUrl"),
                        rs.getString("mainImgUrl"))
                );
    }

    public GetMainRes getSeriesOrMovieMain(String browseType) {
        String getSeriesOrMovieMainQuery = "SELECT id, title, ageRate, summary, type, previewUrl, logoImgUrl, mainImgUrl FROM Content " +
                "WHERE type = ? AND logoImgUrl != 'N' AND releaseDate <= current_timestamp " +
                "ORDER BY RAND() LIMIT 1";
        String getSeriesOrMovieMainParams = browseType;
        return this.jdbcTemplate.queryForObject(getSeriesOrMovieMainQuery,
                (rs, rowNum) -> new GetMainRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("previewUrl"),
                        rs.getString("logoImgUrl"),
                        rs.getString("mainImgUrl")),
                getSeriesOrMovieMainParams);
    }

    public int checkGenreName(String genre) {
        String checkGenreNameQuery = "select exists(select genreId from GenreContent where genreId in (select id from Genre where name = ?))";
        String checkGenreNameParams = genre;

        return this.jdbcTemplate.queryForObject(checkGenreNameQuery,
                int.class,
                checkGenreNameParams);
    }

    public List<GetGenreMovieRes> getGenreMovies(String genre) {
        String getGenreMoviesQuery = "select id, title, ageRate, type, thumbnailUrl, previewUrl, runningTime, percentage " +
                "from Content " +
                "where id in (select contentId from GenreContent where genreId in (select id from Genre where name = ?)) " +
                "  and type = 'M' and releaseDate <= current_timestamp";
        String getGenreMoviesParams = genre;
        return this.jdbcTemplate.query(getGenreMoviesQuery,
                (rs, rowNum) -> new GetGenreMovieRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage")),
                getGenreMoviesParams);
    }

    public List<GetGenreSeriesRes> getGenreSeries(String genre) {
        String getGenreSeriesQuery = "select id, title, ageRate, type, thumbnailUrl, previewUrl, runningTime, percentage " +
                "from Content " +
                "where id in (select contentId from GenreContent where genreId in (select id from Genre where name = ?)) " +
                "  and type = 'S' and releaseDate <= current_timestamp";
        String getGenreSeriesParams = genre;
        return this.jdbcTemplate.query(getGenreSeriesQuery,
                (rs, rowNum) -> new GetGenreSeriesRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("percentage"),
                        rs.getString("newEpisode")),
                getGenreSeriesParams);
    }

    public List<TopTen> getTopTenIdx() {
        String getTopTenIdxQuery = "select contentId, likeCount from (select contentId, count(*) as likeCount from Likes " +
                "where status = 'A' group by contentId) as contentLikes " +
                "order by likeCount desc limit 10";

        return this.jdbcTemplate.query(getTopTenIdxQuery,
                (rs, rowNum) -> new TopTen(
                        rs.getLong("contentId"),
                        rs.getInt("likeCount"))
                );

    }

    public GetTopTenRes getTopTenContents(long topTenIdx) {
        String getTopTenContentsQuery = "select id, title, ageRate, type, thumbnailUrl, previewUrl, runningTime, percentage, newEpisode " +
                "from Content where id = ?";
        long getTopTenContentsParams = topTenIdx;
        return this.jdbcTemplate.queryForObject(getTopTenContentsQuery,
                (rs, rowNum) -> new GetTopTenRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("newEpisode")),
                getTopTenContentsParams);
    }

    public List<GetLatestRes> getLatest() {
        String getLatestQuery = "select id, title, ageRate, type, thumbnailUrl, previewUrl, runningTime, percentage, newEpisode " +
                "from Content where releaseDate <= current_timestamp " +
                "order by releaseDate desc limit 12";
        return this.jdbcTemplate.query(getLatestQuery,
                (rs, rowNum) -> new GetLatestRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("newEpisode"))
                );
    }

    public List<GetSoonRes> getSoon() {
        String getSoonQuery = "select id, title, " +
                "       case " +
                "           when timestampdiff(day, current_timestamp, releaseDate) < 7 " +
                "               then date_format(releaseDate, '%a') " +
                "           else date_format(releaseDate, '%c월 %e일') end as releaseDate, " +
                "       type, thumbnailUrl, previewUrl " +
                "from Content where releaseDate > current_timestamp";
        return this.jdbcTemplate.query(getSoonQuery,
                (rs, rowNum) -> new GetSoonRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("releaseDate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"))
                );
    }

    public List<GetSearchRes> getRelated(int numOfRelated, String search) { // 매개변수 개수만큼 가져오기, 검색 결과와 일치하는건 제외
        String getRelatedQuery = "select id, title, " +
                "       case " +
                "           when releaseDate <= current_timestamp " +
                "               then '공개된 컨텐츠' " +
                "           when timestampdiff(day, current_timestamp, releaseDate) < 7 " +
                "               then date_format(releaseDate, '%a') " +
                "           else date_format(releaseDate, '%c월 %e일') end as releaseDate, " +
                "       ageRate, type, thumbnailUrl, previewUrl, runningTime, percentage, newEpisode " +
                "from Content where id not in (select id from Content where title like concat('%',?,'%')) ORDER BY RAND() LIMIT ?";
        Object[] getRelatedParams = new Object[]{search, numOfRelated};
        return this.jdbcTemplate.query(getRelatedQuery,
                (rs, rowNum) -> new GetSearchRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("releaseDate"),
                        rs.getLong("ageRate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("newEpisode")),
                getRelatedParams);
    }

    public List<GetSearchRes> getSearch(String search) { // 검색한 것 가져오기
        String getSearchQuery = "select id, title, " +
                "       case " +
                "           when releaseDate <= current_timestamp " +
                "               then '공개된 컨텐츠' " +
                "           when timestampdiff(day, current_timestamp, releaseDate) < 7 " +
                "               then date_format(releaseDate, '%a') " +
                "           else date_format(releaseDate, '%c월 %e일') end as releaseDate, " +
                "       ageRate, type, thumbnailUrl, previewUrl, runningTime, percentage, newEpisode " +
                "       from Content where title like concat('%',?,'%')";
        String getSearchParams = search;
        return this.jdbcTemplate.query(getSearchQuery,
                (rs, rowNum) -> new GetSearchRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("releaseDate"),
                        rs.getLong("ageRate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("newEpisode")),
                getSearchParams);
    }
}
