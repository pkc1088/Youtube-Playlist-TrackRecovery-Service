package youtube.youtubeProject.repository.playlists;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.Playlists;

@Repository
@Transactional
@RequiredArgsConstructor
public class PlaylistRepositoryV1 implements PlaylistRepository {

    private final SdjPlaylistRepository repository;


    @Override
    public void save(Playlists playlist) {
        repository.save(playlist);
    }

    @Override
    public Playlists findByPlaylistId(String playlistId) {
        return repository.findByPlaylistId(playlistId);
    }
}
