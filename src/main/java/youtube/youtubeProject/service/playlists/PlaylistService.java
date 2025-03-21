package youtube.youtubeProject.service.playlists;

import youtube.youtubeProject.domain.Playlists;

import java.io.IOException;
import java.util.List;

public interface PlaylistService {
    List<Playlists> getPlaylistsByUserId(String userId);
    void registerPlaylists(String userId) throws IOException;
}
