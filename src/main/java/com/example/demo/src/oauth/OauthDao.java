package com.example.demo.src.oauth;

import com.example.demo.src.oauth.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class OauthDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    // 이메일 중복 체크
    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    // 카카오 소셜로그인한 유저 자동 회원가입
    public long createKakaoUser(String email, long id) {
        String createKakaoUserQuery = "insert into User (email, password) VALUES (?,?)";
        Object[] createKakaoUserParams = new Object[]{email, id};

        this.jdbcTemplate.update(createKakaoUserQuery, createKakaoUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,long.class);
    }

    // 카카오 유저의 User 테이블의 id 가져오기
    public long getKakaoUserIdx(String email) {
        String getKakaoUserIdxQuery = "select id from User where email = ? and status = 'A'";
        String getKakaoUserParams = email;

        return this.jdbcTemplate.queryForObject(getKakaoUserIdxQuery,
                long.class,
                getKakaoUserParams);
    }

    // 이 유저가 status 가 'A'인 멤버십을 갖고 있는지 확인
    public int checkMembership(String email) {
        String checkMembershipQuery = "select exists(select id from Membership where id in (select membershipId from User where email = ?) " +
                "and status = 'A')";
        String checkMembershipParams = email;

        return this.jdbcTemplate.queryForObject(checkMembershipQuery,
                int.class,
                checkMembershipParams);
    }
}
