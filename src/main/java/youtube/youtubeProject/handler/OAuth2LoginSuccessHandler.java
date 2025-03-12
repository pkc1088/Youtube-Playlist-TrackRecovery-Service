package youtube.youtubeProject.handler;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.service.user.UserService;

import java.io.IOException;
import java.util.StringTokenizer;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService; // 추가


    public OAuth2LoginSuccessHandler(UserService userService, OAuth2AuthorizedClientService authorizedClientService) {
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 회원가입 성공 후 처리
        System.err.println("onAuthentication Success");

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            String refreshToken = authorizedClient.getRefreshToken() != null ? authorizedClient.getRefreshToken().getTokenValue() : null;
            String email = ((OidcUser) oauthToken.getPrincipal()).getEmail();
            // 임시 이메일 주소인지 확인
            if (isTemporaryEmail(email)) {
                System.err.println("TemporaryEmail");
                email = getRealEmail(email, oauthToken);// 실제 이메일 주소 요청 (추가 로직 필요)
            }

            /**
             * email 중복 되는지 반드시 체크해야함 나중에
             */
            saveUsersToDatabase(email, accessToken, refreshToken);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void saveUsersToDatabase(String email, String accessToken, String refreshToken) {
        // 데이터베이스에 저장하는 로직 구현
        // 예: UserRepository를 사용하여 사용자 정보와 토큰 저장
        System.out.println("Saved Email: " + email);
        System.out.println("Saved Access Token: " + accessToken);
        System.out.println("Saved Refresh Token: " + refreshToken);

        userService.saveUser(
                new Users("someId", "somePwd", "username", "userHandler",
                        email, accessToken, refreshToken));
    }

    private boolean isTemporaryEmail(String email) {
        return email != null && email.endsWith("@pages.plusgoogle.com");        // 임시 이메일 주소인지 확인
    }

    private String getRealEmail(String email, OAuth2AuthenticationToken oauthToken) {
        // 실제 이메일 주소를 요청하는 로직
        // 예: Google People API를 사용해 프로필 정보 조회
        StringTokenizer st = new StringTokenizer(email, "-");
        return st.nextToken() + "@gmail.com";
        //return oauthToken.getPrincipal().getAttribute("email");
    }


}

    /*@Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        return new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization"
        ) {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest authorizationRequest = super.resolve(request);
                if (authorizationRequest != null) {
                    // 추가 파라미터 설정
                    return OAuth2AuthorizationRequest.from(authorizationRequest)
                            .additionalParameters(params -> {
                                params.put("access_type", "offline");
                                params.put("prompt", "consent");
                            })
                            .build();
                }
                return authorizationRequest;
            }
        };
    }*/
//        if (authentication instanceof OAuth2AuthenticationToken) {
//            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//            // OAuth2 사용자 정보 추출
//            if (oauthToken.getPrincipal() instanceof OidcUser) {
//                OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
//                // Access Token 추출
//                String accessToken = oidcUser.getIdToken().getTokenValue();
//                // Refresh Token 추출 (Google의 경우 refresh_token은 별도로 요청해야 함)
//                String refreshToken = null; // Google은 refresh_token을 기본적으로 제공하지 않음
//                // 사용자 정보 추출 (예: 이메일)
//                String email = oidcUser.getEmail();
//                // 데이터베이스에 저장
//                saveTokensToDatabase(email, accessToken, refreshToken);
//            }
//        }
//        // 기본 리다이렉트 동작 수행
//        super.onAuthenticationSuccess(request, response, authentication);
