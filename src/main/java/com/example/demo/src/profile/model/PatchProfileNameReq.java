package com.example.demo.src.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchProfileNameReq {
    private long userIdx;
    private long profileIdx;
    private String profileName;
}
