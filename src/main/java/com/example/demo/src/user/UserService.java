package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }


    public Membership setMembership(String membershipType) {
        String videoQuality;
        long price;
        long accessLimit;
        if(membershipType.equals("B")) {
            videoQuality = "480p";
            price = 9500;
            accessLimit = 1;
        }
        else if(membershipType.equals("S")) {
            videoQuality = "1080p";
            price = 13500;
            accessLimit = 2;
        }
        else {
            videoQuality = "4K+HDR";
            price = 17000;
            accessLimit = 4;
        }
        return new Membership(videoQuality, price, accessLimit);
    }

    @Transactional
    public long createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            long userIdx = userDao.createUser(postUserReq); // 유저 생성

            return userIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public long createPayment(PostPaymentReq postPaymentReq) throws BaseException {
        try {
            String type;
            type = postPaymentReq.getMembershipType();

            Membership membership = new Membership(setMembership(type).getVideoQuality(), setMembership(type).getPrice(),
                    setMembership(type).getAccessLimit()); // 멤버십 정보 세팅하기

            long membershipIdx = userDao.createMembership(postPaymentReq.getMembershipType(), membership); // 멤버십 생성
            long userIdx = postPaymentReq.getUserIdx();
            int membershipResult = userDao.updateUserMembership(userIdx, membershipIdx); // 회원가입한 유저의 User 테이블에 멤버십 idx 넣기
            if(membershipResult == 0) {
                throw new BaseException(DATABASE_ERROR);
            }

            int result = userDao.createPayment(postPaymentReq);
            if(result == 0) {
                throw new BaseException(DATABASE_ERROR);
            }
            return membershipIdx;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public long createMembership(PostMembershipReq postMembershipReq) throws BaseException {
        try {
            String type;
            type = postMembershipReq.getMembershipType();

            Membership membership = new Membership(setMembership(type).getVideoQuality(), setMembership(type).getPrice(),
                    setMembership(type).getAccessLimit()); // 멤버십 정보 세팅하기

            // 멤버십 생성
            long membershipIdx = userDao.createMembership(postMembershipReq.getMembershipType(), membership);

            // 유저의 User 테이블에 멤버십 Idx 넣어주기
            int membershipResult = userDao.updateUserMembership(postMembershipReq.getUserIdx(), membershipIdx);
            if(membershipResult == 0) {
                throw new BaseException(DATABASE_ERROR);
            }

            // 결제
            PostPaymentReq postPaymentReq = new PostPaymentReq(postMembershipReq.getUserIdx(), postMembershipReq.getCardNumber(),
                    postMembershipReq.getName(), postMembershipReq.getMembershipType());
            int paymentResult = userDao.createPayment(postPaymentReq);
            if(paymentResult == 0) {
                throw new BaseException(DATABASE_ERROR);
            }

            return membershipIdx;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
