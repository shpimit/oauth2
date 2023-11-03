package vmsa.oauth2.web;

import com.google.gson.Gson;
import vmsa.oauth2.config.CommonConfig;
import vmsa.oauth2.vo.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
@RestController
//@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final Gson gson;
    private final RestTemplate restTemplate;
    private final CommonConfig commonConfig;

    /**
     * 인증 서버로부터 Token을 발급 받는 로직
     **/
    @GetMapping(value = "/oauth2/callback")
    public OAuthToken accessToken(@RequestParam String code) {

        // clientId와 clientSecret 으로 신원을 구분하는데, 필자는 해당 값을 config 로 빼놓았다.
        // 뒤에 application.yml 파일에서 확인하자.
        String credentials = commonConfig.getClientId() + ":" + commonConfig.getClientSecret();
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);

        // 인증 서버로부터 Access Token 을 발급 받기 위해 필요한 파라미터 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code); // 승인 코드
        params.add("grant_type", "authorization_code"); // 권한 코드 승인 방식을 사용
        params.add("redirect_uri", "http://localhost:8080/oauth2/callback");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Access Token 발급 요청
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/oauth/token", request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return gson.fromJson(response.getBody(), OAuthToken.class);
        }
        return null;
    }


    /**
     * refresh Token 을 사용하여 새 Token을 발급 받는 로직
     * 권한 코드 승인 방식에서 사용한다.
     **/
    @GetMapping(value = "/oauth2/token/refresh")
    public OAuthToken refreshToken(@RequestParam String refreshToken) {
//        String credentials = clientId + ":" + clientSecret;
        String credentials = commonConfig.getClientId() + ":" + commonConfig.getClientSecret();
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/oauth/token", httpEntity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return gson.fromJson(responseEntity.getBody(), OAuthToken.class);
        }
        return null;
    }
}