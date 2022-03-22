package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private long userIdx;
    private String email;
    private String password; // 복호화된 비밀번호
    private String cardNumber;
    private long membershipIdx;
    private String name;
}
