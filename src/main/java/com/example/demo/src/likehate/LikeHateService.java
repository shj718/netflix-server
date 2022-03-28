package com.example.demo.src.likehate;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.likehate.model.*;
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
public class LikeHateService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LikeHateDao likeHateDao;
    private final LikeHateProvider likeHateProvider;
    private final JwtService jwtService;

    @Autowired
    public LikeHateService(LikeHateDao likeHateDao, LikeHateProvider likeHateProvider, JwtService jwtService) {
        this.likeHateDao = likeHateDao;
        this.likeHateProvider = likeHateProvider;
        this.jwtService = jwtService;
    }


    @Transactional
    public long createLike(PostLikeReq postLikeReq) throws BaseException {
        int contentExists; // 컨텐츠 존재, 공개 여부 체크
        try {
            contentExists = likeHateProvider.checkContent(postLikeReq.getContentIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(CONTENT_NOT_EXISTS);
        }

        int hasLiked; // 이미 좋아요한 컨텐츠인지 체크
        try {
            hasLiked = likeHateProvider.checkLike(postLikeReq.getProfileIdx(), postLikeReq.getContentIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(hasLiked == 1) {
            throw new BaseException(DUPLICATED_LIKE);
        }

        try {
            long likeIdx = likeHateDao.createLike(postLikeReq);
            return likeIdx;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void deleteLike(PatchLikeReq patchLikeReq) throws BaseException {
        int hasLiked;
        try {
            hasLiked = likeHateProvider.checkLike(patchLikeReq.getProfileIdx(), patchLikeReq.getContentIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(hasLiked == 0) {
            throw new BaseException(DELETE_FAIL_LIKE);
        }

        try {
            int result = likeHateDao.deleteLike(patchLikeReq);
            if(result == 0) {
                throw new BaseException(DATABASE_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public long createHate(PostHateReq postHateReq) throws BaseException {
        try {
            int contentExists; // 컨텐츠 존재, 공개 여부 체크
            try {
                contentExists = likeHateProvider.checkContent(postHateReq.getContentIdx());
            } catch(Exception exception){
                throw new BaseException(DATABASE_ERROR);
            }
            if(contentExists == 0) {
                throw new BaseException(CONTENT_NOT_EXISTS);
            }

            int hasHated; // 이미 싫어요한 컨텐츠인지 체크
            try {
                hasHated = likeHateProvider.checkHate(postHateReq.getProfileIdx(), postHateReq.getContentIdx());
            } catch(Exception exception){
                throw new BaseException(DATABASE_ERROR);
            }
            if(hasHated == 1) {
                throw new BaseException(DUPLICATED_HATE);
            }

            try {
                long hateIdx = likeHateDao.createHate(postHateReq);
                return hateIdx;
            } catch(Exception exception){
                throw new BaseException(DATABASE_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void deleteHate(PatchHateReq patchHateReq) throws BaseException {
        int hasHated;
        try {
            hasHated = likeHateProvider.checkHate(patchHateReq.getProfileIdx(), patchHateReq.getContentIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(hasHated == 0) {
            throw new BaseException(DELETE_FAIL_HATE);
        }

        try {
            int result = likeHateDao.deleteHate(patchHateReq);
            if(result == 0) {
                throw new BaseException(DATABASE_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
