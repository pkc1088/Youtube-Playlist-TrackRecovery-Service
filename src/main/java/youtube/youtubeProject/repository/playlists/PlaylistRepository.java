package youtube.youtubeProject.repository.playlists;

import youtube.youtubeProject.domain.Playlists;

public interface PlaylistRepository {
    void save(Playlists playlist);
    Playlists findByPlaylistId(String playlistId);
}
