package com.example.demo.src.browse.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetGenreSeriesRes {
    private long contentIdx;
    private String title;
    private long ageRate;
    private String type; // "M": 영화, "S": 시리즈
    private String thumbnailUrl;
    private String previewUrl;
    // private int seasonCount; // 공개된 시리즈 개수 (상세 정보 API 에 존재)
    private String percentage;
    private String newEpisode; // 새로운 에피소드 공개 여부
}
