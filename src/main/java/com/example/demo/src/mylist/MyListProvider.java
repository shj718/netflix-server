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

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class MyListProvider {

    private final MyListDao myListDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MyListProvider(MyListDao myListDao, JwtService jwtService) {
        this.myListDao = myListDao;
        this.jwtService = jwtService;
    }



    public int checkPick(long profileIdx, long contentIdx) throws BaseException {
        try {
            return myListDao.checkPick(profileIdx, contentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkContent(long contentIdx) throws BaseException {
        try {
            return myListDao.checkContent(contentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetMyListRes> getMyList(long profileIdx) throws BaseException {
        try {
            List<GetMyListRes> getMyListRes = myListDao.getMyList(profileIdx);
            return getMyListRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
