package youtube.youtubeProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import youtube.youtubeProject.service.YoutubeServiceV4;

@RestController
public class YoutubeControllerV4 {

    @Autowired
    private YoutubeServiceV4 youtubeService;

    @GetMapping("/addVideoToPlaylist")
    public String addVideoToPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                                     @RequestParam String playlistId, @RequestParam String videoId) {
        return youtubeService.addVideoToPlaylist(authorizedClient, playlistId, videoId);
    }
}