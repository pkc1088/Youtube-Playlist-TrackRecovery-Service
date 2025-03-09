package youtube.youtubeProject.service;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.io.IOException;
import java.util.List;

public interface YoutubeService {
    public String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId);
    public String deleteFromPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId);
    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException;
    public List<String> getVideosFromPlaylist(String playlistId) throws IOException;
    public List<Video> initiallyAddVideoDetails(String playlistId) throws IOException;
    public Video getVideoDetails(String videoId) throws IOException;
    public String searchVideo(String query) throws IOException;
}
