package com.example.demo.src.detail;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.detail.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class DetailProvider {

    private final DetailDao detailDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public DetailProvider(DetailDao detailDao, JwtService jwtService) {
        this.detailDao = detailDao;
        this.jwtService = jwtService;
    }



    public GetDetailRes getDetail(long contentIdx) throws BaseException {
        try {
            GetDetailRes getDetailRes = detailDao.getDetail(contentIdx);
            return getDetailRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkContent(long contentIdx) throws BaseException {
        try {
            return detailDao.checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkContentType(long contentIdx) throws BaseException {
        try {
            return detailDao.checkContentType(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetDirectorActorRes getDirectorsActors(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXIST);
        }

        try {
            GetDirectorActorRes getDirectorActorRes = detailDao.getDirectorsActors(contentIdx);
            return getDirectorActorRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetGenreFeatureRes getGenresFeatures(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXIST);
        }

        try {
            GetGenreFeatureRes getGenreFeatureRes = detailDao.getGenresFeatures(contentIdx);
            return getGenreFeatureRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getSeasonsCount(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXIST);
        }

        // 해당 컨텐츠가 시리즈물인지 체크
        int isSeries;
        try{
            isSeries = checkContentType(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(isSeries == 0) {
            throw new BaseException(DETAIL_INVALID_CONTENT_TYPE);
        }

        try {
            int seasonsCount = detailDao.getSeasonsCount(contentIdx);
            return seasonsCount;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
