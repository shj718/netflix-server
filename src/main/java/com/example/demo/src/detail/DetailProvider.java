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
}
