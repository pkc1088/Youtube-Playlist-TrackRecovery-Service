package youtube.youtubeProject.repository.playlists;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Playlists;

public interface SdjPlaylistRepository extends JpaRepository<Playlists, String> {
    Playlists findByPlaylistId(String playlistId);
}
