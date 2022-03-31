package com.example.demo.src.oauth;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.oauth.model.*;
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
public class OauthProvider {
    // TODO: 중복 이메일인지 체크 (아 여기다가 하면 여기에다가도 User 도메인에 있는 프로세스들 복붙해야하네)
    // TODO: 복붙하지말고, 이렇게 중복이메일이면, 자동으로 로그인된다. 그래서 서비스를 이용할 수 있다만 말하면 됨.

    private final OauthDao oauthDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OauthProvider(OauthDao oauthDao, JwtService jwtService) {
        this.oauthDao = oauthDao;
        this.jwtService = jwtService;
    }



    // 가입되어 있는 이메일인지 확인
    public int checkEmail(String email) throws BaseException {
        try{
            return oauthDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // User 테이블의 idx 가져오기
    public long getKakaoUserIdx(String email) throws BaseException {
        try {
            return oauthDao.getKakaoUserIdx(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkMembership(String email) throws BaseException {
        try {
            return oauthDao.checkMembership(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
