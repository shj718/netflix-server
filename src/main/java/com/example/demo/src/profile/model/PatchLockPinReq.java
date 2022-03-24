package com.example.demo.src.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchLockPinReq {
    private long userIdx;
    private long profileIdx;
    private String profileLockPin;
}
