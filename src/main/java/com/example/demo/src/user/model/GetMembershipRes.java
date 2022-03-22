package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMembershipRes {
    private long membershipIdx;
    private String membershipType;
    private String videoQuality;
    private long price;
    private long accessLimit;
    private String startTime;
    private String endTime;
    private String status;
}
