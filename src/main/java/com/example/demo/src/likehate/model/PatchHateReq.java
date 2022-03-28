package com.example.demo.src.likehate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchHateReq {
    private long userIdx;
    private long profileIdx;
    private long contentIdx;
}
