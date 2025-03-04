package youtube.youtubeProject.controller;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import youtube.youtubeProject.service.YoutubeServiceV2;

import java.io.IOException;
import java.util.List;

@RestController
public class YoutubeControllerV2 {

    @Autowired
    private YoutubeServiceV2 youtubeService;

    @GetMapping("/youtube/{channelId}/playlists")
    public List<Playlist> getPlaylists(@PathVariable String channelId) throws IOException {
        return youtubeService.getPlaylistsByChannelId(channelId);
    }

    @GetMapping("/youtube/playlist/{playlistId}/videos")
    public List<Video> getVideos(@PathVariable String playlistId) throws IOException {
        return youtubeService.getVideosFromPlaylist(playlistId);
    }
}
