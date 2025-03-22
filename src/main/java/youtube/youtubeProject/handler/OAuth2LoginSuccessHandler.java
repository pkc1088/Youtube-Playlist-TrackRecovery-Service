package youtube.youtubeProject.handler;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import lombok.extern.slf4j.Slf4j;
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
import youtube.youtubeProject.service.users.UserService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.StringTokenizer;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2LoginSuccessHandler(UserService userService, OAuth2AuthorizedClientService authorizedClientService) {
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("onAuthentication Success");
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            String userId = oauthToken.getPrincipal().getName();    // 112735690496635663877, 107155055893692546350

            if(alreadyMember(userId)) {
                log.info("you are already a member of this service");

                // added 25.03.22 ~
                Users user = userService.getUserByUserId(userId);
                String updatedRefreshToken = authorizedClient.getRefreshToken().getTokenValue();
                if(updatedRefreshToken != null && !user.getRefreshToken().equals(updatedRefreshToken)) {
                    user.setRefreshToken(updatedRefreshToken);
                    log.info("refreshToken Updated");
                }
                // ~ done
            } else {
                String fullName = ((OidcUser) oauthToken.getPrincipal()).getFullName(); // pkc1088, whistle_missile 등
                String channelId;
                try {
                    channelId = getChannelIdByUserId(accessToken);
                } catch (GeneralSecurityException e) { throw new RuntimeException(e); }
                String email = ((OidcUser) oauthToken.getPrincipal()).getEmail();
                if (isTemporaryEmail(email)) {
                    log.info("Temporary Email");
                    email = getRealEmail(email, oauthToken);
                }
                String refreshToken = authorizedClient.getRefreshToken() != null ? authorizedClient.getRefreshToken().getTokenValue() : null;

                saveUsersToDatabase(userId, fullName, channelId, email, refreshToken); // new member
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private boolean alreadyMember(String userId) {
        try{
            if(userId.equals(userService.getUserByUserId(userId).getUserId())) {
                log.info("Registered Member");
                return true;
            }
        } catch (RuntimeException e) {
            log.info("New Member");
        }
        return false;
    }

    public void saveUsersToDatabase(String id, String fullName, String channelId, String email, String refreshToken) {
        log.info("new member Id: {}", id);
        log.info("new member Name: {}", fullName);
        log.info("new member Email: {}", email);
        log.info("new member ChannelId: {}", channelId);
        log.info("new member Refresh Token: {}", refreshToken);
        userService.saveUser(new Users(id, fullName, channelId, email, refreshToken));
    }

    public String getChannelIdByUserId(String accessToken) throws IOException, GeneralSecurityException {
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        YouTube youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("youtube-channel-info").build();

        ChannelListResponse response = youtube.channels().list(Collections.singletonList("snippet"))
                                        .setMine(true).execute();   // 현재 인증된 사용자의 채널 정보 조회
        if (response.getItems().isEmpty()) {
            throw new RuntimeException("No channel found for the authenticated user.");
        }
        Channel channel = response.getItems().get(0);
        return channel.getId();
    }

    private boolean isTemporaryEmail(String email) {
        return email != null && email.endsWith("@pages.plusgoogle.com");        // 임시 이메일 주소인지 확인
    }

    private String getRealEmail(String email, OAuth2AuthenticationToken oauthToken) {
        StringTokenizer st = new StringTokenizer(email, "-");
        return st.nextToken() + "@gmail.com";
    }

//    private void updateRefreshTokenByLogin(String email, String refreshToken) { // 이거 안 씀, 이렇게 하면 안됨
//        System.out.println("old member Email: " + email);
//        System.out.println("old member Refresh Token: " + refreshToken);
//        userService.updateRefreshTokenByLogin(email, refreshToken); // login 시 refreshToken 업데이트
//    }
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
