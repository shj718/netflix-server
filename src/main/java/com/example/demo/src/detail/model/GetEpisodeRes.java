package com.example.demo.src.detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetEpisodeRes {
    private long episodeIdx;
    private String summary;
    private String runningTime;
    private long seasonNumber;
    private long episodeNumber;
    private String thumbnailUrl;
    private String episodeTitle;
    private String episodeReleaseDate; // 에피소드 공개일이 현재 시간보다 나중이면(공개 예정 에피소드), 날짜(n월 m일) 반환 / 이미 공개된 에피소드면,  "공개된 에피소드".
}
