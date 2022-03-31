package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.profile.model.*;
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
public class ProfileService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProfileDao profileDao;
    private final ProfileProvider profileProvider;
    private final JwtService jwtService;

    @Autowired
    public ProfileService(ProfileDao profileDao, ProfileProvider profileProvider, JwtService jwtService) {
        this.profileDao = profileDao;
        this.profileProvider = profileProvider;
        this.jwtService = jwtService;
    }


    @Transactional
    public Long createProfile(PostProfileReq postProfileReq) throws BaseException {
        int hasProfile;
        try {
            hasProfile = profileProvider.checkProfile(postProfileReq);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        try {
            if(hasProfile == 1) {
                // 프로필 개수가 5개를 넘지 않는지 밸리데이션 처리
                int profileCount = profileProvider.getProfileCount(postProfileReq);
                if(profileCount == 5) {
                    throw new BaseException(EXCEEDED_PROFILES_COUNT);
                }
            }
        } catch(Exception exception){
            throw new BaseException(EXCEEDED_PROFILES_COUNT);
        }

        try {
            // 프로필 이미지 Url 은 등록시에는 기본 이미지로 세팅됨
            String defaultProfileImage = "https://occ-0-325-993.1.nflxso.net/dnm/api/v6/K6hjPJd6cR6FpVELC5Pd6ovHRSk/AAAABWu33TcylnaLZwSdtgKR6mr0O63afqQLxZbzHYQZLkCJ9bgMTtsf6tzs_ua2BuTpAVPbhxnroiEA-_bqJmKWiXblO9h-.png?r=f71";
            Long profileIdx = profileDao.createProfile(postProfileReq, defaultProfileImage);
            return profileIdx;

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void updateProfileLock(PatchLockPinReq patchLockPinReq) throws BaseException {
        // 존재하는 프로필인지 확인
        int profileExists;
        try {
            profileExists = profileProvider.checkProfileExists(patchLockPinReq.getProfileIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(profileExists == 0) {
            throw new BaseException(PROFILE_NOT_EXISTS);
        }

        try {
            int result = profileDao.updateProfileLock(patchLockPinReq);
            if(result == 0) {
                throw new BaseException(DATABASE_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyProfileName(PatchProfileNameReq patchProfileNameReq) throws BaseException {
        int profileExists;
        try {
            profileExists = profileProvider.checkProfileExists(patchProfileNameReq.getProfileIdx());
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(profileExists == 0) {
            throw new BaseException(PROFILE_NOT_EXISTS);
        }

        try {
            int result = profileDao.modifyProfileName(patchProfileNameReq);
            if(result == 0) {
                throw new BaseException(DATABASE_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
