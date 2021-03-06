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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class BrowseProvider {

    private final BrowseDao browseDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BrowseProvider(BrowseDao browseDao, JwtService jwtService) {
        this.browseDao = browseDao;
        this.jwtService = jwtService;
    }



    public GetMainRes getMain(String browseType) throws BaseException {
        try {
            // 화면 별로 다른 Dao 호출하기
            if(browseType.equals("H")) {
                GetMainRes getMainRes = browseDao.getHomeMain();
                return getMainRes;
            }
            else { // "S"나 "M"인 경우 요청값을 DAO로 전달 후 필터링
                GetMainRes getMainRes = browseDao.getSeriesOrMovieMain(browseType);
                return getMainRes;
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    boolean checkGenreName(String genre) throws BaseException {
        try {
            int checkExist = browseDao.checkGenreName(genre);
            if(checkExist == 1)
                return true;
            else
                return false;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetGenreMovieRes> getGenreMovies(String genre) throws BaseException {
        try{
            // 의미적 validation: 존재하는 장르 이름인지부터 확인!
            boolean genreExists = checkGenreName(genre);
            if(!genreExists) {
                throw new BaseException(GENRE_NOT_EXISTS);
            }
        } catch (Exception exception){
            throw new BaseException(GENRE_NOT_EXISTS);
        }
        try {
            List<GetGenreMovieRes> getGenreMoviesRes = new ArrayList<>();

            // 스크롤 구현을 위해 여러번 호출
            for(int i = 0; i < 3; i++) {
                getGenreMoviesRes.addAll(browseDao.getGenreMovies(genre));
            }
            return getGenreMoviesRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetGenreSeriesRes> getGenreSeries(String genre) throws BaseException {
        try{
            // 의미적 validation: 존재하는 장르 이름인지부터 확인!
            boolean genreExists = checkGenreName(genre);
            if(!genreExists) {
                throw new BaseException(GENRE_NOT_EXISTS);
            }
        } catch (Exception exception){
            throw new BaseException(GENRE_NOT_EXISTS);
        }
        try {
            List<GetGenreSeriesRes> getGenreSeriesRes = new ArrayList<>();

            // 스크롤 구현을 위해 여러번 호출
            for(int i = 0; i < 3; i++) {
                getGenreSeriesRes.addAll(browseDao.getGenreSeries(genre));
            }
            return getGenreSeriesRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetTopTenRes> getTopTen() throws BaseException {
        try {
            List<TopTen> topTens = browseDao.getTopTenIdx();

            // 탑10 콘텐츠들의 Idx로 실제 콘텐츠 데이터들 조회
            long topTenIdx;
            List<GetTopTenRes> getTopTensRes = new ArrayList<>();
            for(int i = 0; i < 10; i++) {
                topTenIdx = topTens.get(i).getContentIdx();
                // Dao 호출
                getTopTensRes.add(browseDao.getTopTenContents(topTenIdx));
            }

            return getTopTensRes;

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLatestRes> getLatest() throws BaseException {
        try {
            List<GetLatestRes> getLatestRes = browseDao.getLatest();
            return getLatestRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetSoonRes> getSoon() throws BaseException {
        try {
            List<GetSoonRes> getSoonRes = new ArrayList<>();

            // 스크롤 구현을 위해 여러번 호출
            for(int i = 0; i < 3; i++) {
                getSoonRes.addAll(browseDao.getSoon());
            }
            return getSoonRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetSearchRes> getSearch(String search) throws BaseException {
        try {
            // 실제로 검색한 컨텐츠
            List<GetSearchRes> getSearchOnlyRes = browseDao.getSearch(search);

            int numOfSearched = getSearchOnlyRes.size();
            // 검색한 컨텐츠와 관련된 컨텐츠
            int numOfRelated = 12 - numOfSearched;
            List<GetSearchRes> getRelatedRes = browseDao.getRelated(numOfRelated, search);

            // 두 결과 합치기
            List<GetSearchRes> getMergedRes = new ArrayList<>();
            getMergedRes.addAll(getSearchOnlyRes);
            getMergedRes.addAll(getRelatedRes);

            return getMergedRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
