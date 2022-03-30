package com.example.demo.src.detail;

import com.example.demo.src.detail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class DetailDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public GetDetailRes getDetail(long contentIdx) {
        String getDetailQuery = "select id, title, ageRate, date_format(productionDate, '%Y') as productionYear, " +
                "summary, type, mainImgUrl, previewUrl, runningTime, percentage, logoImgUrl from Content where id = ?";
        long getDetailParams = contentIdx;
        return this.jdbcTemplate.queryForObject(getDetailQuery,
                (rs, rowNum) -> new GetDetailRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("productionYear"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("mainImgUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("logoImgUrl")),
                getDetailParams);
    }

    public GetDirectorActorRes getDirectorsActors(long contentIdx) {
        String getDirectorsActorsQuery = "select Directors.contentId, directorList, actorList " +
                "from (select contentId, group_concat(name) as directorList " +
                "      from (select contentId, name " +
                "            from Director " +
                "                     inner join (select directorId, contentId from DirectorContent where contentId = ?) ContentDirector " +
                "                                on directorId = id) Directors " +
                "      group by contentId) Directors " +
                "         inner join (select contentId, group_concat(name) as actorList " +
                "                     from (select contentId, name " +
                "                           from Actor " +
                "                                    inner join (select actorId, contentId from ActorContent where contentId = ?) ContentActor " +
                "                                               on actorId = id) Actors " +
                "                     group by contentId) Actors on Directors.contentId = Actors.contentId";
        Object[] getDirectorsActorsParams = new Object[]{contentIdx, contentIdx};
        return this.jdbcTemplate.queryForObject(getDirectorsActorsQuery,
                (rs, rowNum) -> new GetDirectorActorRes(
                        rs.getLong("contentId"),
                        rs.getString("directorList"),
                        rs.getString("actorList")),
                getDirectorsActorsParams);
    }

    public GetGenreFeatureRes getGenresFeatures(long contentIdx) {
        String getGenresFeaturesQuery = "select Genres.contentId, genreList, featureList " +
                "from (select contentId, group_concat(name) as genreList " +
                "      from (select contentId, name " +
                "            from Genre " +
                "                     inner join (select genreId, contentId from GenreContent where contentId = ?) ContentGenre " +
                "                                on genreId = id) Genres " +
                "      group by contentId) Genres " +
                "         inner join (select contentId, group_concat(name) as featureList " +
                "                     from (select contentId, name " +
                "                           from Feature " +
                "                                    inner join (select featureId, contentId from FeatureContent where contentId = ?) ContentFeature " +
                "                                               on featureId = id) Features " +
                "                     group by contentId) Features on Genres.contentId = Features.contentId";
        Object[] getGenresFeaturesParams = new Object[]{contentIdx, contentIdx};
        return this.jdbcTemplate.queryForObject(getGenresFeaturesQuery,
                (rs, rowNum) -> new GetGenreFeatureRes(
                        rs.getLong("contentId"),
                        rs.getString("genreList"),
                        rs.getString("featureList")),
                getGenresFeaturesParams);
    }

    public int checkContent(long contentIdx) { // 존재하는 컨텐츠인지 체크
        String checkContentQuery = "select exists (select id from Content where id = ?)";
        long checkContentParams = contentIdx;

        return this.jdbcTemplate.queryForObject(checkContentQuery,
                int.class,
                checkContentParams);
    }

    public int checkContentType(long contentIdx) {
        String checkContentTypeQuery = "select exists (select id from Content where id = ? and type = 'S')";
        long checkContentTypeParams = contentIdx;

        return this.jdbcTemplate.queryForObject(checkContentTypeQuery,
                int.class,
                checkContentTypeParams);
    }

    public int getSeasonsCount(long contentIdx) {
        String getSeasonsCountQuery = "select count(distinct seasonNum) as seasonsCount from Episode where contentId = ?";
        long getSeasonsCountParams = contentIdx;

        return this.jdbcTemplate.queryForObject(getSeasonsCountQuery,
                int.class,
                getSeasonsCountParams);
    }

    public int checkTrailer(long contentIdx) {
        String checkTrailerQuery = "select exists(select id from Trailer where contentId = ?)";
        long checkTrailerParams = contentIdx;

        return this.jdbcTemplate.queryForObject(checkTrailerQuery,
                int.class,
                checkTrailerParams);
    }

    public List<GetTrailerRes> getTrailers(long contentIdx) {
        String getTrailersQuery = "select id, title, thumbnailUrl, trailerUrl from Trailer where contentId = ?";
        long getTrailersParams = contentIdx;
        return this.jdbcTemplate.query(getTrailersQuery,
                (rs, rowNum) -> new GetTrailerRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("trailerUrl")),
                getTrailersParams);
    }

    public int checkSeason(long contentIdx, long seasonNumber) {
        String checkSeasonQuery = "select exists(select seasonNum from Episode where contentId = ? and seasonNum = ?)";
        Object[] checkSeasonParams = new Object[]{contentIdx, seasonNumber};

        return this.jdbcTemplate.queryForObject(checkSeasonQuery,
                int.class,
                checkSeasonParams);
    }

    public List<GetEpisodeRes> getEpisodes(long contentIdx, long seasonNumber) {
        String getEpisodesQuery = "select id, summary, runningTime, seasonNum, episodeNum, thumbnailUrl, episodeTitle, " +
                "if(releaseDate <= current_timestamp, '공개된 에피소드', date_format(releaseDate, '%c월 %e일')) as releaseDate " +
                "from Episode where contentId = ? and seasonNum = ?";
        Object[] getEpisodesParams = new Object[]{contentIdx, seasonNumber};
        return this.jdbcTemplate.query(getEpisodesQuery,
                (rs, rowNum) -> new GetEpisodeRes(
                        rs.getLong("id"),
                        rs.getString("summary"),
                        rs.getString("runningTime"),
                        rs.getLong("seasonNum"),
                        rs.getLong("episodeNum"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("episodeTitle"),
                        rs.getString("releaseDate")),
                getEpisodesParams);
    }

    public List<SimilarSeries> getSimilarSeries(long contentIdx) { // 시즌 개수를 제외한 나머지 정보
        String getSimilarSeriesQuery = "select id, title, ageRate, summary, type, thumbnailUrl, percentage, " +
                "       date_format(productionDate, '%Y') as productionYear " +
                "from Content " +
                "where type = 'S' " +
                "  and releaseDate <= current_timestamp " +
                "  and id in (select distinct contentId " +
                "             from FeatureContent " +
                "             where featureId in (select featureId from FeatureContent where contentId = ?) " +
                "               and contentId != ?)";
        Object[] getSimilarSeriesParams = new Object[]{contentIdx, contentIdx};
        return this.jdbcTemplate.query(getSimilarSeriesQuery,
                (rs, rowNum) -> new SimilarSeries(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("percentage"),
                        rs.getString("productionYear")),
                getSimilarSeriesParams);
    }

    public int checkContentMovie(long contentIdx) {
        String checkContentMovieQuery = "select exists (select id from Content where id = ? and type = 'M')";
        long checkContentParams = contentIdx;

        return this.jdbcTemplate.queryForObject(checkContentMovieQuery,
                int.class,
                checkContentParams);
    }

    public List<GetSimilarMovieRes> getSimilarMovies(long contentIdx) {
        String getSimilarMoviesQuery = "select id, title, ageRate, summary, type, thumbnailUrl, runningTime, percentage, " +
                "       date_format(productionDate, '%Y') as productionYear " +
                "from Content " +
                "where type = 'M' " +
                "  and releaseDate <= current_timestamp " +
                "  and id in (select distinct contentId " +
                "             from FeatureContent " +
                "             where featureId in (select featureId from FeatureContent where contentId = ?) " +
                "               and contentId != ?)";
        Object[] getSimilarMoviesParams = new Object[]{contentIdx, contentIdx};
        return this.jdbcTemplate.query(getSimilarMoviesQuery,
                (rs, rowNum) -> new GetSimilarMovieRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("productionYear")),
                getSimilarMoviesParams);
    }
}
