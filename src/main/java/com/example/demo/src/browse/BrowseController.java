package com.example.demo.src.browse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.browse.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/browse")
@CrossOrigin(origins = "http://localhost:3000")
public class BrowseController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final BrowseProvider browseProvider;
    @Autowired
    private final BrowseService browseService;
    @Autowired
    private final JwtService jwtService;


    public BrowseController(BrowseProvider browseProvider, BrowseService browseService, JwtService jwtService) {
        this.browseProvider = browseProvider;
        this.browseService = browseService;
        this.jwtService = jwtService;
    }


    /**
     * 메인 콘텐츠(화면 상단) 조회 API
     * [GET] /browse/main
     * @return BaseResponse<GetMainRes>
     */
    @ResponseBody
    @GetMapping("/main")
    public BaseResponse<GetMainRes> getMain(@RequestBody GetMainReq getMainReq) {
        try {
            long userIdx = getMainReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // Browse Type 형식적 밸리데이션
            if(getMainReq.getBrowseType() == null) {
                return new BaseResponse<>(EMPTY_BROWSE_TYPE);
            }
            if(!isRegexBrowseType(getMainReq.getBrowseType()) || getMainReq.getBrowseType().length() != 1) {
                return new BaseResponse<>(INVALID_BROWSE_TYPE);
            }
            GetMainRes getMainRes = browseProvider.getMain(getMainReq);
            return new BaseResponse<>(getMainRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 장르 콘텐츠 조회 (영화) API
     * [GET] /browse/genre/movie
     * @return BaseResponse<List<GetGenreMovieRes>>
     */
    @ResponseBody
    @GetMapping("/genre/movie")
    public BaseResponse<List<GetGenreMovieRes>> getGenreMovies(@RequestBody GetGenreContentReq getGenreContentReq) {
        try {
            long userIdx = getGenreContentReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(getGenreContentReq.getGenre() == null) {
                return new BaseResponse<>(EMPTY_GENRE_REQUEST);
            }
            List<GetGenreMovieRes> getGenreMoviesRes = browseProvider.getGenreMovies(getGenreContentReq);
            return new BaseResponse<>(getGenreMoviesRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 장르 콘텐츠 조회 (시리즈) API
     * [GET] /browse/genre/series
     * @return BaseResponse<List<GetGenreSeriesRes>>
     */
    @ResponseBody
    @GetMapping("/genre/series")
    public BaseResponse<List<GetGenreSeriesRes>> getGenreSeries(@RequestBody GetGenreContentReq getGenreContentReq) {
        try {
            long userIdx = getGenreContentReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(getGenreContentReq.getGenre() == null) {
                return new BaseResponse<>(EMPTY_GENRE_REQUEST);
            }
            List<GetGenreSeriesRes> getGenreSeriesRes = browseProvider.getGenreSeries(getGenreContentReq);
            return new BaseResponse<>(getGenreSeriesRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * TOP 10 컨텐츠 조회 (기준: 좋아요 개수)
     * [GET] /browse/top-ten
     * @return BaseResponse<List<GetTopTenRes>>
     */
    @ResponseBody
    @GetMapping("/top-ten")
    public BaseResponse<List<GetTopTenRes>> getTopTen(@RequestParam long userIdx, @RequestParam long profileIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetTopTenRes> getTopTensRes = browseProvider.getTopTen();
            return new BaseResponse<>(getTopTensRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 새로운 콘텐츠 조회 API (최신 7개)
     * [GET] /browse/latest
     * @return BaseResponse<List<GetLatestRes>>
     */
    @ResponseBody
    @GetMapping("/latest")
    public BaseResponse<List<GetLatestRes>> getLatest(@RequestParam long userIdx, @RequestParam long profileIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetLatestRes> getLatestRes = browseProvider.getLatest();
            return new BaseResponse<>(getLatestRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공개 예정 콘텐츠 조회 API
     * [GET] /browse/soon
     * @return BaseResponse<List<GetSoonRes>>
     */
    @ResponseBody
    @GetMapping("/soon")
    public BaseResponse<List<GetSoonRes>> getSoon(@RequestParam long userIdx, @RequestParam long profileIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetSoonRes> getSoonRes = browseProvider.getSoon();
            return new BaseResponse<>(getSoonRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
