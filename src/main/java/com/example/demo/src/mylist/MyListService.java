package com.example.demo.src.mylist;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.mylist.model.*;
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
public class MyListService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MyListDao myListDao;
    private final MyListProvider myListProvider;
    private final JwtService jwtService;

    @Autowired
    public MyListService(MyListDao myListDao, MyListProvider myListProvider, JwtService jwtService) {
        this.myListDao = myListDao;
        this.myListProvider = myListProvider;
        this.jwtService = jwtService;
    }


    @Transactional
    public long createPick(PostPickReq postPickReq) throws BaseException {
        int contentExists; // 존재하는 공개된 컨텐츠인지 체크
        try {
            contentExists = myListProvider.checkContent(postPickReq.getContentIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(CONTENT_NOT_EXISTS);
        }

        int hasPicked; // 이미 찜한 컨텐츠인지 체크
        try {
            hasPicked = myListProvider.checkPick(postPickReq.getProfileIdx(), postPickReq.getContentIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(hasPicked == 1) {
            throw new BaseException(DUPLICATED_PICK);
        }

        try {
            long pickIdx = myListDao.createPick(postPickReq);
            return pickIdx;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void deletePick(PatchPickReq patchPickReq) throws BaseException {
        int hasPicked;
        try {
            hasPicked = myListProvider.checkPick(patchPickReq.getProfileIdx(), patchPickReq.getContentIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(hasPicked == 0) {
            throw new BaseException(DELETE_FAIL_PICK);
        }

        try {
            int result = myListDao.deletePick(patchPickReq);
            if(result == 0) {
                throw new BaseException(DATABASE_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
