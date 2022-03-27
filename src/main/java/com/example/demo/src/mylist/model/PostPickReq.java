package com.example.demo.src.mylist.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPickReq {
    private long userIdx;
    private long profileIdx;
    private long contentIdx;
}
