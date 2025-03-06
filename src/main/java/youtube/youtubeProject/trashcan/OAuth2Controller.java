package youtube.youtubeProject.trashcan;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import java.io.IOException;

//@Controller
public class OAuth2Controller{// implements AuthenticationSuccessHandler {

    /*@Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    // OAuth2 인증 후 리디렉션 주소에 대한 처리
    @GetMapping("/login/oauth2/code/google")
    public String oauth2LoginCallback(OAuth2AuthenticationToken authentication) {
        OAuth2User user = authentication.getPrincipal();
        String accessToken = ((OAuth2AccessToken) authentication.getCredentials()).getTokenValue();
        // Use the access token to make API calls, e.g., add video to playlist
        return "redirect:/addVideoToPlaylist?playlistId=PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74&videoId=XPtu0i3Mjag";
        //return "redirect:/addVideoToPlaylist";//"redirect:/home";  // 리디렉션 처리
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        // 사용자 정보 출력 (예: 이름, 이메일 등)
        String userName = user.getAttribute("name");
        String userEmail = user.getAttribute("email");

        // 로그인 후 리디렉션 URL로 전달할 모델 데이터 설정
        Model model = new ExtendedModelMap();
        model.addAttribute("userName", userName);
        model.addAttribute("userEmail", userEmail);

        // OAuth2 인증을 처리한 후 리디렉션
        // 예시: 구글 API를 이용해 추가 데이터 처리 및 YouTube API 호출 등
        System.out.println("User Info: " + userName + ", " + userEmail);

        // 필요한 경우 외부 서비스 호출하여 리디렉션하거나, 메인 페이지로 리디렉션
        model.addAttribute("message", "Welcome to the YouTube Project!");
        return;  // 홈 화면으로 리디렉션
    }*/
}
