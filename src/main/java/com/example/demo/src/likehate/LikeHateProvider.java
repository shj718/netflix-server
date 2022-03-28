package com.example.demo.src.likehate;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.likehate.model.*;
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
public class LikeHateProvider {

    private final LikeHateDao likeHateDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public LikeHateProvider(LikeHateDao likeHateDao, JwtService jwtService) {
        this.likeHateDao = likeHateDao;
        this.jwtService = jwtService;
    }



    public int checkContent(long contentIdx) throws BaseException {
        try {
            return likeHateDao.checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkLike(long profileIdx, long contentIdx) throws BaseException {
        try {
            return likeHateDao.checkLike(profileIdx, contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkHate(long profileIdx, long contentIdx) throws BaseException {
        try {
            return likeHateDao.checkHate(profileIdx, contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
