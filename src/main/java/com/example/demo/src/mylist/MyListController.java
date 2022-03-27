package com.example.demo.src.mylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mylist.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexMembership;

@RestController
@RequestMapping("/my-list")
@CrossOrigin(origins = "http://localhost:3000")
public class MyListController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MyListProvider myListProvider;
    @Autowired
    private final MyListService myListService;
    @Autowired
    private final JwtService jwtService;


    public MyListController(MyListProvider myListProvider, MyListService myListService, JwtService jwtService) {
        this.myListProvider = myListProvider;
        this.myListService = myListService;
        this.jwtService = jwtService;
    }


    /**
     * 콘텐츠 찜하기 생성 API
     * [POST] /my-list
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Long> createPick(@RequestBody PostPickReq postPickReq) {
        try {
            long userIdx = postPickReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Long pickIdx = myListService.createPick(postPickReq);
            return new BaseResponse<>(pickIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 콘텐츠 찜 여부 조회 API
     * [GET] /my-list/check
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @GetMapping("/check")
    public BaseResponse<Integer> checkContentPick(@RequestBody GetPickCheckReq getPickCheckReq) {
        try {
            long userIdx = getPickCheckReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Integer hasPicked = myListProvider.checkPick(getPickCheckReq.getProfileIdx(), getPickCheckReq.getContentIdx());
            return new BaseResponse<>(hasPicked);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 찜한 콘텐츠 전체 조회 API
     * [GET] /my-list
     * @return BaseResponse<List<GetMyListRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetMyListRes>> getMyList(@RequestParam long userIdx, @RequestParam long profileIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetMyListRes> getMyListRes = myListProvider.getMyList(profileIdx);
            return new BaseResponse<>(getMyListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 콘텐츠 찜하기 삭제 API
     * [PATCH] /my-list
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<String> deletePick(@RequestBody PatchPickReq patchPickReq) {
        try {
            long userIdx = patchPickReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            myListService.deletePick(patchPickReq);
            String result = "콘텐츠 찜하기 삭제 성공";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
