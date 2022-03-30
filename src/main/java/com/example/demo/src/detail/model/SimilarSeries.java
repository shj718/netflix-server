package com.example.demo.src.detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimilarSeries {
    private long contentIdx;
    private String title;
    private long ageRate;
    private String summary;
    private String type;
    private String thumbnailUrl;
    private String percentage;
    private String productionYear;
}
