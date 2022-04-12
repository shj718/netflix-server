package com.example.demo.src.oauth;

import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 추가
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.oauth.model.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexMembership;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class OauthController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final OauthService oauthService;
    @Autowired
    private final JwtService jwtService;

    /**
     * 카카오 callback (카카오 로그인시 정보를 받는 주소)
     * [GET] /oauth/kakao
     */
    @ResponseBody
    @GetMapping("/kakao")
    public BaseResponse<SocialLoginRes> kakaoCallback(@RequestParam String code) {
        System.out.println("authorization code : " + code);
        String accessToken = oauthService.getKakaoAccessToken(code);
        try {
            SocialLoginRes socialLoginRes = oauthService.createKakaoUser(accessToken);
            return new BaseResponse<>(socialLoginRes);
        } catch (BaseException exception) {
            // 에러 메세지 출력
            System.out.println("소셜 로그인 에러 발생");
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
