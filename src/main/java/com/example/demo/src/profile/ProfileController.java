package com.example.demo.src.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.profile.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProfileProvider profileProvider;
    @Autowired
    private final ProfileService profileService;
    @Autowired
    private final JwtService jwtService;


    public ProfileController(ProfileProvider profileProvider, ProfileService profileService, JwtService jwtService) {
        this.profileProvider = profileProvider;
        this.profileService = profileService;
        this.jwtService = jwtService;
    }


    /**
     * 프로필 생성 API (한 계정당 5개까지만 생성 가능)
     * [POST] /profile/manage
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("/manage")
    public BaseResponse<Long> createProfile(@RequestBody PostProfileReq postProfileReq) {
        try {
            long userIdx = postProfileReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Long profileIdx = profileService.createProfile(postProfileReq);
            return new BaseResponse<>(profileIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 프로필 정보 조회
     * [GET] /profile/info
     * @return BaseResponse<GetProfileInfoRes>
     */
    @ResponseBody
    @GetMapping("/info")
    public BaseResponse<GetProfileInfoRes> getProfileInfo(@RequestParam long userIdx, @RequestParam long profileIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetProfileInfoRes getProfileInfoRes = profileProvider.getProfileInfo(profileIdx);
            return new BaseResponse<>(getProfileInfoRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저의 전체 프로필 조회 API
     * [GET] /profile
     * @return BaseResponse<List<GetProfileRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetProfileRes>> getProfiles(@RequestParam long userIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetProfileRes> getProfilesRes = profileProvider.getProfiles(userIdx);
            return new BaseResponse<>(getProfilesRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로필 잠금 설정/해제 및 비밀번호 수정 API (요청값으로 "N"이나 4자리 숫자로 된 문자열만 가능)
     * [PATCH] /profile/manage
     * @return BaseResponse<Long>
     */
     @ResponseBody
     @PatchMapping("/manage")
     public BaseResponse<Long> updateProfileLock(@RequestBody PatchLockPinReq patchLockPinReq) {
        try {
            long userIdx = patchLockPinReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(patchLockPinReq.getProfileLockPin() == null) {
                return new BaseResponse<>(PATCH_EMPTY_PROFILE_LOCK);
            }
            // 프로필 잠금 PIN 번호 형식 검사
            boolean regexProfileLockPin = isRegexProfileLockPin(patchLockPinReq.getProfileLockPin());
            if(!regexProfileLockPin && !patchLockPinReq.getProfileLockPin().equals("N")) {
                return new BaseResponse<>(INVALID_PROFILE_LOCK_REQUEST);
            }
            profileService.updateProfileLock(patchLockPinReq);
            Long result = patchLockPinReq.getProfileIdx();
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로필 선택(프로필 로그인) API (요청값으로 4자리 숫자로 된 문자열만 가능)
     * [POST] /profile
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Long> profileLogIn(@RequestBody PostProfileLogInReq postProfileLogInReq) {
        try {
            long userIdx = postProfileLogInReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(postProfileLogInReq.getProfileLockPin() == null) {
                return new BaseResponse<>(EMPTY_PROFILE_LOCK_PIN);
            }
            // 프로필 잠금 PIN 번호 형식 검사
            boolean regexProfileLockPin = isRegexProfileLockPin(postProfileLogInReq.getProfileLockPin());
            if(!regexProfileLockPin) {
                return new BaseResponse<>(INVALID_PROFILE_LOCK_PIN);
            }
            long profileIdx = profileProvider.profileLogIn(postProfileLogInReq);
            return new BaseResponse<>(profileIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
