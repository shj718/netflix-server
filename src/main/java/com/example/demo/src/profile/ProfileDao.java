package com.example.demo.src.profile;

import com.example.demo.src.profile.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProfileDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public int checkProfile(PostProfileReq postProfileReq) {
        String checkProfileQuery = "select exists(select id from Profile where userId = ? and status = 'A')";
        long checkProfileParams = postProfileReq.getUserIdx();
        return this.jdbcTemplate.queryForObject(checkProfileQuery,
                int.class,
                checkProfileParams);
    }

    public int getProfileCount(PostProfileReq postProfileReq) {
        String getProfileCountQuery = "select count(id) from Profile where userId = ? and status = 'A'";
        long getProfileCountParams =postProfileReq.getUserIdx();
        return this.jdbcTemplate.queryForObject(getProfileCountQuery,
                int.class,
                getProfileCountParams);
    }

    public Long createProfile(PostProfileReq postProfileReq, String defaultProfileImage) {
        String createProfileQuery = "insert into Profile (userId, name, profileImgUrl) VALUES (?,?,?)";
        Object[] createProfileParams = new Object[]{postProfileReq.getUserIdx(), postProfileReq.getProfileName(), defaultProfileImage};
        this.jdbcTemplate.update(createProfileQuery,createProfileParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,long.class);
    }

    public GetProfileInfoRes getProfileInfo(long profileIdx) {
        String getProfileInfoQuery = "select id, userId, name, profileImgUrl, ageRate, lockPin, status from Profile where id = ? and status = 'A'";
        long getProfileInfoParams = profileIdx;
        return this.jdbcTemplate.queryForObject(getProfileInfoQuery,
                (rs, rowNum) -> new GetProfileInfoRes(
                        rs.getLong("id"),
                        rs.getLong("userId"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getLong("ageRate"),
                        rs.getString("lockPin"),
                        rs.getString("status")),
                getProfileInfoParams);
    }

    public List<GetProfileRes> getProfiles(long userIdx) {
        String getProfilesQuery = "select id, name, profileImgUrl, lockPin, status from Profile where userId = ? and status = 'A'";
        long getProfilesParams = userIdx;
        return this.jdbcTemplate.query(getProfilesQuery,
                (rs,rowNum) -> new GetProfileRes(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("lockPin"),
                        rs.getString("status")),
                getProfilesParams);
    }

    public int updateProfileLock(PatchLockPinReq patchLockPinReq) {
        String updateProfileLockQuery = "update Profile set lockPin = ? where id = ? and userId = ? and status = 'A'";
        Object[] updateProfileLockParams = new Object[]{patchLockPinReq.getProfileLockPin(), patchLockPinReq.getProfileIdx(),
        patchLockPinReq.getUserIdx()};

        return this.jdbcTemplate.update(updateProfileLockQuery, updateProfileLockParams);
    }

    public String checkProfileLock(long profileIdx) {
        String checkProfileLockQuery = "select lockPin from Profile where id = ? and status = 'A'";
        long checkProfileLockParams = profileIdx;
        return this.jdbcTemplate.queryForObject(checkProfileLockQuery, String.class, checkProfileLockParams);
    }

    public Profile getProfilePwd(PostProfileLogInReq postProfileLogInReq) {
        String getProfilePwdQuery = "select id, lockPin from Profile where id = ? and status = 'A'";
        long getProfilePwdParams = postProfileLogInReq.getProfileIdx();
        return this.jdbcTemplate.queryForObject(getProfilePwdQuery,
                (rs,rowNum) -> new Profile(
                        rs.getLong("id"),
                        rs.getString("lockPin")),
                getProfilePwdParams);
    }

    public int checkProfileExists(long profileIdx) {
        String checkProfileExistsQuery = "select exists(select id from Profile where id = ? and status = 'A')";
        long checkProfileExistsParams = profileIdx;

        return this.jdbcTemplate.queryForObject(checkProfileExistsQuery,
                int.class,
                checkProfileExistsParams);
    }

    public int modifyProfileName(PatchProfileNameReq patchProfileNameReq) {
        String modifyProfileNameQuery = "update Profile set name = ? where id = ? and userId = ? and status = 'A'";
        Object[] modifyProfileNameParams = new Object[]{patchProfileNameReq.getProfileName(), patchProfileNameReq.getProfileIdx(),
                patchProfileNameReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyProfileNameQuery, modifyProfileNameParams);
    }
}
