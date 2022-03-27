package com.example.demo.src.mylist;

import com.example.demo.src.mylist.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MyListDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public int checkPick(long profileIdx, long contentIdx) { // 해당 콘텐츠에 해당 프로필이 찜했는지 체크
        String checkPickQuery = "select exists(select profileId from Pick where profileId = ? and contentId = ? and status = 'A')";
        Object[] checkPickParams = new Object[]{profileIdx, contentIdx};
        return this.jdbcTemplate.queryForObject(checkPickQuery,
                int.class,
                checkPickParams);
    }

    public long createPick(PostPickReq postPickReq) {
        String createPickQuery = "insert into Pick (profileId, contentId) VALUES (?,?)";
        Object[] createPickParams = new Object[]{postPickReq.getProfileIdx(), postPickReq.getContentIdx()};
        this.jdbcTemplate.update(createPickQuery, createPickParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,long.class);
    }

    public List<GetMyListRes> getMyList(long profileIdx) {
        String getMyListQuery = "select ifnull(id,0) as id, ifnull(title,'없음') as title, ifnull(ageRate,0) as ageRate, ifnull(type,'없음') as type, " +
                "ifnull(thumbnailUrl,'없음') as thumbnailUrl, ifnull(previewUrl,'없음') as previewUrl, ifnull(runningTime,'없음') as runningTime, " +
                "ifnull(percentage,'없음') as percentage, ifnull(newEpisode,'없음') as newEpisode " +
                "from Content inner join (select contentId, createdAt from Pick where profileId = ? and status = 'A') Picks " +
                "on Picks.contentId = Content.id order by Picks.createdAt desc";
        long getMyListParams = profileIdx;
        return this.jdbcTemplate.query(getMyListQuery,
                (rs, rowNum) -> new GetMyListRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("type"),
                        rs.getString("thumbnailUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("newEpisode")),
                getMyListParams);
    }

    public int deletePick(PatchPickReq patchPickReq) {
        String deletePickQuery = "update Pick set status = 'D' where profileId = ? and contentId = ? and status = 'A'";
        Object[] deletePickParams = new Object[]{patchPickReq.getProfileIdx(), patchPickReq.getContentIdx()};

        return this.jdbcTemplate.update(deletePickQuery, deletePickParams);
    }
}
