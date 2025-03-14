package youtube.youtubeProject.repository.youtube;

import youtube.youtubeProject.domain.Music;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface YoutubeRepository {
    List<Music> findAllMusicByPlaylistId(String playlistId);
    Optional<Music> getMusicFromDBThruMusicId(String videoIdToDelete);
    void dBTrackAndRecover(String videoIdToDelete, Music videoToRecover);
    void addUpdatePlaylist(String playlistId, Music music);
    void deleteUpdatePlaylist(String playlistId, String videoId);




//    OAuth2AuthorizedClient authorizedClient 필요 없음 (서비스에서 처리했고 Repository는 내 로컬 DB에 저장하는거니까)
//    String addVideoToPlaylist(String playlistId, String videoId, Music music);
//    String deleteFromPlaylist(String playlistId, String videoId);
//    List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException;
//    List<String> getVideosFromPlaylist(String playlistId) throws IOException;
//    String searchVideo(String query) throws IOException;
//    String memberRegister(String userId, String userPwd, String userName);
//    Video getVideoDetails(String videoId) throws IOException;
//    public void fileTrackAndRecover(String videoIdToDelete, String videoTitleToDelete, Music videoToRecover);
}
