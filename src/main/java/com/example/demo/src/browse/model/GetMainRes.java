package com.example.demo.src.browse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMainRes {
    private long contentIdx;
    private String title;
    private long ageRate;
    private String summary;
    private String type;
    private String thumbnailUrl;
    private String previewUrl;
    private String logoImageUrl;
}
