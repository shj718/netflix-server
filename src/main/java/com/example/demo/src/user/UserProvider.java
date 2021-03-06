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

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }



    public int checkEmail(String email) throws BaseException{ // 중복 이메일 체크
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetMembershipRes getMembership(long membershipIdx) throws BaseException {
        try {
            // TODO: 진짜 이 유저가 가진 membershipIdx가 맞는지 확인하는 코드 추가하기
            GetMembershipRes getMembershipRes = userDao.getMembership(membershipIdx);
            return getMembershipRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        // 존재하지 않는 이메일인 경우 예외 처리
        try {
            User user = userDao.getPwd(postLoginReq);
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_NOT_EXISTS);
        }
        User user = userDao.getPwd(postLoginReq);
        String password;
        try { // 디비에 저장된 비밀번호 복호화
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }
        // 비밀번호 비교
        if(postLoginReq.getPassword().equals(password)){
            long userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx);

            // 멤버십 소유 여부 가져오기
            int hasMembership = userDao.checkMembership(postLoginReq);

            return new PostLoginRes(userIdx, jwt, hasMembership);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public GetUserInfoRes getUserInfo(long userIdx) throws BaseException {
        try {
            GetUserInfoRes getUserInfoRes = userDao.getUserInfo(userIdx);
            // 비밀번호를 그대로 주는 것이 아니라 복호화해서 주기
            String password;
            try { // 디비에 저장된 비밀번호 복호화
                password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(getUserInfoRes.getPassword());
            } catch (Exception ignored) {
                throw new BaseException(PASSWORD_DECRYPTION_ERROR);
            }
            getUserInfoRes.setPassword(password);
            return getUserInfoRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
