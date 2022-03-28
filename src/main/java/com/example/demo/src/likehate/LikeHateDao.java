package com.example.demo.src.likehate;

import com.example.demo.src.likehate.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LikeHateDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public int checkContent(long contentIdx) { // 존재하고 공개된 컨텐츠인지 체크
        String checkContentQuery = "select exists (select id from Content where id = ? and releaseDate <= current_timestamp)";
        long checkContentParams = contentIdx;

        return this.jdbcTemplate.queryForObject(checkContentQuery,
                int.class,
                checkContentParams);
    }

    public int checkLike(long profileIdx, long contentIdx) {
        String checkLikeQuery = "select exists(select profileId from Likes where profileId = ? and contentId = ? and status = 'A')";
        Object[] checkLikeParams = new Object[]{profileIdx, contentIdx};

        return this.jdbcTemplate.queryForObject(checkLikeQuery,
                int.class,
                checkLikeParams);
    }

    public long createLike(PostLikeReq postLikeReq) {
        String createLikeQuery = "insert into Likes (profileId, contentId) VALUES (?,?)";
        Object[] createLikeParams = new Object[]{postLikeReq.getProfileIdx(), postLikeReq.getContentIdx()};
        this.jdbcTemplate.update(createLikeQuery, createLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,long.class);
    }

    public int deleteLike(PatchLikeReq patchLikeReq) {
        String deleteLikeQuery = "update Likes set status = 'D' where profileId = ? and contentId = ? and status = 'A'";
        Object[] deleteLikeParams = new Object[]{patchLikeReq.getProfileIdx(), patchLikeReq.getContentIdx()};

        return this.jdbcTemplate.update(deleteLikeQuery, deleteLikeParams);
    }

    public int checkHate(long profileIdx, long contentIdx) {
        String checkHateQuery = "select exists(select profileId from Hates where profileId = ? and contentId = ? and status = 'A')";
        Object[] checkHateParams = new Object[]{profileIdx, contentIdx};

        return this.jdbcTemplate.queryForObject(checkHateQuery,
                int.class,
                checkHateParams);
    }

    public long createHate(PostHateReq postHateReq) {
        String createHateQuery = "insert into Hates (profileId, contentId) VALUES (?,?)";
        Object[] createHateParams = new Object[]{postHateReq.getProfileIdx(), postHateReq.getContentIdx()};
        this.jdbcTemplate.update(createHateQuery, createHateParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,long.class);
    }

    public int deleteHate(PatchHateReq patchHateReq) {
        String deleteHateQuery = "update Hates set status = 'D' where profileId = ? and contentId = ? and status = 'A'";
        Object[] deleteHateParams = new Object[]{patchHateReq.getProfileIdx(), patchHateReq.getContentIdx()};

        return this.jdbcTemplate.update(deleteHateQuery, deleteHateParams);
    }
}
