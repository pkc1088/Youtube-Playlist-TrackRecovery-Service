package youtube.youtubeProject.repository.youtube;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import youtube.youtubeProject.domain.Music;

import java.io.IOException;
import java.util.List;

public interface YoutubeRepository {

    // OAuth2AuthorizedClient authorizedClient 필요 없음 (서비스에서 처리했고 Repository는 내 로컬 DB에 저장하는거니까)
    String addVideoToPlaylist(String playlistId, String videoId, Music music);
    String deleteFromPlaylist(String playlistId, String videoId);
    List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException;
    List<String> getVideosFromPlaylist(String playlistId) throws IOException;
    void initiallyAddVideoDetails(String playlistId, String videoName, String videoId, String videoUploader) throws IOException;
    Video getVideoDetails(String videoId) throws IOException;
    String searchVideo(String query) throws IOException;
    String memberRegister(String userId, String userPwd, String userName);
    String getMusicTitleFromDBThruMusicId(String videoIdToDelete);
    void dBTrackAndRecover(String videoIdToDelete, Music videoToRecover);

    //public void fileTrackAndRecover(String videoIdToDelete, String videoTitleToDelete, Music videoToRecover);


}
