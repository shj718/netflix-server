package com.example.demo.src.browse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMainReq {
    private long userIdx;
    private long profileIdx;
    private String browseType;
}
