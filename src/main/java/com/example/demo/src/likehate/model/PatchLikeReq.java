package com.example.demo.src.likehate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchLikeReq {
    private long userIdx;
    private long profileIdx;
    private long contentIdx;
}
