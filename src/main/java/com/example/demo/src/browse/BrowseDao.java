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



    public GetMainRes getHomeMain(GetMainReq getMainReq) {
        String getHomeMainQuery = "SELECT id, title, ageRate, summary, type, thumbnailUrl, previewUrl FROM Content " +
                "WHERE logoImgUrl != 'N' ORDER BY RAND() LIMIT 1";
        return this.jdbcTemplate.queryForObject(getHomeMainQuery,
                (rs, rowNum) -> new GetMainRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl")
                ));
    }

    public GetMainRes getSeriesOrMovieMain(GetMainReq getMainReq) {
        String getSeriesOrMovieMainQuery = "SELECT id, title, ageRate, summary, type, thumbnailUrl, previewUrl FROM Content " +
                "WHERE type = ? AND logoImgUrl != 'N' ORDER BY RAND() LIMIT 1";
        String getSeriesOrMovieMainParams = getMainReq.getBrowseType();
        return this.jdbcTemplate.queryForObject(getSeriesOrMovieMainQuery,
                (rs, rowNum) -> new GetMainRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl")),
                getSeriesOrMovieMainParams);
    }

    public int checkGenreName(GetGenreContentReq getGenreContentReq) {
        String checkGenreNameQuery = "select exists(select genreId from GenreContent where genreId in (select id from Genre where name = ?))";
        String checkGenreNameParams = getGenreContentReq.getGenre();
        return this.jdbcTemplate.queryForObject(checkGenreNameQuery,
                int.class,
                checkGenreNameParams);
    }

    public List<GetGenreMovieRes> getGenreMovies(GetGenreContentReq getGenreContentReq) {
        String getGenreMoviesQuery = "select id, title, ageRate, type, thumbnailUrl, previewUrl, runningTime, percentage " +
                "from Content where id in (select contentId from GenreContent where genreId in (select id from Genre where name = ?)) " +
                "and type = 'M'";
        String getGenreMoviesParams = getGenreContentReq.getGenre();
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

    public List<GetGenreSeriesRes> getGenreSeries(GetGenreContentReq getGenreContentReq) {
        String getGenreSeriesQuery = "select id, title, ageRate, type, thumbnailUrl, previewUrl, percentage, newEpisode " +
                "from Content where id in (select contentId from GenreContent where genreId in (select id from Genre where name = ?)) " +
                "and type = 'S'";
        String getGenreSeriesParams = getGenreContentReq.getGenre();
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
                "from Content order by releaseDate desc limit 7";
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
}
