package youtube.youtubeProject.service.youtube;

import com.google.api.services.youtube.model.Video;
import youtube.youtubeProject.domain.Playlist;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface YoutubeService {

    Set<Playlist> getPlaylistsByUserId(String userId);
    void registerPlaylists(String userId) throws IOException ;
    void initiallyAddVideoDetails(String playlistId) throws IOException; // for user display
    void fileTrackAndRecover(String userEmail, String playlistId) throws IOException; // service version 1
    void updatePlaylist(String playlistId) throws IOException; // service version 2


//    Video getVideoDetails(String videoId) throws IOException; 여기에 선언할 이유는 없다?
//    String searchVideo(String query) throws IOException;
//    void fileTrackAndRecover(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, String playlistId) throws IOException;
//    String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId, long videoPosition);
//    String deleteFromPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId);
//    void TestAddVideoToPlaylist(String userEmail, String playlistId, String videoId); 선언할 이유는 없다?
//    void TestDeleteFromPlaylist(String accessToken, String playlistId, String videoId); 선언할 이유는 없다?
//    void tokenTest(OAuth2AuthorizedClient authorizedClient);
//    String memberRegister(String userId, String userPwd, String userName);
//    void scheduledTest();

}
