package youtube.youtubeProject.trashcan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

//@Service
public class YoutubeServiceV3_1 {

    //@Autowired
    //private OAuth2AuthorizedClientService authorizedClientService;

    //private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true";

//    public String getYouTubeChannelInfo(String username) {
//        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("google", username);
//
//        // 액세스 토큰을 가져옵니다.
//        String accessToken = authorizedClient.getAccessToken().getTokenValue();
//
//        // YouTube API에 액세스하여 채널 정보를 가져옵니다.
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        String response = restTemplate.exchange(YOUTUBE_API_URL, HttpMethod.GET, entity, String.class).getBody();
//
//        return response;
//    }
}
