package youtube.youtubeProject.repository.playlists;

import youtube.youtubeProject.domain.Playlists;
import java.util.List;

public interface PlaylistRepository {
    void save(Playlists playlist);
    Playlists findByPlaylistId(String playlistId); // 이거 어디서 씀?
    List<Playlists> findAllPlaylistsByUserId(String userId);
}
