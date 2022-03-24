package com.example.demo.src.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProfileRes {
    private Long profileIdx;
    private String profileName;
    private String profileImageUrl;
    private String lockPin; // "N"이면 프로필 잠금 없음.
    private String status;

}
