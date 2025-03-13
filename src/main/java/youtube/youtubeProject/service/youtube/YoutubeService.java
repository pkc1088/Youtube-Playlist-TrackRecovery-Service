package youtube.youtubeProject.service.youtube;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface YoutubeService {
//    public String TestAddVideoToPlaylist(String accessToken, String playlistId, String videoId);
    String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId, long videoPosition);

    // 굳이 interface에 선언해야할 이유가 있나 지금
    String deleteFromPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId);
    List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException;

    //    public Map<String, String> getVideosFromPlaylist(String playlistId) throws IOException;
    List<String> getVideosFromPlaylist(String playlistId) throws IOException;
    List<Video> initiallyAddVideoDetails(String playlistId) throws IOException;
    Video getVideoDetails(String videoId) throws IOException;
    String searchVideo(String query) throws IOException;
    String memberRegister(String userId, String userPwd, String userName);
    void fileTrackAndRecover(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                                    String playlistId) throws IOException;
    void tokenTest(OAuth2AuthorizedClient authorizedClient);

    //void scheduledTest();
}
