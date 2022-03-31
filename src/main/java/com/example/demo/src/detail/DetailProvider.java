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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class DetailProvider {

    private final DetailDao detailDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public DetailProvider(DetailDao detailDao, JwtService jwtService) {
        this.detailDao = detailDao;
        this.jwtService = jwtService;
    }



    public GetDetailRes getDetail(long contentIdx) throws BaseException {
        try {
            GetDetailRes getDetailRes = detailDao.getDetail(contentIdx);
            return getDetailRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkContent(long contentIdx) throws BaseException {
        try {
            return detailDao.checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkContentType(long contentIdx) throws BaseException {
        try {
            return detailDao.checkContentType(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetDirectorActorRes getDirectorsActors(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXISTS);
        }

        try {
            GetDirectorActorRes getDirectorActorRes = detailDao.getDirectorsActors(contentIdx);
            return getDirectorActorRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetGenreFeatureRes getGenresFeatures(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXISTS);
        }

        try {
            GetGenreFeatureRes getGenreFeatureRes = detailDao.getGenresFeatures(contentIdx);
            return getGenreFeatureRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getSeasonsCount(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXISTS);
        }

        // 해당 컨텐츠가 시리즈물인지 체크
        int isSeries;
        try{
            isSeries = checkContentType(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(isSeries == 0) {
            throw new BaseException(DETAIL_INVALID_CONTENT_TYPE);
        }

        try {
            int seasonsCount = detailDao.getSeasonsCount(contentIdx);
            return seasonsCount;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkTrailer(long contentIdx) throws BaseException {
        try {
            return detailDao.checkTrailer(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetTrailerRes> getTrailers(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXISTS);
        }

        // 예고편 및 다른 영상이 존재하는지 체크
        int trailerExists;
        try {
            trailerExists = checkTrailer(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (trailerExists == 0) {
            throw new BaseException(DETAIL_TRAILER_NOT_EXISTS);
        }

        try {
            List<GetTrailerRes> getTrailerRes = detailDao.getTrailers(contentIdx);
            return getTrailerRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public int checkSeason(long contentIdx, long seasonNumber) throws BaseException {
        try {
            return detailDao.checkSeason(contentIdx, seasonNumber);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetEpisodeRes> getEpisodes(long contentIdx, long seasonNumber) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXISTS);
        }

        // 컨텐츠가 시리즈물인지 체크
        int isSeries;
        try{
            isSeries = checkContentType(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(isSeries == 0) {
            throw new BaseException(DETAIL_INVALID_CONTENT_TYPE);
        }

        // 존재하는 시즌인지 체크
        int seasonExists;
        try {
            seasonExists = checkSeason(contentIdx, seasonNumber);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if(seasonExists == 0) {
            throw new BaseException(DETAIL_SEASON_NOT_EXISTS);
        }

        try {
            List<GetEpisodeRes> getEpisodeRes = detailDao.getEpisodes(contentIdx, seasonNumber);
            return getEpisodeRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetSimilarSeriesRes> getSimilarSeries(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXISTS);
        }

        // 컨텐츠가 시리즈물인지 체크
        int isSeries;
        try{
            isSeries = checkContentType(contentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(isSeries == 0) {
            throw new BaseException(DETAIL_INVALID_CONTENT_TYPE);
        }

        // 비슷한 컨텐츠 가져오기
        try {
            // TODO: SimilarSeries에 시즌 개수를 뺀 나머지 정보 받아오기
            List<SimilarSeries> similarSeriesList = detailDao.getSimilarSeries(contentIdx);
            // 각각의 시즌 개수 가져오기
            List<GetSimilarSeriesRes> getSimilarSeriesRes = new ArrayList<GetSimilarSeriesRes>();

            long similarSeriesIdx; // 비슷한 시리즈들의 contentIdx
            int seasonsCountPerSeries; // 각 시리즈의 시즌 개수
            for(int i = 0; i < similarSeriesList.size(); i++) {
                similarSeriesIdx = similarSeriesList.get(i).getContentIdx();

                seasonsCountPerSeries = detailDao.getSeasonsCount(similarSeriesIdx); // 시즌 개수 받아오기

                getSimilarSeriesRes.add(new GetSimilarSeriesRes(seasonsCountPerSeries, similarSeriesList.get(i).getContentIdx(),
                        similarSeriesList.get(i).getTitle(), similarSeriesList.get(i).getAgeRate(),similarSeriesList.get(i).getSummary(),
                        similarSeriesList.get(i).getType(), similarSeriesList.get(i).getThumbnailUrl(),
                        similarSeriesList.get(i).getPercentage(), similarSeriesList.get(i).getProductionYear()));

            }
            return getSimilarSeriesRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkContentMovie(long contentIdx) throws BaseException {
        try {
            return detailDao.checkContentMovie(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetSimilarMovieRes> getSimilarMovies(long contentIdx) throws BaseException {
        // 존재하는 컨텐츠인지 체크
        int contentExists;
        try {
            contentExists = checkContent(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (contentExists == 0) {
            throw new BaseException(DETAIL_CONTENT_NOT_EXISTS);
        }

        // 영화인지 체크
        int isMovie;
        try {
            isMovie = checkContentMovie(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if(isMovie == 0) {
            throw new BaseException(DETAIL_TYPE_NOT_MOVIE);
        }

        try {
            List<GetSimilarMovieRes> getSimilarMovieRes = detailDao.getSimilarMovies(contentIdx);
            return getSimilarMovieRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetContentByActorRes> getContentsByActor(String actorName) throws BaseException {
        int actorExists;
        try {
            // 현재 공개된 컨텐츠에 출연한 해당 이름의 배우가 존재하는지 체크
            actorExists = checkActor(actorName);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if(actorExists == 0) {
            throw new BaseException(CONTENT_BY_ACTOR_FAIL);
        }

        try {
            List<GetContentByActorRes> getContentByActorRes = detailDao.getContentsByActor(actorName);
            return getContentByActorRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkActor(String actorName) throws BaseException {
        try {
            return detailDao.checkActor(actorName);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
