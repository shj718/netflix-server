package com.example.demo.src.browse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GetSoonRes {
    private long contentIdx;
    private String title;
    private String releaseDate; // current time 이랑 비교해서 current time보다 큰 콘텐츠들을 select
    private String type; // "M": 영화, "S": 시리즈
    private String thumbnailUrl;
    private String previewUrl;
}
