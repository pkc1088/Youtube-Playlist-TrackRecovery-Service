package youtube.youtubeProject.trashcan;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import youtube.youtubeProject.trashcan.YoutubeServiceV2;

import java.io.IOException;
import java.util.List;

//@RestController
public class YoutubeControllerV2 {

//    @Autowired
    private YoutubeServiceV2 youtubeService;

    //@GetMapping("/youtube/{channelId}/playlists")
    public List<Playlist> getPlaylists(@PathVariable String channelId) throws IOException {
        return youtubeService.getPlaylistsByChannelId(channelId);
    }

    //@GetMapping("/youtube/playlist/{playlistId}/videos")
    public List<Video> getVideos(@PathVariable String playlistId) throws IOException {
        return youtubeService.getVideosFromPlaylist(playlistId);
    }

    //@GetMapping("/youtube/search")
    public ResponseEntity<String> searchVideo(@RequestParam String keyword) throws IOException {
        String result = youtubeService.searchVideo(keyword);
        return ResponseEntity.ok(result);
    }
    //UC6SN0-0k6z1fj5LmhYHd5UA
}
