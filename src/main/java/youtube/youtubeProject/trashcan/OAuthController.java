package youtube.youtubeProject.trashcan;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OAuthController {
//    private final OAuth2AuthorizedClientService authorizedClientService;
//
//    public OAuthController(OAuth2AuthorizedClientService authorizedClientService) {
//        this.authorizedClientService = authorizedClientService;
//    }
//
//    @GetMapping("/oauth2/callback")
//    public String oauthCallback(@RequestParam("code") String code) {
//        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", "user");
//        OAuth2AccessToken accessToken = client.getAccessToken();
//        System.out.println("OAuth2 Access Token: " + accessToken.getTokenValue());
//        return "ok";
//    }

//    @GetMapping("/oauth2/callback")
//    public String oauthCallback(@RequestParam("code") String code) {
//        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", "user");
//        OAuth2AccessToken accessToken = client.getAccessToken();
//        System.out.println("OAuth2 Access Token: " + accessToken.getTokenValue());
//        return "redirect:/success";
//    }
}
