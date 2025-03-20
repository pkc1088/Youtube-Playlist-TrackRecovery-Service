package youtube.youtubeProject.repository.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.Playlist;

@Repository
@Transactional
@RequiredArgsConstructor
public class PlaylistRepositoryV1 implements PlaylistRepository{
    private final SdjPlaylistRepository repository;


    @Override
    public void save(Playlist playlist) {
        repository.save(playlist);
    }
}
