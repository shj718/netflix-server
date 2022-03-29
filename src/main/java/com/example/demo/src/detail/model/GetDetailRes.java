package com.example.demo.src.detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetDetailRes {
    private long contentIdx;
    private String title;
    private long ageRate;
    private String productionYear; // 제작 년도
    private String summary;
    private String type; // "M": 영화, "S": 시리즈
    private String mainImageUrl;
    private String previewUrl;
    private String runningTime; // for 영화
    private String percentage;
    private String logoImageUrl;
}
