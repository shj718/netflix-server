package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostMembershipReq {
    private long userIdx;
    private String membershipType;
    private String cardNumber;
    private String name;
}
