package com.example.demo.src.browse;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.browse.model.*;
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
public class BrowseService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BrowseDao browseDao;
    private final BrowseProvider browseProvider;
    private final JwtService jwtService;

    @Autowired
    public BrowseService(BrowseDao browseDao, BrowseProvider browseProvider, JwtService jwtService) {
        this.browseDao = browseDao;
        this.browseProvider = browseProvider;
        this.jwtService = jwtService;
    }



}
