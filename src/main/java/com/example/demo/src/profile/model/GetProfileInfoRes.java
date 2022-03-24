package com.example.demo.src.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProfileInfoRes {
    private long profileIdx;
    private long userIdx;
    private String profileName;
    private String profileImageUrl;
    private long ageRate;
    private String lockPin;
    private String status;
}
