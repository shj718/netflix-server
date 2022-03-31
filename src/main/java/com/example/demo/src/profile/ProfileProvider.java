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

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class ProfileProvider {

    private final ProfileDao profileDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProfileProvider(ProfileDao profileDao, JwtService jwtService) {
        this.profileDao = profileDao;
        this.jwtService = jwtService;
    }



    public int checkProfile(PostProfileReq postProfileReq) throws BaseException {
        try {
            int hasProfile = profileDao.checkProfile(postProfileReq);
            return hasProfile;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getProfileCount(PostProfileReq postProfileReq) throws BaseException {
        try {
            int profileCount = profileDao.getProfileCount(postProfileReq);
            return profileCount;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProfileInfoRes getProfileInfo(long profileIdx) throws BaseException {
        int profileExists;
        try {
            profileExists = checkProfileExists(profileIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if(profileExists == 0) {
            throw new BaseException(PROFILE_NOT_EXISTS);
        }

        try {
            GetProfileInfoRes getProfileInfoRes = profileDao.getProfileInfo(profileIdx);
            return getProfileInfoRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetProfileRes> getProfiles(long userIdx) throws BaseException {
        try {
            List<GetProfileRes> getProfilesRes = profileDao.getProfiles(userIdx);
            return getProfilesRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean checkProfileLock(long profileIdx) throws BaseException {
        try {
            String lockPin = profileDao.checkProfileLock(profileIdx);
            if(lockPin.equals("N")) {
                return false;
            }
            else return true;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public long profileLogIn(PostProfileLogInReq postProfileLogInReq) throws BaseException {
        // 잠금 걸려있는 프로필인지부터 확인
        boolean isProfileLocked = checkProfileLock(postProfileLogInReq.getProfileIdx());
        if(!isProfileLocked) {
            throw new BaseException(PROFILE_LOGIN_NOT_LOCKED);
        }

        try {
            // 존재하는 profile Idx 인지 검사
            Profile profile = profileDao.getProfilePwd(postProfileLogInReq);
        } catch(Exception exception){
            throw new BaseException(PROFILE_NOT_EXISTS);
        }
        // 비밀 번호 비교
        Profile profile = profileDao.getProfilePwd(postProfileLogInReq);
        String lockPin = profile.getProfileLockPin();
        if(postProfileLogInReq.getProfileLockPin().equals(lockPin)) {
            return profile.getProfileIdx();
        }
        else {
            throw new BaseException(FAILED_TO_PROFILE_LOGIN);
        }
    }

    public int checkProfileExists(long profileIdx) throws BaseException {
        try {
            return profileDao.checkProfileExists(profileIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
