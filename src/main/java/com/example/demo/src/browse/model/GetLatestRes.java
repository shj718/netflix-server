package com.example.demo.src.browse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetLatestRes {
    private long contentIdx;
    private String title;
    private long ageRate;
    private String type; // "M": 영화, "S": 시리즈
    private String thumbnailUrl;
    private String previewUrl;
    private String runningTime; // 상영 시간 (for 영화)
    private String percentage;
    private String newEpisode; // 새로운 에피소드 공개 여부 (for 시리즈)
}
