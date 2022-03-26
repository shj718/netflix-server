package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexMembership;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;



    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 회원 가입 API
     * [POST] /users/signup
     * @return BaseResponse<Long>
     */
    // Body
    @ResponseBody
    @PostMapping("/signup")
    public BaseResponse<Long> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요! (password, membership)
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postUserReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            Long userIdx = userService.createUser(postUserReq);
            return new BaseResponse<>(userIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 신규 회원 결제 정보 및 멤버십 등록 API (회원가입, 로그인 두 경우 모두 사용하므로 Jwt 적용 X)
     * [POST] /users/payment
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("/payment")
    public BaseResponse<Long> createPayment(@RequestBody PostPaymentReq postPaymentReq) {
        try {
            if(postPaymentReq.getMembershipType() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_MEMBERSHIP); // 베이식은 'B', 스탠다드는 'S', 프리미엄은 'P' 받기
            }
            //멤버십 정규 표현
            if(!isRegexMembership(postPaymentReq.getMembershipType()) || postPaymentReq.getMembershipType().length() != 1) {
                return new BaseResponse<>(POST_USERS_INVALID_MEMBERSHIP);
            }
            if(postPaymentReq.getCardNumber() == null) {
                return new BaseResponse<>(PAYMENT_EMPTY_CARD_NUMBER);
            }
            if(postPaymentReq.getName() == null) {
                return new BaseResponse<>(PAYMENT_EMPTY_NAME);
            }
            long membershipIdx = userService.createPayment(postPaymentReq);
            return new BaseResponse<>(membershipIdx);
        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
            if(postLoginReq.getEmail() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if(postLoginReq.getPassword() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            //이메일 정규표현
            if(!isRegexEmail(postLoginReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            // 멤버십 상태 조회 프로바이더에서 진행
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 기존 회원 멤버십 등록(+ 결제 정보 등록) API (회원가입은 되어있지만, 멤버십이 해지된 경우, 로그인한 유저가 등록 가능)
     * [POST] /users/membership
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("/membership")
    public BaseResponse<Long> createMembership(@RequestBody PostMembershipReq postMembershipReq) {
        try {
            if(postMembershipReq.getMembershipType() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_MEMBERSHIP);
            }
            //멤버십 정규 표현
            if(!isRegexMembership(postMembershipReq.getMembershipType()) || postMembershipReq.getMembershipType().length() != 1) {
                return new BaseResponse<>(POST_USERS_INVALID_MEMBERSHIP);
            }
            if(postMembershipReq.getCardNumber() == null) {
                return new BaseResponse<>(PAYMENT_EMPTY_CARD_NUMBER);
            }
            if(postMembershipReq.getName() == null) {
                return new BaseResponse<>(PAYMENT_EMPTY_NAME);
            }
            long userIdx = postMembershipReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Long membershipIdx = userService.createMembership(postMembershipReq);
            return new BaseResponse<>(membershipIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 멤버십 정보 조회 API
     * [GET] /users/membership
     * @return BaseResponse<GetMembershipRes>
     */
    @ResponseBody
    @GetMapping("/membership")
    public BaseResponse<GetMembershipRes> getMembership(@RequestParam long userIdx, @RequestParam long membershipIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetMembershipRes getMembershipRes = userProvider.getMembership(membershipIdx);
            return new BaseResponse<>(getMembershipRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 계정 및 결제 정보 조회 API
     * [GET] /users/account
     * @return BaseResponse<GetUserInfoRes>
     */
    @ResponseBody
    @GetMapping("/account")
    public BaseResponse<GetUserInfoRes> getUserInfo(@RequestParam long userIdx) {
        try {
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserInfoRes getUserInfoRes = userProvider.getUserInfo(userIdx);
            return new BaseResponse<>(getUserInfoRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 이메일 가입 여부 조회 API
     * [GET] /users/check-email
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @GetMapping("/check-email")
    public BaseResponse<Integer> checkUser(@RequestParam String email) {
        try {
            //이메일 정규표현
            if(!isRegexEmail(email)){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            Integer hasAccount = userProvider.checkEmail(email);
            return new BaseResponse<>(hasAccount);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
