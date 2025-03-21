package youtube.youtubeProject.service.playListsService;

import youtube.youtubeProject.domain.Playlists;

import java.io.IOException;
import java.util.Set;

public interface PlayListsService {
    Set<Playlists> getPlaylistsByUserId(String userId);
    void registerPlaylists(String userId) throws IOException;
}
