package com.example.demo.src.browse.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetGenreMovieRes {
    private long contentIdx;
    private String title;
    private long ageRate;
    private String type; // "M": 영화, "S": 시리즈
    private String thumbnailUrl;
    private String previewUrl;
    private String runningTime; // 상영 시간
    private String percentage;
}
