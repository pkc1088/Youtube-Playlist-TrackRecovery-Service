package youtube.youtubeProject.trashcan;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import youtube.youtubeProject.trashcan.YoutubeServiceV3_5;

import java.io.IOException;
import java.util.List;

//@RestController
public class YoutubeControllerV3_5 {

//    @Autowired
    private YoutubeServiceV3_5 youtubeService;

//    @GetMapping("/addVideoToPlaylist")
    public String addVideoToPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, @RequestParam String playlistId, @RequestParam String videoId) {
        return youtubeService.addVideoToPlaylist(authorizedClient, playlistId, videoId);
    }

//    @GetMapping("/youtube/{channelId}/playlists")
    public List<Playlist> getPlaylists(@PathVariable String channelId) throws IOException {
        return youtubeService.getPlaylistsByChannelId(channelId);
    }

//    @GetMapping("/youtube/playlist/{playlistId}/videos")
    public List<Video> getVideos(@PathVariable String playlistId) throws IOException {
        return youtubeService.getVideosFromPlaylist(playlistId);
    }

//    @GetMapping("/youtube/search")
    public ResponseEntity<String> searchVideo(@RequestParam String keyword) throws IOException {
        String result = youtubeService.searchVideo(keyword);
        return ResponseEntity.ok(result);
    }
}