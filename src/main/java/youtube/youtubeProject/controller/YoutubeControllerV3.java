package youtube.youtubeProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import youtube.youtubeProject.service.YoutubeServiceV3;

@RestController
public class YoutubeControllerV3 {

    @Autowired
    private YoutubeServiceV3 youtubeService;

    @PostMapping("/addVideoToPlaylist")
    public String addVideoToPlaylist(OAuth2AuthenticationToken authentication, @RequestParam String playlistId, @RequestParam String videoId) {
        try {
            youtubeService.addVideoToPlaylist(authentication, playlistId, videoId);
            return "Video added to playlist successfully!";
        } catch (Exception e) {
            return "Failed to add video to playlist: " + e.getMessage();
        }
    }

    /*@Autowired
    private YoutubeServiceV3 youtubeService;

    @GetMapping("/add-video")
    public String addVideoToPlaylist(@RequestParam String playlistId, @RequestParam String videoId, @RequestParam String userName) {
        return youtubeService.addVideoToPlaylist(playlistId, videoId, userName);
    }*/
}