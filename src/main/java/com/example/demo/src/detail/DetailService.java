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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class DetailService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetailDao detailDao;
    private final DetailProvider detailProvider;
    private final JwtService jwtService;

    @Autowired
    public DetailService(DetailDao detailDao, DetailProvider detailProvider, JwtService jwtService) {
        this.detailDao = detailDao;
        this.detailProvider = detailProvider;
        this.jwtService = jwtService;
    }



}
