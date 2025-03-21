package youtube.youtubeProject.repository.playLists;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.Playlists;

@Repository
@Transactional
@RequiredArgsConstructor
public class PlayListsRepositoryV1 implements PlayListsRepository {

    private final SdjPlayListsRepository repository;


    @Override
    public void save(Playlists playlist) {
        repository.save(playlist);
    }

    @Override
    public Playlists findByPlaylistId(String playlistId) {
        return repository.findByPlaylistId(playlistId);
    }
}
