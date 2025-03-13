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

        System.err.println("onAuthentication Success");
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            String refreshToken = authorizedClient.getRefreshToken() != null ? authorizedClient.getRefreshToken().getTokenValue() : null;
            String email = ((OidcUser) oauthToken.getPrincipal()).getEmail();

            if (isTemporaryEmail(email)) {// 임시 이메일 주소인지 확인
                System.err.println("TemporaryEmail");
                email = getRealEmail(email, oauthToken);// 실제 이메일 주소 요청 (추가 로직 필요)
            }

            if(true) {
                System.out.println("got ace : " + accessToken);
                System.out.println("got ref : " + refreshToken); // ~19AyboNhX0o
                System.out.println("got nam : " + oauthToken.getPrincipal().getName()); // 112735690496635663877
                System.out.println("got rID : " + oauthToken.getAuthorizedClientRegistrationId()); // google
                System.out.println("got ema : " + oauthToken.getPrincipal().getAttribute("email"));
                System.out.println("getFullName : " + ((OidcUser) oauthToken.getPrincipal()).getFullName());
                System.out.println("getProfile : " + ((OidcUser) oauthToken.getPrincipal()).getProfile());
                System.out.println("getIdToken : " + ((OidcUser) oauthToken.getPrincipal()).getIdToken());
            } // 정보 출력

            /* email 중복 되는지 반드시 체크 */
            if(alreadyMember(email)) { // registered member
                updateRefreshTokenByLogin(email, accessToken, refreshToken);
            } else { // new member
                saveUsersToDatabase(email, accessToken, refreshToken);
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private boolean alreadyMember(String email) {
        try{
            if(email.equals(userService.getUserByEmail(email).getUserEmail())) {
                System.err.println("Registered Member");
                return true;
            }
        } catch (RuntimeException e) {
            System.err.println("New member");
        }
        return false;
    }

    private void updateRefreshTokenByLogin(String email, String accessToken, String refreshToken) {
        System.out.println("old member Email: " + email);
        System.out.println("old member Access Token: " + accessToken);
        System.out.println("old member Refresh Token: " + refreshToken);
        userService.updateRefreshTokenByLogin(email, refreshToken); // login 시 refreshToken 업데이트
    }

    private void saveUsersToDatabase(String email, String accessToken, String refreshToken) {
        System.out.println("new member Email: " + email);
        System.out.println("new member Access Token: " + accessToken);
        System.out.println("new member Refresh Token: " + refreshToken);
        userService.saveUser(
                new Users("someId", "somePwd", "username", "userHandler",
                        email, accessToken, refreshToken));
    }

    private boolean isTemporaryEmail(String email) {
        return email != null && email.endsWith("@pages.plusgoogle.com");        // 임시 이메일 주소인지 확인
    }

    private String getRealEmail(String email, OAuth2AuthenticationToken oauthToken) {
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
