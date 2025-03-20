package youtube.youtubeProject.repository.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Playlist;

public interface SdjPlaylistRepository extends JpaRepository<Playlist, String> {
}
