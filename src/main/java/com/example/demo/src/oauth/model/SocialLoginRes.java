package com.example.demo.src.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocialLoginRes {
    private long userIdx;
    private String jwt;
    private int hasMembership; // 유효한 멤버십 있으면 1, 없으면 0
}
