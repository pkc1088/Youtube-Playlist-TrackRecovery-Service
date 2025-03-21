package youtube.youtubeProject.service.playlists;

import youtube.youtubeProject.domain.Playlists;

import java.io.IOException;
import java.util.Set;

public interface PlaylistService {
    Set<Playlists> getPlaylistsByUserId(String userId);
    void registerPlaylists(String userId) throws IOException;
}
