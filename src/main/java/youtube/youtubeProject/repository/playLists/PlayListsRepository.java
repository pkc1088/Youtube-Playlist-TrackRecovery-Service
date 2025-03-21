package youtube.youtubeProject.repository.playLists;

import youtube.youtubeProject.domain.Playlists;

public interface PlayListsRepository {
    void save(Playlists playlist);
    Playlists findByPlaylistId(String playlistId);
}
