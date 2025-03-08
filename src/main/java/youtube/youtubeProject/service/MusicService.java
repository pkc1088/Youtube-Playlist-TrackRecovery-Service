package youtube.youtubeProject.service;

import youtube.youtubeProject.domain.NormalMusic;
import java.util.List;
import java.util.Optional;

public interface MusicService {

    NormalMusic save(NormalMusic music);

    void update(String videoId);

    Optional<NormalMusic> findById(String videoId);

    List<NormalMusic> findItems(String videoId);
}