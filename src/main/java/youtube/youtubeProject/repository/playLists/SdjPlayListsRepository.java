package youtube.youtubeProject.repository.playLists;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Playlists;

public interface SdjPlayListsRepository extends JpaRepository<Playlists, String> {
    Playlists findByPlaylistId(String playlistId);
}
