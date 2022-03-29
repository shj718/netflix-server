package com.example.demo.src.detail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.detail.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexMembership;

@RestController
@RequestMapping("/detail")
@CrossOrigin(origins = "http://localhost:3000")
public class DetailController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final DetailProvider detailProvider;
    @Autowired
    private final DetailService detailService;
    @Autowired
    private final JwtService jwtService;


    public DetailController(DetailProvider detailProvider, DetailService detailService, JwtService jwtService) {
        this.detailProvider = detailProvider;
        this.detailService = detailService;
        this.jwtService = jwtService;
    }

    /**
     * 콘텐츠 기본 상세 정보 조회 API
     * [GET] /detail
     * @return BaseResponse<GetDetailRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetDetailRes> getDetail(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetDetailRes getDetailRes = detailProvider.getDetail(contentIdx);
            return new BaseResponse<>(getDetailRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
