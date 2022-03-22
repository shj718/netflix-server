package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    
    public GetMembershipRes getMembership(long membershipIdx) { // 멤버십 정보
        String getMembershipQuery = "select id, type, videoQuality, price, accessLimit, date_format(startTime, '%c월 %Y'), " +
                "date_format(endTime, '%Y년 %c월 %e일'), status from Membership where id = ?";
        long getMembershipParams = membershipIdx;
        return this.jdbcTemplate.queryForObject(getMembershipQuery,
                (rs, rowNum) -> new GetMembershipRes(
                        rs.getLong("id"),
                        rs.getString("type"),
                        rs.getString("videoQuality"),
                        rs.getLong("price"),
                        rs.getLong("accessLimit"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getString("status")),
                getMembershipParams);
    }

    public GetUserInfoRes getUserInfo(long userIdx) {
        String getUserInfoQuery = "select id, email, password, payment, membershipId, name from User where id = ?";
        long getUserInfoParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs, rowNum) -> new GetUserInfoRes(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("payment"),
                        rs.getLong("membershipId"),
                        rs.getString("name")),
                getUserInfoParams);
    }

    public long createUser(PostUserReq postUserReq){ // 유저 생성
        String createUserQuery = "insert into User (email, password) VALUES (?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(), postUserReq.getPassword()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,long.class);
    }

    public int checkEmail(String email){ // 이메일 중복 체크
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkMembership(PostLoginReq postLoginReq) { // 유저가 status 가 'A'인 멤버십을 갖고 있는지 체크
        String checkMembershipQuery = "select exists(select id from Membership where id in (select membershipId from User where email = ?) " +
                "and status = 'A')";
        String checkMembershipParams = postLoginReq.getEmail();
        return this.jdbcTemplate.queryForObject(checkMembershipQuery,
                int.class,
                checkMembershipParams);
    }

    public long createMembership(String membershipType, Membership membership) { // 멤버십 생성
        String createMembershipQuery = "insert into Membership (type, videoQuality, price, accessLimit) VALUES (?,?,?,?)";
        Object[] createMembershipParams = new Object[]{membershipType,membership.getVideoQuality(),
                membership.getPrice(), membership.getAccessLimit()};
        this.jdbcTemplate.update(createMembershipQuery,createMembershipParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,long.class);

    }

    public int updateUserMembership(long userIdx, long membershipIdx) { // 유저 테이블에 멤버십 정보 반영
        String updateUserMembershipQuery = "update User set membershipId = ? where id = ?";
        Object[] updateUserMembershipParams = new Object[]{membershipIdx, userIdx};

        return this.jdbcTemplate.update(updateUserMembershipQuery,updateUserMembershipParams);
    }

    public int createPayment(PostPaymentReq postPaymentReq) {
        String createPaymentQuery = "update User set payment = ?, name = ? where id = ?";
        Object[] createPaymentParams = new Object[]{postPaymentReq.getCardNumber(), postPaymentReq.getName(),
        postPaymentReq.getUserIdx()};

        return this.jdbcTemplate.update(createPaymentQuery, createPaymentParams);
    }

    public User getPwd(PostLoginReq postLoginReq){ // 비밀 번호를 포함한 유저 개인 정보
        String getPwdQuery = "select id, password, email from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getLong("userIdx"),
                        rs.getString("password"),
                        rs.getString("email")),
                getPwdParams);

    }



}
