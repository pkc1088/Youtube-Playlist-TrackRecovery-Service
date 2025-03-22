package youtube.youtubeProject.repository.playlists;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Playlists;

import java.util.List;

public interface SdjPlaylistRepository extends JpaRepository<Playlists, String> {

    Playlists findByPlaylistId(String playlistId);

    List<Playlists> findByUser_UserId(String userId);

}
