package com.example.demo.src.oauth;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

// 내가 추가
import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.src.oauth.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class OauthService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OauthDao oauthDao;
    private final JwtService jwtService;
    private final OauthProvider oauthProvider;

    @Autowired
    public OauthService(OauthDao oauthDao, JwtService jwtService, OauthProvider oauthProvider) {
        this.oauthDao = oauthDao;
        this.jwtService = jwtService;
        this.oauthProvider = oauthProvider;
    }


    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=aa236eb12e32e4166338c596e8c5b1a9"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:9000/oauth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    // TODO: Transaction 처리

    public SocialLoginRes createKakaoUser(String token) throws BaseException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            // TODO: 이 밑의 id 부분 내가 int형을 long형으로 변경함. 이렇게 해도 되는지 체크하자!
            long id = element.getAsJsonObject().get("id").getAsLong();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            // TODO: 저 id는 카카오톡에서의 이 유저 식별자이므로 아이디,비번과 동일한 힘을 가짐. 이제 이 유저를 이 이메일로 회원가입시킨 후, userIdx랑 JWT를 리턴해주기
            // TODO: 음 회원가입 안시키고 Jwt만 주는건가? 아닌데 회원가입시키는거 맞음. 근데 얘는 그럼 다음번에 로그인할땐 뭘로 로그인해? 또 소셜로그인.
            // TODO: 이때는 저 이메일이 가입되어 있는 이메일이므로 그냥 바로 userIdx랑 Jwt 줌 (바로 가입시키는게 아니라 User 도메인의 checkEmail로 중복 확인하자!)
            long userIdx;
            String jwt;
            int hasMembership;
            try {
                int emailExists = oauthProvider.checkEmail(email);
                // TODO: emailExists==0이면 회원가입(User테이블에 insert 후, userIdx랑 jwt 리턴), ==1이면 로그인(insert안하고 걍 userIdx랑 jwt 리턴).

                if(emailExists == 0) {
                    userIdx = oauthDao.createKakaoUser(email, id); // id를 비번 칼럼에 넣으면 되겠네!!
                }
                else { // 이미 회원인 경우. 로그인 시켜주기. getUserIdx로 userIdx 가져오기.
                    userIdx = oauthProvider.getKakaoUserIdx(email);
                }
                // TODO: 원래 얘를 리턴해주고 Controller 에서도 이걸 리턴해줘야 하지만 우리는 일단 print 하자.
                // TODO: 프린트 성공하면 리턴하고 포스트맨으로 테스트해보자. (포스트맨에 카카오톡 로그인 Url 치면 HTML파일 날라옴)
                System.out.println("userIdx : " + userIdx);
                // TODO: JWT 발급하고 걔도 print 하기!
                jwt = jwtService.createJwt(userIdx);
                System.out.println("jwt : " + jwt);
                // TODO: 이 유저가 status 가 'A'인 멤버십을 가지고 있는지 여부 체크
                hasMembership = oauthDao.checkMembership(email);
                System.out.println("has Membership : " + hasMembership);

            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }



            System.out.println("id : " + id);
            System.out.println("email : " + email);

            br.close();

            // TODO: 이제 void 형이 아닌 return 값이 있는 함수로 바꿔보자!
            return new SocialLoginRes(userIdx, jwt, hasMembership);

        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(KAKAO_SOCIAL_LOGIN_ERROR);
        }
    }
}
