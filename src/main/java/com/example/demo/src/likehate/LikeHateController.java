package com.example.demo.src.likehate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.likehate.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexMembership;

@RestController
@RequestMapping("/likehate")
@CrossOrigin(origins = "http://localhost:3000")
public class LikeHateController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final LikeHateProvider likeHateProvider;
    @Autowired
    private final LikeHateService likeHateService;
    @Autowired
    private final JwtService jwtService;


    public LikeHateController(LikeHateProvider likeHateProvider, LikeHateService likeHateService, JwtService jwtService) {
        this.likeHateProvider = likeHateProvider;
        this.likeHateService = likeHateService;
        this.jwtService = jwtService;
    }


    /**
     * 콘텐츠 좋아요 생성 API
     * [POST] /likehate/likes
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("/likes")
    public BaseResponse<Long> createLike(@RequestBody PostLikeReq postLikeReq) {
        try {
            long userIdx = postLikeReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Long likeIdx = likeHateService.createLike(postLikeReq);
            return new BaseResponse<>(likeIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 콘텐츠 좋아요 조회 API
     * [GET] /likehate/likes
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @GetMapping("/likes")
    public BaseResponse<Integer> checkLike(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Integer hasLiked = likeHateProvider.checkLike(profileIdx, contentIdx);
            return new BaseResponse<>(hasLiked);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 콘텐츠 좋아요 삭제 API
     * [PATCH] /likehate/likes
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/likes")
    public BaseResponse<String> deleteLike(@RequestBody PatchLikeReq patchLikeReq) {
        try {
            long userIdx = patchLikeReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            likeHateService.deleteLike(patchLikeReq);
            String result = "콘텐츠 좋아요 삭제 성공";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 콘텐츠 싫어요 생성 API
     * [POST] /likehate/hates
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("/hates")
    public BaseResponse<Long> createHate(@RequestBody PostHateReq postHateReq) {
        try {
            long userIdx = postHateReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Long hateIdx = likeHateService.createHate(postHateReq);
            return new BaseResponse<>(hateIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 콘텐츠 싫어요 조회 API
     * [GET] /likehate/hates
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @GetMapping("/hates")
    public BaseResponse<Integer> checkHate(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int hasHated = likeHateProvider.checkHate(profileIdx, contentIdx);
            return new BaseResponse<>(hasHated);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 콘텐츠 싫어요 삭제 API
     * [PATCH] /likehate/hates
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/hates")
    public BaseResponse<String> deleteHate(@RequestBody PatchHateReq patchHateReq) {
        try {
            long userIdx = patchHateReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            likeHateService.deleteHate(patchHateReq);
            String result = "콘텐츠 싫어요 삭제 성공";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
