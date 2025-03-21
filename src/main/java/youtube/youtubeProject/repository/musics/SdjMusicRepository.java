package youtube.youtubeProject.repository.musics;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.domain.Playlists;

import java.util.List;
import java.util.Optional;

public interface SdjMusicRepository extends JpaRepository<Music, Long> {

    List<Music> findByPlaylist_PlaylistId(String playlistId);

    Optional<Music> findByVideoId(String videoId);

    void deleteByVideoId(String videoId);

}
