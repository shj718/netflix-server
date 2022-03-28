package com.example.demo.src.browse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchRes {
    private long contentIdx;
    private String title;
    private String releaseDate; // current time 이랑 비교해서 current time보다 크면, 날짜 반환 / 작으면 "공개된 콘텐츠"
    private long ageRate;
    private String type; // "M": 영화, "S": 시리즈
    private String thumbnailUrl;
    private String previewUrl;
    private String runningTime; // 상영 시간 (for 영화)
    private String percentage;
    private String newEpisode; // 새로운 에피소드 공개 여부 (for 시리즈)
}
