package youtube.youtubeProject.controller;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import youtube.youtubeProject.service.YoutubeServiceV4;
import youtube.youtubeProject.service.YoutubeServiceV5;

import java.io.IOException;
import java.util.List;

@Controller
public class YoutubeControllerV5 {

    @Autowired
    private YoutubeServiceV5 youtubeService;


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String welcomePage() {
        return "welcome";
    }

    @PostMapping("/addVideoToPlaylist") // not done
    public String addVideoToPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, @RequestParam String playlistId, @RequestParam String videoId) {
        String result =  youtubeService.addVideoToPlaylist(authorizedClient, playlistId, videoId);
        return "redirect:/";
    }

    @PostMapping("/deleteFromPlaylist") // not done
    public String deleteFromPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, @RequestParam String playlistId, @RequestParam String videoId) {
        String result = youtubeService.deleteFromPlaylist(authorizedClient, playlistId, videoId);
        return "redirect:/";
    }

    @GetMapping("{channelId}/playlists") // not done
    public String getPlaylists(@PathVariable String channelId, Model model) throws IOException {
        List<Playlist> playlists = youtubeService.getPlaylistsByChannelId(channelId);
        model.addAttribute("playlists", playlists);
        return "playlists";
    }

    @GetMapping("{playlistId}/getVideos") // not done
    public String getVideos(@PathVariable String playlistId, Model model) throws IOException {
        try {
            List<String> videos = youtubeService.getVideosFromPlaylist(playlistId);
            model.addAttribute("getVideos", videos); // 비디오 목록만 모델에 담는거임
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch videos from playlist - getvideos() method.");
            e.printStackTrace();
        }
        return "getVideos";
    }

    @GetMapping("{playlistId}/initiallyAddVideoDetails") // not done
    public String InitiallyAddVideoDetails(@PathVariable String playlistId, Model model) throws IOException {
        try {
            List<Video> videos = youtubeService.initiallyAddVideoDetails(playlistId);
            model.addAttribute("addVideos", videos); // 비디오 목록만 모델에 담는거임
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch videos from playlist - Addvideos() method.");
            e.printStackTrace();
        }
        return "addVideos";
    }

    @GetMapping("search") // not done
    public String searchVideo(@RequestParam String keyword, Model model) throws IOException {
        String result = youtubeService.searchVideo(keyword);
        model.addAttribute("result", result);
        return "search";
    }
}