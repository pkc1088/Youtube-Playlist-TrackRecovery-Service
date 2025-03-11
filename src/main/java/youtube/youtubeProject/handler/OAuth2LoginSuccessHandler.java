package youtube.youtubeProject.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import youtube.youtubeProject.domain.User;
import youtube.youtubeProject.service.UserService;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 로그인 성공 후 처리
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            // OAuth2 사용자 정보 추출
            if (oauthToken.getPrincipal() instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
                // Access Token 추출
                String accessToken = oidcUser.getIdToken().getTokenValue();
                // Refresh Token 추출 (Google의 경우 refresh_token은 별도로 요청해야 함)
                String refreshToken = null; // Google은 refresh_token을 기본적으로 제공하지 않음
                // 사용자 정보 추출 (예: 이메일)
                String email = oidcUser.getEmail();
                // 데이터베이스에 저장
                saveTokensToDatabase(email, accessToken, refreshToken);
            }
        }
        // 기본 리다이렉트 동작 수행
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void saveTokensToDatabase(String email, String accessToken, String refreshToken) {
        // 데이터베이스에 저장하는 로직 구현
        // 예: UserRepository를 사용하여 사용자 정보와 토큰 저장
        System.out.println("Email: " + email);
        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);
//        userRepository
        userService.saveUser(
                new User("someId", "somePwd", "username", "userHandler",
                        email, accessToken, refreshToken));
    }
}