package com.example.demo.src.detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetTrailerRes {
    private long trailerIdx;
    private String title;
    private String thumbnailUrl;
    private String trailerUrl;
}
