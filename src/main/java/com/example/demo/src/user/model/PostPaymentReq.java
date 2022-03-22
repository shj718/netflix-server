package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPaymentReq {
    private long userIdx;
    private long membershipIdx;
    private String cardNumber;
    private String name;
}
