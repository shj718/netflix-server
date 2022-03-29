package com.example.demo.src.detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetGenreFeatureRes {
    private long contentIdx;
    private String genreList;
    private String featureList;
}
