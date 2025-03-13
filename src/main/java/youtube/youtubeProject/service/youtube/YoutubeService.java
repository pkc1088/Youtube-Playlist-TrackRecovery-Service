package youtube.youtubeProject.service.youtube;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface YoutubeService {
    //void fileTrackAndRecover(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, String playlistId) throws IOException;
    //String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId, long videoPosition);
    //String deleteFromPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId);
    List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException;
    List<String> getVideosFromPlaylist(String playlistId) throws IOException;
    List<Video> initiallyAddVideoDetails(String playlistId) throws IOException;
    //Video getVideoDetails(String videoId) throws IOException; 여기에 선언할 이유는 없다?
    //String searchVideo(String query) throws IOException;

    void fileTrackAndRecover(String userEmail, String playlistId) throws IOException; // 이건 시스템 내부에서 호출해야함 스케쥴러가
//    void TestAddVideoToPlaylist(String userEmail, String playlistId, String videoId); 선언할 이유는 없다?
//    void TestDeleteFromPlaylist(String accessToken, String playlistId, String videoId); 선언할 이유는 없다?



//    void tokenTest(OAuth2AuthorizedClient authorizedClient);
//    String memberRegister(String userId, String userPwd, String userName);
//    void scheduledTest();
}
