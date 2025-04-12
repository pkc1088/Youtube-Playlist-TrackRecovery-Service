package youtube.youtubeProject.service.playlists;

import com.google.api.services.youtube.model.Playlist;
import youtube.youtubeProject.domain.Playlists;

import java.io.IOException;
import java.util.List;

public interface PlaylistService {
    List<Playlists> getPlaylistsByUserId(String userId);
    List<Playlist> getAllPlaylists(String userId) throws IOException;
    void registerPlaylists(String userId, List<String> selectedPlaylistIds) throws IOException;
}
