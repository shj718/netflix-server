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
}
