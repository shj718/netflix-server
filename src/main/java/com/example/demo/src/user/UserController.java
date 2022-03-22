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
     * 회원 가입 및 멤버십 선택 API
     * [POST] /users/signup
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("/signup")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요! (password, membership)
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postUserReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getMembershipType() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_MEMBERSHIP); // 베이식은 'B', 스탠다드는 'S', 프리미엄은 'P' 받기
        }
        //멤버십 정규 표현
        if(!isRegexMembership(postUserReq.getMembershipType()) || postUserReq.getMembershipType().length() != 1) {
            return new BaseResponse<>(POST_USERS_INVALID_MEMBERSHIP);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
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
     * 멤버십 등록 API (회원가입은 되어있지만, 멤버십이 해지된 경우, 로그인한 유저가 등록 가능)
     * [POST] /users/membership
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/membership")
    public BaseResponse<String> createMembership(@RequestBody PostMembershipReq postMembershipReq) {
        try {
            // TODO: postMembershipReq의 userIdx가 null일 경우 형식적 validation 어떻게 하는지 문의
            if(postMembershipReq.getMembershipType() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_MEMBERSHIP);
            }
            long userIdx = postMembershipReq.getUserIdx();
            //jwt에서 idx 추출.
            long userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Long membershipIdx = userService.createMembership(postMembershipReq);
            String result = "membershipIdx : " + membershipIdx.toString();
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 결제 정보 등록 API (회원가입, 로그인 두 경우 모두 사용하므로 Jwt 적용 X 어짜피 Req로 멤버십 Idx를 받으므로 인증 가능)
     * [POST] /users/payment
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/payment")
    public BaseResponse<String> createPayment(@RequestBody PostPaymentReq postPaymentReq) {
        try {
            // TODO: userIdx가 null일 경우 형식적 validation
            if(postPaymentReq.getCardNumber() == null) {
                return new BaseResponse<>(PAYMENT_EMPTY_CARD_NUMBER);
            }
            if(postPaymentReq.getName() == null) {
                return new BaseResponse<>(PAYMENT_EMPTY_NAME);
            }
            userService.createPayment(postPaymentReq);
            String result = "카드 등록에 성공했습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception) {
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
     * [GET] /users/payment
     * @return BaseResponse<GetUserInfoRes>
     */
    @ResponseBody
    @GetMapping("/payment")
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
}
