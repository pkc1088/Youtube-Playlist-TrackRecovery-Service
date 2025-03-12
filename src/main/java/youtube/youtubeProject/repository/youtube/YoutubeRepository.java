package youtube.youtubeProject.repository.youtube;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import youtube.youtubeProject.domain.Music;

import java.io.IOException;
import java.util.List;

public interface YoutubeRepository {

    // OAuth2AuthorizedClient authorizedClient 필요 없음 (서비스에서 처리했고 Repository는 내 로컬 DB에 저장하는거니까)
    public String addVideoToPlaylist(String playlistId, String videoId, Music music); /// dwdw
    public String deleteFromPlaylist(String playlistId, String videoId);
    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException;
    public List<String> getVideosFromPlaylist(String playlistId) throws IOException;
    public void initiallyAddVideoDetails(String playlistId, String videoName, String videoId, String videoUploader) throws IOException;
    public Video getVideoDetails(String videoId) throws IOException;
    public String searchVideo(String query) throws IOException;
    public String memberRegister(String userId, String userPwd, String userName);
    public String getMusicTitleFromDBThruMusicId(String videoIdToDelete);
    public void dBTrackAndRecover(String videoIdToDelete, Music videoToRecover);
    //public void fileTrackAndRecover(String videoIdToDelete, String videoTitleToDelete, Music videoToRecover);


}
