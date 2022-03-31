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

    /**
     * 감독(크리에이터) 리스트 & 출연자 리스트 조회 API
     * [GET] /detail/directors-actors
     * @return BaseResponse<GetDirectorActorRes>
     */
    @ResponseBody
    @GetMapping("/directors-actors")
    public BaseResponse<GetDirectorActorRes> getDirectorsActors(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetDirectorActorRes getDirectorActorRes = detailProvider.getDirectorsActors(contentIdx);
            return new BaseResponse<>(getDirectorActorRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 장르 리스트 & 특징 리스트 조회 API
     * [GET] /detail/genres-features
     * @return BaseResponse<GetGenreFeatureRes>
     */
    @ResponseBody
    @GetMapping("/genres-features")
    public BaseResponse<GetGenreFeatureRes> getGenresFeatures(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetGenreFeatureRes getGenreFeatureRes = detailProvider.getGenresFeatures(contentIdx);
            return new BaseResponse<>(getGenreFeatureRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 시리즈 콘텐츠의 시즌 개수 조회 API
     * [GET] /detail/seasons-count
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @GetMapping("/seasons-count")
    public BaseResponse<Integer> getSeasonsCount(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            Integer seasonsCount = detailProvider.getSeasonsCount(contentIdx);
            return new BaseResponse<>(seasonsCount);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 예고편 및 다른 영상 조회 API
     * [GET] /detail/trailers
     * @return BaseResponse<List<GetTrailerRes>>
     */
    @ResponseBody
    @GetMapping("/trailers")
    public BaseResponse<List<GetTrailerRes>> getTrailers(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetTrailerRes> getTrailerRes = detailProvider.getTrailers(contentIdx);
            return new BaseResponse<>(getTrailerRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 시즌별 회차 정보 조회 API
     * [GET] /detail/episodes
     * @return BaseResponse<List<GetEpisodeRes>>
     */
    @ResponseBody
    @GetMapping("/episodes")
    public BaseResponse<List<GetEpisodeRes>> getEpisodes(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx, @RequestParam long seasonNumber) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetEpisodeRes> getEpisodeRes = detailProvider.getEpisodes(contentIdx, seasonNumber);
            return new BaseResponse<>(getEpisodeRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 비슷한 시리즈 조회 API (비슷한 콘텐츠 선정 기준: 콘텐츠 특징 리스트 Ex. 힐링, 긴장감 넘치는, 유쾌 발랄, 감정을 파고드는 등)
     * [GET] /detail/similar/series
     * @return BaseResponse<List<GetSimilarSeriesRes>>
     */
    @ResponseBody
    @GetMapping("/similar/series")
    public BaseResponse<List<GetSimilarSeriesRes>> getSimilarSeries(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetSimilarSeriesRes> getSimilarSeriesRes = detailProvider.getSimilarSeries(contentIdx);
            return new BaseResponse<>(getSimilarSeriesRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 비슷한 영화 조회 API (비슷한 콘텐츠 선정 기준: 콘텐츠 특징 리스트 Ex. 힐링, 긴장감 넘치는, 유쾌 발랄, 감정을 파고드는 등)
     * [GET] /detail/similar/movies
     * @return BaseResponse<List<GetSimilarSeriesRes>>
     */
    @ResponseBody
    @GetMapping("/similar/movies")
    public BaseResponse<List<GetSimilarMovieRes>> getSimilarMovies(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam long contentIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetSimilarMovieRes> getSimilarMovieRes = detailProvider.getSimilarMovies(contentIdx);
            return new BaseResponse<>(getSimilarMovieRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 배우의 출연작 조회 API
     * [GET] /detail/others/actor
     * @return BaseResponse<List<GetContentByActorRes>>
     */
    @ResponseBody
    @GetMapping("/others/actor")
    public BaseResponse<List<GetContentByActorRes>> getContentsByActor(@RequestParam long userIdx, @RequestParam long profileIdx, @RequestParam String actorName) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(actorName == null) {
                return new BaseResponse<>(EMPTY_ACTOR_NAME);
            }

            List<GetContentByActorRes> getContentByActorRes = detailProvider.getContentsByActor(actorName);
            return new BaseResponse<>(getContentByActorRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
